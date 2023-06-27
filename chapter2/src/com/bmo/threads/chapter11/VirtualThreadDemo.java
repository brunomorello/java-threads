package com.bmo.threads.chapter11;

import java.util.ArrayList;
import java.util.List;

public class VirtualThreadDemo {

    private static final int NUM_OF_VIRTUAL_THREADS = 2;

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> System.out.println("Inside thread: " + Thread.currentThread());

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < NUM_OF_VIRTUAL_THREADS; i++) {
            // it will be available on JDK 21
            Thread thread = Thread.ofVirtual.unstarted(runnable);
            threadList.add(thread);
        }

        for (Thread vThread: threadList) {
            vThread.start();
        }

        for (Thread vThread: threadList) {
            vThread.join();
        }

    }
}
