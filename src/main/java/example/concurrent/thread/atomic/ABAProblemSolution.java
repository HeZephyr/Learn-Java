package example.concurrent.thread.atomic;

import java.util.concurrent.atomic.AtomicStampedReference;

public class ABAProblemSolution {
    public static void main(String[] args) {
        // Initialize the AtomicStampedReference with an initial value of 100 and initial stamp 1
        AtomicStampedReference<Integer> atomicStampedRef = new AtomicStampedReference<>(100, 1);

        // Array to hold the initial stamp (version)
        int[] stampHolder = new int[1];
        Integer initialValue = atomicStampedRef.get(stampHolder);

        // Print the initial value and stamp
        System.out.println("Initial Value: " + initialValue + ", Initial Stamp: " + stampHolder[0]);

        // Thread to simulate the ABA problem
        Thread thread1 = new Thread(() -> {
            // Change the value from 100 to 200, increment the stamp by 1
            atomicStampedRef.compareAndSet(100, 200, stampHolder[0], stampHolder[0] + 1);
            System.out.println("Thread1 changed value to 200");

            // Change the value back from 200 to 100, increment the stamp again
            atomicStampedRef.compareAndSet(200, 100, stampHolder[0] + 1, stampHolder[0] + 2);
            System.out.println("Thread1 changed value back to 100");
        });

        // Thread to attempt an update and detect if the stamp has changed
        Thread thread2 = new Thread(() -> {
            // Attempt to update the value from 100 to 300, but only if the stamp is still the initial stamp
            boolean success = atomicStampedRef.compareAndSet(100, 300, stampHolder[0], stampHolder[0] + 1);

            // Print the result of the update attempt, the current value, and the current stamp
            System.out.println("Thread2 update success: " + success + ", New Value: "
                    + atomicStampedRef.getReference() + ", New Stamp: " + atomicStampedRef.getStamp());
        });

        // Start both threads
        thread1.start();
        thread2.start();
    }
}