package example.concurrent.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSumDemo {
    // Define the threshold for the smallest task size
    private static final int THRESHOLD = 5;

    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // Create ForkJoinPool to manage Fork/Join tasks
        ForkJoinPool pool = new ForkJoinPool();

        // Submit the task to the pool
        int sum = pool.invoke(new SumTask(numbers, 0, numbers.length));

        System.out.println("Total sum: " + sum);
    }

    // RecursiveTask to compute the sum of an integer array
    static class SumTask extends RecursiveTask<Integer> {
        private final int[] array;
        private final int start;
        private final int end;

        public SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            // Check if the task is small enough to be computed directly
            if (end - start <= THRESHOLD) {
                int sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                // Split the task into two subtasks
                int mid = (start + end) / 2;
                SumTask leftTask = new SumTask(array, start, mid);
                SumTask rightTask = new SumTask(array, mid, end);

                // Fork the subtasks to run them in parallel
                leftTask.fork();
                rightTask.fork();

                // Wait for the subtasks to finish and join their results
                int leftResult = leftTask.join();
                int rightResult = rightTask.join();

                // Combine the results
                return leftResult + rightResult;
            }
        }
    }
}