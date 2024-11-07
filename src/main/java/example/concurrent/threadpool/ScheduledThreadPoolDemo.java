package example.concurrent.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolDemo {
    public static void main(String[] args) {
        // Create a ScheduledThreadPool with 2 threads for periodic tasks
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Schedule a one-time delayed task (5 seconds delay)
        scheduler.schedule(() -> System.out.println("One-time backup started at " + System.currentTimeMillis()),
                5, TimeUnit.SECONDS);

        // Schedule a recurring task with a fixed rate (initial delay of 3 seconds, period of 2 seconds)
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Recurring backup started at " + System.currentTimeMillis());
        }, 3, 2, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Recurring backup with fixed delay started at " + System.currentTimeMillis());
        }, 3, 2, TimeUnit.SECONDS);

        // The scheduled tasks will continue running until scheduler is shut down
        scheduler.schedule(scheduler::shutdown, 10, TimeUnit.SECONDS); // Schedule shutdown after 10 seconds
    }
}