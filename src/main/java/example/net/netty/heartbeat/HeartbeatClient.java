package example.net.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HeartbeatClient {
    private static final Logger logger = Logger.getLogger(HeartbeatClient.class.getName());
    private final String host;
    private final int port;

    public HeartbeatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        // EventLoopGroup to handle client I/O threads
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Set up the Bootstrap to configure client settings
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class) // Specify channel type for client
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // IdleStateHandler: triggers an idle event if the client does not write within 4 seconds
                            ch.pipeline().addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                            // Add encoder and decoder for String messages
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder());
                            // Add custom handler to manage heartbeats
                            ch.pipeline().addLast(new HeartbeatClientHandler());
                        }
                    });

            // Connect to the server and wait until connection is established
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("Connected to server at " + host + ":" + port);

            // Wait until the connection is closed
            future.channel().closeFuture().sync();
        } finally {
            // Gracefully shut down the EventLoopGroup to release resources
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Start the client with specified server address and port
        new HeartbeatClient("localhost", 8080).start();
    }
}
