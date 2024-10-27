package example.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(NioClient.class.getName());

    public static void main(String[] args) {
        try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT))) {
            clientChannel.configureBlocking(false);
            logger.info("Connected to server at " + HOST + ":" + PORT);

            // Send message to server
            String message = "Hello from NIO Client!";
            ByteBuffer buffer = ByteBuffer.allocate(256);
            // Writing data to the buffer
            buffer.put(message.getBytes());
            // Flip to switch to read mode for writing to the channel
            buffer.flip();
            // Write buffer  contents to the channel
            clientChannel.write(buffer);

            // Read server's response
            buffer.clear();
            int bytesRead;
            while ((bytesRead = clientChannel.read(buffer)) == 0) {
                // Wait for response (simple polling)
                Thread.sleep(100);
            }
            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                logger.info("Received from server: " + new String(data));
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Client encountered an IOException", e);
        }
    }
}
