package com.bmo.threads.chapter11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoBoundApplicationV2 {
    private static final int NUM_OF_TASKS = 10_000;

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
        // JDK 21 - newVirtualThreadPerTaskExecutor
//        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        // Fixed Thread Pool has a huge overhead - change to newVirtualThreadPerTaskExecutor
        ExecutorService executorService = Executors.newFixedThreadPool(1000);

//        for (int i = 0; i < NUM_OF_TASKS; i++) {
//            executorService.submit(() -> blockingIoOperation());
//        }

        for (int i = 0; i < NUM_OF_TASKS; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    blockingIoOperation();
                }
            });
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
