package com.bmo.threads.chapter6;

import java.util.Random;

public class DeadlockSimulation {

    public static void main(String[] args) throws InterruptedException {
        Intersection intersection = new Intersection();
        Thread threadTrainA = new Thread(new TrainA(intersection));
        Thread threadTrainB = new Thread(new TrainB(intersection));

        threadTrainA.start();
        threadTrainA.join(2);
        threadTrainB.start();
    }

    public static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(6);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadB();
            }
        }
    }

    public static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadA();
            }
        }
    }
    public static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through Road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void takeRoadB() {
            // Invert synchronized to avoid circular dependency and deadlock
//            synchronized (roadB) {
            synchronized (roadA) {
                System.out.println("Road B is locked by " + Thread.currentThread().getName());

//                synchronized (roadA) {
                synchronized (roadB) {
                    System.out.println("Train is passing trough Road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
