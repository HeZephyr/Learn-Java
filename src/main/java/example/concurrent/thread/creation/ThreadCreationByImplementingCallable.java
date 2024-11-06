package example.concurrent.thread.creation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Example class demonstrating thread creation by implementing Callable interface.
 * This approach allows the thread to return a result and throw checked exceptions.
 */
public class ThreadCreationByImplementingCallable implements Callable<String> {

    /**
     * The call method is executed when the thread is started.
     * It returns a string message.
     *
     * @return a message indicating the thread is running
     */
    @Override
    public String call() {
        return "Thread is running using Callable interface";
    }

    public static void main(String[] args) {
        // Create an instance of Callable
        Callable<String> callable = new ThreadCreationByImplementingCallable();

        // Wrap the Callable in a FutureTask, which can be used to start the thread and get the result
        FutureTask<String> futureTask = new FutureTask<>(callable);

        // Create a Thread with the FutureTask and start it
        Thread thread = new Thread(futureTask);
        thread.start(); // Start the thread

        try {
            // Get the result from the thread. This will block until the result is available.
            String result = futureTask.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            // Log any exceptions that occur during execution
            Logger.getLogger(ThreadCreationByImplementingCallable.class.getName()).severe(e.getMessage());
        }
    }
}