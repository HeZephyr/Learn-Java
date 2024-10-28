package example.net.nio.demo5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZeroCopyServer is a server that uses zero-copy to transfer files to clients.
 * The server listens on a specified port and waits for client connections.
 * Upon receiving a file request from a client, it uses zero-copy to send the file data.
 */
public class ZeroCopyServer {
    private static final Logger logger = Logger.getLogger(ZeroCopyServer.class.getName());
    private static final int PORT = 8080;
    private static final String FILE_DIRECTORY = "server_files"; // Directory where files are stored on the server

    public static void main(String[] args) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            // Bind the server to a port and start listening for client connections
            serverChannel.bind(new InetSocketAddress(PORT));
            logger.info("Zero-copy server started on port " + PORT);

            while (true) {
                // Accept client connections in a loop
                try (SocketChannel clientChannel = serverChannel.accept()) {
                    logger.info("Client connected: " + clientChannel.getRemoteAddress());

                    // Receive the requested file name from the client
                    String requestedFile = receiveFileName(clientChannel);

                    // Use zero-copy to send the requested file
                    sendFile(clientChannel, requestedFile);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to process client connection", e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server encountered an IOException", e);
        }
    }

    /**
     * Receives the file name from the client.
     * The client sends a file name, which the server reads from the channel.
     *
     * @param clientChannel the channel connected to the client
     * @return the name of the file requested by the client
     * @throws IOException if an I/O error occurs
     */
    private static String receiveFileName(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        clientChannel.read(buffer);
        buffer.flip();

        // Convert buffer data to a string representing the file name
        byte[] fileNameBytes = new byte[buffer.remaining()];
        buffer.get(fileNameBytes);
        return new String(fileNameBytes).trim();
    }

    /**
     * Sends the requested file to the client using zero-copy.
     * This method utilizes FileChannel's transferTo method to send data directly from the file to the client channel.
     *
     * @param clientChannel the channel connected to the client
     * @param filename the name of the file to be sent
     */
    private static void sendFile(SocketChannel clientChannel, String filename) {
        Path filePath = Path.of(FILE_DIRECTORY, filename);

        try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            long position = 0;
            logger.info("Starting file transfer: " + filename);

            // Use zero-copy to transfer the file content to the client channel
            while (position < fileSize) {
                position += fileChannel.transferTo(position, fileSize - position, clientChannel);
            }

            logger.info("File transfer completed using zero-copy for: " + filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File transfer failed", e);
        }
    }
}
