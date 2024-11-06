package example.concurrent.thread.interruption;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadInterruptionDemo {

    // Initialize a Logger for this class
    private static final Logger logger = Logger.getLogger(ThreadInterruptionDemo.class.getName());

    public static void main(String[] args) {
        // Create a new thread
        Thread thread = new Thread(() -> {
            // Continuously check if the thread should continue running
            while (true) {
                // Check if the current thread has been interrupted
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread was interrupted! Exiting...");
                    break; // Exit the loop if interrupted
                }
                try {
                    // Simulating work by making the thread sleep for 500 ms
                    logger.info("Thread is working...");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Log the interruption with an error message
                    logger.log(Level.SEVERE, "Thread was interrupted during sleep", e);

                    // Re-interrupt the thread to set the interrupted status again
                    Thread.currentThread().interrupt();
                    break; // Exit the loop to stop the thread
                }
            }
        });

        // Start the worker thread
        thread.start();

        try {
            // Main thread sleeps for 2 seconds to allow the worker thread to start and work
            Thread.sleep(2000);

            // Log that the main thread is interrupting the worker thread
            logger.info("Main thread is interrupting the worker thread");
            thread.interrupt(); // Interrupt the worker thread
        } catch (InterruptedException e) {
            // Log if the main thread itself is interrupted
            logger.log(Level.SEVERE, "Main thread was interrupted", e);
        }
    }
}