package example.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BioServer {
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(BioServer.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("BIO Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Blocking call
                logger.info("Connected to client: " + clientSocket.getInetAddress());

                try (OutputStream out = clientSocket.getOutputStream()) {
                    String message = "Hello from BIO Server!";
                    out.write(message.getBytes()); // Send data to client
                    out.flush();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error sending message to client", e);
                }

                clientSocket.close(); // Close the connection after responding
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server encountered an IOException", e);
        }
    }
}
