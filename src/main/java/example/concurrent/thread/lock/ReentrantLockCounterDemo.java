package example.concurrent.thread.lock;

import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ReentrantLockCounterDemo {

    private static final Logger logger = Logger.getLogger(ReentrantLockCounterDemo.class.getName());

    // Method to get the current counter value
    // Shared counter variable
    @Getter
    private int counter = 0;

    // ReentrantLock instance for synchronization
    private final ReentrantLock lock = new ReentrantLock();

    // Method to increment the counter using ReentrantLock
    public void increment() {
        lock.lock(); // Acquire the lock
        try {
            counter++;
            logger.info("Counter incremented to: " + counter);
        } finally {
            lock.unlock(); // Ensure the lock is released in the finally block
        }
    }

    public static void main(String[] args) {
        ReentrantLockCounterDemo counterDemo = new ReentrantLockCounterDemo();

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