package example.concurrent.container;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueDemo {
    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        // Producer thread that adds tasks to the queue
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                String task = "Task " + i;
                queue.offer(task); // Add task to the queue
                System.out.println("Produced: " + task);
            }
        });

        // Consumer thread that processes tasks from the queue
        Thread consumer = new Thread(() -> {
            while (true) {
                String task = queue.poll(); // Remove task from the queue
                if (task != null) {
                    System.out.println("Consumed: " + task);
                } else {
                    break; // Exit if queue is empty
                }
            }
        });

        producer.start();
        try {
            producer.join(); // Wait for producer to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        consumer.start();
    }
}