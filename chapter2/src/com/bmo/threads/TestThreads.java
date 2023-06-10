package com.bmo.threads;

public class TestThreads {
    public static void main(String[] args) {
        Thread thread = new Thread(new Thread2());
        thread.start();
    }

    public static class Thread2 implements Runnable {

        @Override
        public void run() {
            System.out.println("hello from Thread2");
        }
    }
}
