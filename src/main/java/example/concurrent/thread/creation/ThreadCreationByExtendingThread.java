package example.concurrent.thread.creation;

public class ThreadCreationByExtendingThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread is running using Thread class");
    }

    public static void main(String[] args) {
        Thread thread = new ThreadCreationByExtendingThread();
        thread.start(); // start the thread
    }
}
