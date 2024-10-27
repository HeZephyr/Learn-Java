package example.net.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BioClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(BioClient.class.getName());

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) { // Blocking call
            logger.info("Connected to server at " + HOST + ":" + PORT);

            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[256];
            int bytesRead = in.read(buffer); // Blocking read

            if (bytesRead != -1) {
                String received = new String(buffer, 0, bytesRead);
                logger.info("Received from server: " + received);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client encountered an IOException", e);
        }
    }
}
