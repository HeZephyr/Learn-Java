package example.concurrent.thread.synchronization;

import lombok.Getter;

import java.util.logging.Logger;

@Getter
public class SynchronizedCounterDemo {

    private static final Logger logger = Logger.getLogger(SynchronizedCounterDemo.class.getName());

    // Method to get the current counter value
    // Shared counter variable
    private int counter = 0;

    // Synchronized method to increment the counter
    public synchronized void increment() {
        counter++;
        logger.info("Counter incremented to: " + counter);
    }

    public static void main(String[] args) {
        SynchronizedCounterDemo counterDemo = new SynchronizedCounterDemo();

        // Create multiple threads that increment the counter
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                counterDemo.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                counterDemo.increment();
            }
        });

        thread1.start();
        thread2.start();

        try {
            // Wait for both threads to finish
            thread1.join();
            thread2.join();

            // Print the final counter value
            logger.info("Final counter value: " + counterDemo.getCounter());
        } catch (InterruptedException e) {
            logger.severe("Main thread was interrupted");
        }
    }
}