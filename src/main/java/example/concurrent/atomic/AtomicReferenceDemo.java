package example.concurrent.atomic;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {
    private static final AtomicReference<String> atomicString = new AtomicReference<>("initial");

    public static void main(String[] args) {
        // Thread 1: try to update the value
        new Thread(() -> {
            String prev = atomicString.get();
            if (atomicString.compareAndSet("initial", "updated by Thread 1")) {
                System.out.println("Thread 1 successfully updated from '" + prev + "' to '" + atomicString.get() + "'");
            } else {
                System.out.println("Thread 1 failed to update, current value is '" + atomicString.get() + "'");
            }
        }).start();

        // Thread 2: try to update the value
        new Thread(() -> {
            String prev = atomicString.get();
            if (atomicString.compareAndSet("initial", "updated by Thread 2")) {
                System.out.println("Thread 2 successfully updated from '" + prev + "' to '" + atomicString.get() + "'");
            } else {
                System.out.println("Thread 2 failed to update, current value is '" + atomicString.get() + "'");
            }
        }).start();
    }
}