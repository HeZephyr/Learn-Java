package example.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

// A Counter class with a volatile integer field 'count'
// 'count' will be updated atomically in a multi-threaded environment
class Counter {
    volatile int count = 0; // This field must be volatile for atomic operations
}

public class AtomicIntegerFieldUpdaterDemo {
    // Create an AtomicIntegerFieldUpdater for the 'count' field in the Counter class
    // AtomicIntegerFieldUpdater is used to atomically update a specific field of an object
    private static final AtomicIntegerFieldUpdater<Counter> updater = AtomicIntegerFieldUpdater.newUpdater(Counter.class, "count");

    public static void main(String[] args) {
        Counter counter = new Counter(); // Create a Counter instance

        // Create multiple threads that will perform atomic increment on the 'count' field of the Counter instance
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                // Increment the 'count' field atomically and print the updated value
                System.out.println("Updated count to: " + updater.incrementAndGet(counter));
            }).start();
        }
    }
}