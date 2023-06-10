package com.bmo.threads;

public class InterruptThread {
    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingTasks());
        thread.start();
        thread.interrupt();
    }

    private static class BlockingTasks implements Runnable {

        @Override
        public void run() {
            // do whatever
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
                System.out.println("Exiting blocking thread");
            }
        }
    }
}
