package example.concurrent.thread.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockDemo {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private int data = 0;

    public void writeData(int value) {
        rwLock.writeLock().lock(); // Acquire write lock
        try {
            System.out.println("Writing data: " + value);
            data = value;
        } finally {
            rwLock.writeLock().unlock(); // Release write lock
        }
    }

    public void readData() {
        rwLock.readLock().lock(); // Acquire read lock
        try {
            System.out.println("Reading data: " + data);
        } finally {
            rwLock.readLock().unlock(); // Release read lock
        }
    }

    public static void main(String[] args) {
        ReentrantReadWriteLockDemo demo = new ReentrantReadWriteLockDemo();

        // Create a writer thread
        new Thread(() -> demo.writeData(42)).start();

        // Create multiple reader threads
        for (int i = 0; i < 3; i++) {
            new Thread(demo::readData).start();
        }
    }
}