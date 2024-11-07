package example.concurrent.sync;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.logging.Logger;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        int partyCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(partyCount, () -> {
            System.out.println("All parties have arrived at the barrier. Proceeding to the next phase.");
        });

        // Create threads that will reach the barrier point
        for (int i = 1; i <= partyCount; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " has reached the barrier.");
                    barrier.await(); // Wait until all parties reach the barrier
                    System.out.println(Thread.currentThread().getName() + " continues after the barrier.");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    public static class ExchangerDemo {

        private static final Logger logger = Logger.getLogger(ExchangerDemo.class.getName());

        public static void main(String[] args) {
            // Create an Exchanger to allow two threads to exchange data
            Exchanger<String> exchanger = new Exchanger<>();

            // Producer thread that will send data to the consumer
            Thread producer = new Thread(() -> {
                try {
                    // Data to be sent by the producer
                    String dataToSend = "Data from Producer";
                    logger.info("Producer is sending data: " + dataToSend);

                    // Exchange data with the consumer
                    // This call will block until the consumer is also ready to exchange data
                    String receivedData = exchanger.exchange(dataToSend);

                    // Log the data received from the consumer
                    logger.info("Producer received data: " + receivedData);
                } catch (InterruptedException e) {
                    // Log if the producer thread is interrupted during the exchange
                    logger.severe("Producer was interrupted");
                }
            });

            // Consumer thread that will send data to the producer
            Thread consumer = new Thread(() -> {
                try {
                    // Data to be sent by the consumer
                    String dataToSend = "Data from Consumer";
                    logger.info("Consumer is sending data: " + dataToSend);

                    // Exchange data with the producer
                    // This call will also block until the producer is ready to exchange data
                    String receivedData = exchanger.exchange(dataToSend);

                    // Log the data received from the producer
                    logger.info("Consumer received data: " + receivedData);
                } catch (InterruptedException e) {
                    // Log if the consumer thread is interrupted during the exchange
                    logger.severe("Consumer was interrupted");
                }
            });

            // Start both threads to initiate the data exchange
            producer.start();
            consumer.start();
        }
    }
}
