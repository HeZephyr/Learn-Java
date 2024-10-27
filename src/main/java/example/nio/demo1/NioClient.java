package example.nio.demo1;

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
            clientChannel.configureBlocking(false); // Set to non-blocking mode
            logger.info("Connected to server at " + HOST + ":" + PORT);

            ByteBuffer buffer = ByteBuffer.allocate(256);
            int bytesRead = clientChannel.read(buffer); // Read message from server

            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                logger.info("Received from server: " + new String(data));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client encountered an IOException", e);
        }
    }
}
