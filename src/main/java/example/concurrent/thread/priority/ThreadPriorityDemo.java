package example.concurrent.thread.priority;

import java.util.logging.Logger;

public class ThreadPriorityDemo {

    // Initialize a Logger for this class
    private static final Logger logger = Logger.getLogger(ThreadPriorityDemo.class.getName());

    public static void main(String[] args) {
        // Create a thread with maximum priority
        Thread highPriorityThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                logger.info("High-priority thread executing, iteration: " + i);
            }
        });
        highPriorityThread.setPriority(Thread.MAX_PRIORITY); // Set to maximum priority

        // Create a thread with normal priority
        Thread normalPriorityThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                logger.info("Normal-priority thread executing, iteration: " + i);
            }
        });
        normalPriorityThread.setPriority(Thread.NORM_PRIORITY); // Set to normal priority

        // Create a thread with minimum priority
        Thread lowPriorityThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                logger.info("Low-priority thread executing, iteration: " + i);
            }
        });
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY); // Set to minimum priority

        // Start all threads
        highPriorityThread.start();
        normalPriorityThread.start();
        lowPriorityThread.start();
    }
}