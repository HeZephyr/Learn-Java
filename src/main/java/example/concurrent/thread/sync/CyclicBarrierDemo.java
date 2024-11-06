package example.concurrent.thread.sync;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Logger;

public class CyclicBarrierDemo {

    private static final Logger logger = Logger.getLogger(CyclicBarrierDemo.class.getName());

    public static void main(String[] args) {
        // Create a CyclicBarrier for 3 threads, with a barrier action
        CyclicBarrier barrier = new CyclicBarrier(3, () ->
                logger.info("All threads have reached the barrier. Moving to the next phase...")
        );

        // Create multiple worker threads
        for (int i = 1; i <= 3; i++) {
            Thread worker = new Thread(new Worker(barrier), "Worker-" + i);
            worker.start();
        }
    }

    static class Worker implements Runnable {
        private final CyclicBarrier barrier;

        Worker(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                logger.info(Thread.currentThread().getName() + " is performing some work...");
                Thread.sleep(1000); // Simulate work

                logger.info(Thread.currentThread().getName() + " has reached the barrier.");
                barrier.await(); // Wait at the barrier, and indicate that the thread has reached the barrier

                logger.info(Thread.currentThread().getName() + " is continuing after the barrier.");
            } catch (InterruptedException | BrokenBarrierException e) {
                logger.severe(Thread.currentThread().getName() + " was interrupted or barrier broken");
            }
        }
    }
}