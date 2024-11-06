package example.concurrent.thread.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadLifecycleDemo {

    // Initialize a Logger for this class
    private static final Logger logger = Logger.getLogger(ThreadLifecycleDemo.class.getName());

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                // State: TIMED_WAITING (sleeping for 1 second)
                System.out.println("Thread is in TIMED_WAITING state");
                Thread.sleep(1000);

                synchronized (ThreadLifecycleDemo.class) {
                    // State: WAITING (waiting to acquire a lock)
                    System.out.println("Thread is in WAITING state");
                    ThreadLifecycleDemo.class.wait();
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Thread was interrupted", e);
            }
        });

        System.out.println("State after thread creation: " + thread.getState()); // NEW

        thread.start(); // State transitions to RUNNABLE
        System.out.println("State after starting the thread: " + thread.getState());

        try {
            Thread.sleep(1500); // Main thread sleeps to allow child thread to reach WAITING
            System.out.println("State while thread is sleeping: " + thread.getState());

            synchronized (ThreadLifecycleDemo.class) {
                ThreadLifecycleDemo.class.notify(); // Notify the waiting thread to continue
            }

            thread.join(); // Wait for the thread to finish
            System.out.println("State after thread termination: " + thread.getState()); // TERMINATED
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Main thread was interrupted", e);
        }
    }
}