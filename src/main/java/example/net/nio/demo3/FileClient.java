package example.net.nio.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(FileClient.class.getName());

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);

        try {
            // Create a custom AsynchronousChannelGroup to manage the channel
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(2));
            try (AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open(group)) {
                Future<Void> connectFuture = clientChannel.connect(new InetSocketAddress(HOST, PORT));
                connectFuture.get();  // Blocking until connected
                logger.info("Connected to server at " + HOST + ":" + PORT);

                // Request file
                String filename = "example.txt";
                ByteBuffer buffer = ByteBuffer.wrap(filename.getBytes());
                clientChannel.write(buffer).get();

                // Receive file data
                receiveFileData(clientChannel, filename, latch);
                latch.await();  // Wait for the asynchronous file transfer to complete
            } finally {
                // Shutdown the AsynchronousChannelGroup after operations are complete
                group.shutdown();
                group.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);  // Wait for threads to terminate
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Client encountered an error", e);
        }
    }

    private static void receiveFileData(AsynchronousSocketChannel clientChannel, String filename, CountDownLatch latch) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Path outputPath = Paths.get("client_files", filename);

        try {
            Files.createDirectories(outputPath.getParent());
            Files.deleteIfExists(outputPath);
            Files.createFile(outputPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to set up output file", e);
            closeChannel(clientChannel, latch);
            return;
        }

        clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {  // End of stream, file transfer completed
                    logger.info("File download completed: " + filename);
                    closeChannel(clientChannel, latch);  // Close the client channel after receiving the entire file
                    return;
                }

                attachment.flip();
                try (var fileChannel = Files.newByteChannel(outputPath, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
                    fileChannel.write(attachment);  // Write buffer content to file
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to write to file", e);
                }
                attachment.clear();
                clientChannel.read(attachment, attachment, this);  // Read next chunk
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                logger.log(Level.WARNING, "Failed to read file data from server", exc);
                closeChannel(clientChannel, latch);  // Close the channel on failure
            }
        });
    }

    private static void closeChannel(AsynchronousSocketChannel clientChannel, CountDownLatch latch) {
        try {
            if (clientChannel.isOpen()) {
                clientChannel.close();
                logger.info("Client connection closed.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close client connection", e);
        } finally {
            latch.countDown();  // Signal that the asynchronous operation is complete
        }
    }
}
