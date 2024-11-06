package example.concurrent.thread.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class CASCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        int oldValue, newValue;
        do {
            oldValue = count.get();
            newValue = oldValue + 1;
        } while (!count.compareAndSet(oldValue, newValue)); // CAS操作
    }

    public int getCount() {
        return count.get();
    }

    public static void main(String[] args) {
        CASCounter counter = new CASCounter();

        // Create 10 threads that increment the counter
        for (int i = 0; i < 10; i++) {
            new Thread(counter::increment).start();
        }

        try {
            Thread.sleep(1000); // wait for all threads to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final count: " + counter.getCount());
    }
}