package example.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolDemo {
    public static void main(String[] args) {
        // Create a CachedThreadPool for handling dynamic tasks
        // indicate that the thread pool can create as many threads as needed
        ExecutorService executorService = Executors.newCachedThreadPool();

        // Submit multiple tasks simulating data processing requests
        for (int i = 1; i <= 5; i++) {
            int requestId = i;
            executorService.submit(() -> {
                System.out.println("Processing request " + requestId + " on " + Thread.currentThread().getName());
                processDataRequest(requestId);
            });
        }

        // Shut down the executor after submitting all tasks
        executorService.shutdown();
    }

    // Simulates processing a data request with random delay
    private static void processDataRequest(int requestId) {
        try {
            Thread.sleep((int) (Math.random() * 1000 + 500)); // Random delay
            System.out.println("Request " + requestId + " processed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}