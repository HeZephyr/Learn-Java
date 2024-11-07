package example.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FixedThreadPoolDemo {
    public static void main(String[] args) {
        // Create a FixedThreadPool with 3 threads
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Submit multiple tasks simulating complex calculations
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executorService.submit(() -> {
                System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                performComplexCalculation(taskId); // Simulate a time-consuming task
            });
        }

        // Shut down the executor after submitting all tasks
        executorService.shutdown(); // No new tasks will be accepted, and ensure the resources are released after all tasks are completed

        try {
            // Wait for all tasks to complete
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        System.out.println("All tasks completed.");
    }

    // Simulates a time-consuming calculation
    private static void performComplexCalculation(int taskId) {
        try {
            Thread.sleep(2000); // Simulate delay
            System.out.println("Task " + taskId + " completed calculation");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}