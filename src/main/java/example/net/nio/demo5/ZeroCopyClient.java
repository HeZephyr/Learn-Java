package example.net.nio.demo5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZeroCopyClient connects to the server, requests a specific file,
 * and receives the file using zero-copy mechanisms for efficient transfer.
 */
public class ZeroCopyClient {
    private static final Logger logger = Logger.getLogger(ZeroCopyClient.class.getName());
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;
    private static final String REQUESTED_FILE = "example.txt"; // The name of the file requested from the server
    private static final String DEST_FILE_PATH = "client_files/received_example.txt"; // Destination path for the received file

    public static void main(String[] args) {
        // Establishes connection with the server and initiates file request
        try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(SERVER_ADDRESS, PORT))) {
            logger.info("Connected to server at " + SERVER_ADDRESS + ":" + PORT);

            // Request the file from the server
            requestFile(clientChannel);

            // Receive the file from the server and save it locally
            receiveFile(clientChannel);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client encountered an IOException", e);
        }
    }

    /**
     * Sends the file request to the server by writing the filename to the server's socket channel.
     *
     * @param clientChannel the socket channel connected to the server
     * @throws IOException if an I/O error occurs
     */
    private static void requestFile(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(ZeroCopyClient.REQUESTED_FILE.getBytes());
        clientChannel.write(buffer);  // Sends the file name request to the server
        logger.info("Requested file: " + ZeroCopyClient.REQUESTED_FILE);
    }

    /**
     * Receives the requested file from the server and saves it to the local file system.
     *
     * @param clientChannel the socket channel connected to the server
     * @throws IOException if an I/O error occurs
     */
    private static void receiveFile(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        Path outputPath = Path.of(ZeroCopyClient.DEST_FILE_PATH);

        // Prepares the output file path by creating directories if necessary
        Files.createDirectories(outputPath.getParent());
        Files.deleteIfExists(outputPath);  // Delete the file if it already exists
        Files.createFile(outputPath);      // Create a new empty file to write data

        // Open the output file as a writable FileChannel
        try (FileChannel fileChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE)) {
            while (clientChannel.read(buffer) > 0) {
                buffer.flip();
                fileChannel.write(buffer);  // Writes the received data into the file
                buffer.clear();  // Clears the buffer to receive the next chunk of data
            }
            logger.info("File received successfully: " + ZeroCopyClient.DEST_FILE_PATH);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write received file", e);
        }
    }
}
