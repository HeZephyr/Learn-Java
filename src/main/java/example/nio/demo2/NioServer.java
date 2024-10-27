package example.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioServer {
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(NioServer.class.getName());

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false); // Non-blocking mode
            // Register the server channel with the selector to accept incoming connections
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("NIO Server started on port " + PORT);

            while (true) {
                selector.select(); // Blocking call, waiting for events

                // get the keys for which an event has occurred
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    // Remove the key to prevent handling it multiple times
                    keyIterator.remove();

                    if (key.isAcceptable()) { // New client connection
                        handleAccept(serverChannel, selector);
                    } else if (key.isReadable()) { // Data available to read
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server encountered an IOException", e);
        }
    }

    private static void handleAccept(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.info("Connected to client: " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                // Client has closed the connection; close the channel and cancel key
                try {
                    if (clientChannel.isOpen()) {
                        logger.info("Client disconnected: " + clientChannel.getRemoteAddress());
                    }
                } catch (ClosedChannelException e) {
                    logger.warning("Attempted to access remote address on closed channel.");
                } finally {
                    clientChannel.close();
                    key.cancel();
                }
                return;
            }
            buffer.flip();
            // Allocate an array with exact remaining size to store the data
            byte[] data = new byte[buffer.remaining()];
            // Read data from the buffer into the array
            buffer.get(data);
            String receivedMessage = new String(data);
            logger.info("Received from client: " + receivedMessage);

            // Echo message back to client
            if (clientChannel.isOpen()) {  // Check if the channel is still open
                buffer.clear();
                buffer.put(("Echo from server: " + receivedMessage).getBytes());
                buffer.flip();
                clientChannel.write(buffer); // Attempt to write only if the channel is open
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error handling client message", e);
            try {
                clientChannel.close();
                key.cancel();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error closing client connection", ex);
            }
        }
    }
}
