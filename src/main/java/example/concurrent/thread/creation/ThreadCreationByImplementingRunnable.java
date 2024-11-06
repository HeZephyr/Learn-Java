package example.concurrent.thread.creation;

public class ThreadCreationByImplementingRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread is running using Runnable interface");
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new ThreadCreationByImplementingRunnable());
        thread.start(); // start the thread
    }
}
