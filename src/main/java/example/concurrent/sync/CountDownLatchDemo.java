package example.concurrent.sync;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);

        // Create multiple threads for different tasks
        for (int i = 1; i <= taskCount; i++) {
            int taskId = i;
            new Thread(() -> {
                System.out.println("Task " + taskId + " is completed.");
                latch.countDown(); // Decrement the count of the latch
            }).start();
        }

        System.out.println("Main thread is waiting for tasks to complete...");
        latch.await(); // Wait until the count reaches zero
        System.out.println("All tasks are completed. Main thread continues.");
    }
}