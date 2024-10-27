package example.net.nio.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioServer {
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(NioServer.class.getName());

    public static void main(String[] args) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false); // Set to non-blocking mode
            logger.info("NIO Server started on port " + PORT);

            while (true) {
                // Accept a connection (non-blocking)
                SocketChannel clientChannel = serverChannel.accept();
                if (clientChannel != null) {
                    logger.info("Connected to client: " + clientChannel.getRemoteAddress());

                    // Send a message to the client
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    buffer.put("Hello from NIO Server!".getBytes());
                    buffer.flip();
                    clientChannel.write(buffer); // Send data to client

                    clientChannel.close(); // Close connection after sending message
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server encountered an IOException", e);
        }
    }
}
