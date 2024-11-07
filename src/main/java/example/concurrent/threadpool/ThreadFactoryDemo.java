package example.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryDemo {
    public static void main(String[] args) {
        // Create a FixedThreadPool with a custom ThreadFactory
        ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomThreadFactory("CustomPool"));

        // Submit tasks to the thread pool
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing task " + taskId);
                try {
                    Thread.sleep(1000); // Simulate task execution time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Shut down the executor service
        executorService.shutdown();
    }
}

class CustomThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public CustomThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        // Create a new thread with a custom name and set it as a daemon
        Thread thread = new Thread(r, namePrefix + "-Thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(true); // Set the thread as a daemon thread
        thread.setPriority(Thread.NORM_PRIORITY); // Set thread priority (default priority)
        return thread;
    }
}