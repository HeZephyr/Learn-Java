package example.concurrent.thread.lock;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Thread is going to park.");
            LockSupport.park(); // Block the thread
            System.out.println("Thread is unparked.");
        });

        thread.start();

        try {
            Thread.sleep(1000); // Main thread waits for a while
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Main thread is going to unpark the thread.");
        LockSupport.unpark(thread); // Unblock the thread
    }
}