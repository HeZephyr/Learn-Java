package example.concurrent.thread.lock;

public class MyLockTest {
    private static final MyLock lock = new MyLock();
    private static int counter = 0;

    public static void main(String[] args) {
        Runnable task = () -> {
            // Acquire the lock
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " acquired lock");

                // Increment the counter safely
                counter++;
                System.out.println("Counter: " + counter);

            } finally {
                // Release the lock
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " released lock");
            }
        };

        // Create and start multiple threads
        for (int i = 0; i < 5; i++) {
            new Thread(task).start();
        }
    }
}