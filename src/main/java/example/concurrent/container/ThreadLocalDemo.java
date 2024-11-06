package example.concurrent.container;

public class ThreadLocalDemo {
    // Define a ThreadLocal variable with an initial value of 0 for each thread
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        // Define a task that each thread will execute
        Runnable task = () -> {
            // Get the initial value from ThreadLocal for the current thread
            int initialValue = threadLocal.get();
            System.out.println(Thread.currentThread().getName() + " initial value: " + initialValue);

            // Set a new value for ThreadLocal in the current thread
            threadLocal.set(initialValue + 1);
            System.out.println(Thread.currentThread().getName() + " new value: " + threadLocal.get());
        };

        // Create and start multiple threads, each executing the task
        for (int i = 0; i < 3; i++) {
            new Thread(task).start();
        }
    }
}