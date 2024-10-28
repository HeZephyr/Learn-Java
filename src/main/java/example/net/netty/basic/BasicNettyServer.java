package example.net.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Logger;

/**
 * BasicNettyServer sets up a simple Netty server with basic configurations.
 * It listens for incoming connections on a specified port and supports
 * string-based communication through StringEncoder and StringDecoder.
 */
public class BasicNettyServer {
    private static final Logger logger = Logger.getLogger(BasicNettyServer.class.getName());
    private final int port;

    /**
     * Constructor to initialize the server with a specific port.
     *
     * @param port Port on which the server listens for incoming connections
     */
    public BasicNettyServer(int port) {
        this.port = port;
    }

    /**
     * Starts the Netty server, configures the pipeline, and handles client connections.
     *
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public void start() throws InterruptedException {
        // Boss group handles incoming connection requests; worker group handles the I/O
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  // Sets the channel type to NioServerSocketChannel
                    .handler(new LoggingHandler(LogLevel.INFO))  // Adds logging for connection events
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // Adds StringDecoder and StringEncoder for string-based communication
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)  // Sets the maximum number of pending connections
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  // Enables TCP keep-alive for client connections

            // Binds the server to the specified port and starts listening for incoming connections
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("Netty server started on port " + port);

            // Waits until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            // Gracefully shuts down the boss and worker groups
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Main method to start the server on a specific port.
     *
     * @param args Command-line arguments (unused)
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public static void main(String[] args) throws InterruptedException {
        new BasicNettyServer(8080).start();
    }
}
