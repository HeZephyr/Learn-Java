package example.concurrent.container;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListDemo {
    private static final List<String> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // Add initial elements
        list.add("Item1");
        list.add("Item2");

        // Thread that iterates over the list
        new Thread(() -> {
            for (String item : list) {
                System.out.println("Reading: " + item);
                try {
                    Thread.sleep(100); // Simulate processing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        // Thread that adds elements to the list
        new Thread(() -> {
            list.add("Item3");
            System.out.println("Added Item3");
        }).start();
    }
}