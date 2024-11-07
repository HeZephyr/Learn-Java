package example.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        // create 5 threads to increment the counter
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    int value = atomicCounter.incrementAndGet(); // increment the counter, and get the updated value
                    System.out.println(Thread.currentThread().getName() + " incremented to: " + value);
                }
            }).start();
        }
    }
}