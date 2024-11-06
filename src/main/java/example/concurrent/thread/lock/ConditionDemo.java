package example.concurrent.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int buffer = 0;
    private boolean isFull = false;

    public void produce() throws InterruptedException {
        lock.lock();
        try {
            while (isFull) {
                condition.await(); // Wait until buffer is not full
            }
            buffer++;
            System.out.println("Produced: " + buffer);
            isFull = true;
            condition.signalAll(); // Signal consumer
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();
        try {
            while (!isFull) {
                condition.await(); // Wait until buffer is full
            }
            System.out.println("Consumed: " + buffer);
            buffer--;
            isFull = false;
            condition.signalAll(); // Signal producer
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionDemo demo = new ConditionDemo();

        // Create producer thread
        new Thread(() -> {
            try {
                demo.produce();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Create consumer thread
        new Thread(() -> {
            try {
                demo.consume();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}