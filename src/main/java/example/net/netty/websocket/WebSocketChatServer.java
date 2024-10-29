package example.net.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Logger;

/**
 * WebSocketChatServer sets up and starts a WebSocket-based chat server.
 * The server listens on a specified port and uses Netty to manage connections,
 * handle WebSocket upgrades, and enable real-time message broadcasting between clients.
 */
public class WebSocketChatServer {
    private static final Logger logger = Logger.getLogger(WebSocketChatServer.class.getName());
    private final int port;

    /**
     * Constructs a WebSocketChatServer with the specified port.
     *
     * @param port The port on which the WebSocket server will listen.
     */
    public WebSocketChatServer(int port) {
        this.port = port;
    }

    /**
     * Starts the WebSocket chat server.
     * Configures Netty with a boss group for accepting connections and a worker group for handling I/O.
     * Sets up the channel pipeline with a logging handler and the WebSocketChatServerInitializer to handle WebSocket connections.
     */
    public void start() throws InterruptedException {
        // Boss group: handles incoming connection requests
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // Worker group: handles data transfer for connected clients
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // Specifies the type of channel to use (non-blocking server socket channel)
                    .handler(new LoggingHandler(LogLevel.INFO)) // Logs channel events at the INFO level
                    .childHandler(new WebSocketChatServerInitializer()) // Sets up the pipeline for child channels
                    .option(ChannelOption.SO_BACKLOG, 128) // Configures the maximum number of pending connections
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // Keeps connections alive

            // Binds the server to the specified port and starts it
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("WebSocket chat server started on port " + port);

            // Waits for the server socket to close (blocks until shutdown)
            future.channel().closeFuture().sync();
        } finally {
            // Gracefully shuts down the event loop groups to release resources
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Main method to start the WebSocket chat server on port 8080.
     *
     * @param args Command-line arguments
     * @throws InterruptedException If the server thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        new WebSocketChatServer(8080).start();
    }
}
