package example.concurrent.container;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueDemo {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

    public static void main(String[] args) {
        // Producer thread that adds tasks to the queue
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    String task = "Task " + i;
                    queue.put(task); // Blocking if queue is full
                    System.out.println("Produced: " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Consumer thread that processes tasks from the queue
        new Thread(() -> {
            try {
                while (true) {
                    String task = queue.take(); // Blocking if queue is empty
                    System.out.println("Consumed: " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}