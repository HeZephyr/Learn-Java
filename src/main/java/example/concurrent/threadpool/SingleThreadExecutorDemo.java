package example.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutorDemo {
    public static void main(String[] args) {
        // Create a SingleThreadExecutor to process orders sequentially
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Submit multiple tasks simulating order processing
        for (int i = 1; i <= 5; i++) {
            int orderId = i;
            executorService.submit(() -> {
                System.out.println("Processing order " + orderId + " on " + Thread.currentThread().getName());
                processOrder(orderId);
            });
        }

        // Shut down the executor after all orders are processed
        executorService.shutdown();
    }

    // Simulates order processing with a fixed delay
    private static void processOrder(int orderId) {
        try {
            Thread.sleep(1000); // Simulate order processing time
            System.out.println("Order " + orderId + " processed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}