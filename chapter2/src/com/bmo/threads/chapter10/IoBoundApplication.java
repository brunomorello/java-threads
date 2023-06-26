package com.bmo.threads.chapter10;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoBoundApplication {

    private static final int NUM_OF_TASKS = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to start");
        scanner.nextLine();

        System.out.printf("Running %d of tasks\n", NUM_OF_TASKS);

        final long startTime = System.currentTimeMillis();
        performTask();
        System.out.printf("Tasks tooks %dms to complete\n", (System.currentTimeMillis() - startTime));
    }

    private static void performTask() {
        // With dynamic new thread approach if number of tasks is high, it's dangerous to have many threads
        ExecutorService executorService = Executors.newCachedThreadPool();

        // With fixed num of threads and a high number of tasks there is no out of memory, but performance is bad
//        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        for (int i = 0; i < NUM_OF_TASKS; i++) {
            executorService.submit(() -> blockingIoOperation());
        }
    }

    // Simulates a long blocking IO
    private static void blockingIoOperation() {
        System.out.println("Executing a blocking IO Operation: " + Thread.currentThread());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
