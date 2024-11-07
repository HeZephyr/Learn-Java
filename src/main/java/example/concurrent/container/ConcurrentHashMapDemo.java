package example.concurrent.container;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    // Create a ConcurrentHashMap to store a counter with thread-safe operations
    private static final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Define a Runnable task that each thread will execute
        Runnable task = () -> {
            // Each thread will execute this loop 5 times
            for (int i = 0; i < 5; i++) {
                // Use the merge method to atomically update the value associated with the "counter" key
                // If the "counter" key doesn't exist, it will be initialized to 1
                // If it does exist, the current value will be incremented by 1
                map.merge("counter", 1, Integer::sum); // Atomic update of the counter

                // Print the updated counter value with the thread name for tracking
                System.out.println(Thread.currentThread().getName() + " updated counter to " + map.get("counter"));
            }
        };

        // Create and start multiple threads (in this case, 3 threads)
        for (int i = 0; i < 3; i++) {
            new Thread(task).start(); // Each thread executes the defined task
        }
    }
}