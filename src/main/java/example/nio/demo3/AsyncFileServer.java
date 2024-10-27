package example.nio.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncFileServer {
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(AsyncFileServer.class.getName());

    public static void main(String[] args) {
        try (AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(PORT));
            logger.info("Async File Server started on port " + PORT);

            while (true) {
                // Accept client connection asynchronously
                Future<AsynchronousSocketChannel> acceptFuture = serverChannel.accept();
                AsynchronousSocketChannel clientChannel = acceptFuture.get(); // Blocking until a client connects
                logger.info("Connected to client: " + clientChannel.getRemoteAddress());

                // Handle file request
                handleClientRequest(clientChannel);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Server encountered an error", e);
        }
    }

    private static void handleClientRequest(AsynchronousSocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Asynchronously read the filename sent by the client
        clientChannel.read(buffer, buffer, new java.nio.channels.CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) { // Callback when read is successful
                // Prepare the buffer to read data (flip switches from write mode to read mode)
                attachment.flip();

                // Retrieve the filename from the buffer
                byte[] filenameBytes = new byte[attachment.remaining()];
                attachment.get(filenameBytes);
                // Convert bytes to String and trim any leading/trailing whitespace
                String filename = new String(filenameBytes).trim();
                logger.info("Requested file: " + filename);
                attachment.clear();

                // Begin asynchronously sending the requested file to the client
                sendFileToClient(clientChannel, filename);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) { // Callback when read fails
                logger.log(Level.WARNING, "Failed to read from client", exc);
                closeChannel(clientChannel);
            }
        });
    }

    private static void sendFileToClient(AsynchronousSocketChannel clientChannel, String filename) {
        Path filePath = Paths.get("server_files", filename);
        try {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Start reading from the file at position 0
            readFileChunk(clientChannel, fileChannel, buffer, 0);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to open file: " + filename, e);
            closeChannel(clientChannel);  // Close the channel if the file cannot be opened
        }
    }

    private static void readFileChunk(AsynchronousSocketChannel clientChannel, AsynchronousFileChannel fileChannel,
                                      ByteBuffer buffer, long position) {
        // Read a chunk of the file asynchronously
        fileChannel.read(buffer, position, buffer, new java.nio.channels.CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer attachment) {
                if (bytesRead == -1) {  // End of file reached
                    logger.info("File transfer completed.");
                    closeChannel(clientChannel);  // Close the client channel once the transfer is complete
                    try {
                        fileChannel.close();  // Also close the file channel
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Failed to close file channel", e);
                    }
                    return;
                }

                // Prepare buffer for writing to the client
                attachment.flip();
                clientChannel.write(attachment, attachment, new java.nio.channels.CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        buffer.clear();  // Clear buffer for the next read
                        long newPosition = position + bytesRead;  // Update the file read position
                        readFileChunk(clientChannel, fileChannel, buffer, newPosition);  // Read the next chunk
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer buffer) {
                        logger.log(Level.WARNING, "Failed to send file data to client", exc);
                        closeChannel(clientChannel);  // Close the channel if there's an error sending data
                        try {
                            fileChannel.close();  // Also close the file channel
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed to close file channel after write failure", e);
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                logger.log(Level.WARNING, "Failed to read from file at position: " + position, exc);
                closeChannel(clientChannel);  // Close the client channel if there's an error reading the file
                try {
                    fileChannel.close();  // Also close the file channel
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to close file channel after read failure", e);
                }
            }
        });
    }

    private static void closeChannel(AsynchronousSocketChannel clientChannel) {
        try {
            if (clientChannel.isOpen()) {
                clientChannel.close();
                logger.info("Client connection closed.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close client connection", e);
        }
    }
}
