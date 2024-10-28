package example.net.netty.basic;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.logging.Logger;

/**
 * BasicNettyClient sets up a simple Netty client that connects to a server
 * on a specified host and port, supporting string-based communication.
 */
public class BasicNettyClient {
    private static final Logger logger = Logger.getLogger(BasicNettyClient.class.getName());
    private final String host;
    private final int port;

    /**
     * Constructor to initialize the client with a specific server host and port.
     *
     * @param host Server hostname or IP address
     * @param port Server port number
     */
    public BasicNettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Starts the Netty client, sets up the pipeline, and connects to the server.
     *
     * @throws InterruptedException if the connection is interrupted
     */
    public void start() throws InterruptedException {
        // EventLoopGroup for handling all client events including connection and data transfer
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)  // Specifies the channel type for client connections
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // Adds StringEncoder and StringDecoder for string-based communication
                            ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new ClientHandler());
                        }
                    });

            // Connects to the server and waits until the connection is completed
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("Netty client connected to server at " + host + ":" + port);

            // Waits until the client connection is closed
            future.channel().closeFuture().sync();
        } finally {
            // Gracefully shuts down the EventLoopGroup to release all resources
            group.shutdownGracefully();
        }
    }

    /**
     * Main method to start the client and connect to the server.
     *
     * @param args Command-line arguments (unused)
     * @throws InterruptedException if the connection is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        new BasicNettyClient("localhost", 8080).start();
    }
}
