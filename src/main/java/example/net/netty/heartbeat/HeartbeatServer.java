package example.net.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HeartbeatServer {
    private static final Logger logger = Logger.getLogger(HeartbeatServer.class.getName());
    private final int port;

    public HeartbeatServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        // Create the EventLoopGroup to manage I/O threads
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // Handles incoming connection requests
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // Manages data transfer once connection is established
        try {
            // Configure the server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) // Set thread groups
                    .channel(NioServerSocketChannel.class) // Specify the server channel type
                    .handler(new LoggingHandler(LogLevel.INFO)) // Add logging for server events
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Initialize the pipeline for each client
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // Add IdleStateHandler to detect client inactivity
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            // Add custom handler to manage heartbeat messages
                            ch.pipeline().addLast(new HeartbeatServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // Set max connection backlog
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // Enable keep-alive for each connection

            // Bind the server to the specified port and start it
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("Heartbeat server started on port " + port);

            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            // Shut down event loop groups gracefully to release resources
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Start the server on port 8080
        new HeartbeatServer(8080).start();
    }
}
