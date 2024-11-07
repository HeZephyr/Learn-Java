package example.concurrent.sync;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    public static void main(String[] args) {
        int permits = 3;
        Semaphore semaphore = new Semaphore(permits);

        // Create multiple threads that will try to access a limited resource
        for (int i = 1; i <= 5; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    semaphore.acquire(); // Acquire a permit
                    System.out.println("Worker " + workerId + " is accessing the resource.");
                    Thread.sleep(2000); // Simulate resource usage
                    System.out.println("Worker " + workerId + " has released the resource.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release(); // Release the permit
                }
            }).start();
        }
    }
}