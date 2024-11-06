package example.concurrent.thread.atomic;

import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {
    private final AtomicReference<Thread> owner = new AtomicReference<>();

    public void lock() {
        Thread currentThread = Thread.currentThread();
        while (!owner.compareAndSet(null, currentThread)); // spin until lock is acquired
    }

    public void unlock() {
        Thread currentThread = Thread.currentThread();
        owner.compareAndSet(currentThread, null); // release the lock
    }

    public static void main(String[] args) {
        SpinLock spinLock = new SpinLock();

        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " trying to acquire lock.");
            spinLock.lock();
            System.out.println(Thread.currentThread().getName() + " acquired lock.");

            try {
                Thread.sleep(1000); // simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println(Thread.currentThread().getName() + " releasing lock.");
                spinLock.unlock();
            }
        };

        // create two threads that try to acquire the lock
        new Thread(task).start();
        new Thread(task).start();
    }
}