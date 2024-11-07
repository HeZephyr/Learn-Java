package example.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayDemo {
    public static void main(String[] args) {
        AtomicIntegerArray array = new AtomicIntegerArray(5);

        // create 5 threads to increment the array elements
        for (int i = 0; i < 5; i++) {
            int index = i;
            new Thread(() -> {
                array.incrementAndGet(index); // atomically increment the element at index
                System.out.println("Index " + index + " updated to: " + array.get(index));
            }).start();
        }
    }
}