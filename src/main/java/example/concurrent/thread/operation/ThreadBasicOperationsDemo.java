package example.concurrent.thread.operation;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadBasicOperationsDemo {

    private static final Logger logger = Logger.getLogger(ThreadBasicOperationsDemo.class.getName());

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            try {
                logger.info("Thread1: Sleeping for 2 seconds...");
                Thread.sleep(2000); // Sleep for 2 seconds
                logger.info("Thread1: Woke up from sleep");
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Thread1 was interrupted during sleep", e);
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (ThreadBasicOperationsDemo.class) {
                try {
                    logger.info("Thread2: Waiting for notification...");
                    ThreadBasicOperationsDemo.class.wait(); // Wait for notification
                    logger.info("Thread2: Received notification and resumed");
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Thread2 was interrupted while waiting", e);
                }
            }
        });

        Thread thread3 = new Thread(() -> {
            try {
                logger.info("Thread3: Waiting for Thread1 to finish using join");
                thread1.join(); // Wait for thread1 to finish
                logger.info("Thread3: Thread1 has finished, proceeding further");
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Thread3 was interrupted while joining", e);
            }
        });

        // Start the threads
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            Thread.sleep(500); // Sleep for 0.5 seconds to ensure thread2 is waiting
            synchronized (ThreadBasicOperationsDemo.class) {
                logger.info("Main thread: Notifying thread2");
                ThreadBasicOperationsDemo.class.notify(); // Notify thread2
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Main thread was interrupted", e);
        }
    }
}