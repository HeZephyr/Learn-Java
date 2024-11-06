package example.concurrent.thread.sync;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class CountDownLatchDemo {

    private static final Logger logger = Logger.getLogger(CountDownLatchDemo.class.getName());

    public static void main(String[] args) throws InterruptedException {
        // Create a CountDownLatch with a count of 3
        CountDownLatch latch = new CountDownLatch(3);

        // Create multiple worker threads
        for (int i = 1; i <= 3; i++) {
            Thread worker = new Thread(new Worker(latch), "Worker-" + i);
            worker.start();
        }

        // Main thread waits until all workers have completed their tasks
        logger.info("Main thread waiting for workers to finish...");
        latch.await(); // Wait until the count reaches zero
        logger.info("All workers have finished. Main thread proceeding.");
    }

    static class Worker implements Runnable {
        private final CountDownLatch latch;

        Worker(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                logger.info(Thread.currentThread().getName() + " is working...");
                Thread.sleep(1000); // Simulate work
                logger.info(Thread.currentThread().getName() + " has finished.");
            } catch (InterruptedException e) {
                logger.severe(Thread.currentThread().getName() + " was interrupted");
            } finally {
                latch.countDown(); // Decrease the count of the latch
            }
        }
    }
}