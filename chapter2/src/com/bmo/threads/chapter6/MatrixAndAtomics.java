package com.bmo.threads.chapter6;

import java.util.Random;

public class MatrixAndAtomics {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        BusinessLogic businessLogic1 = new BusinessLogic(metrics);
        BusinessLogic businessLogic2 = new BusinessLogic(metrics);

        MetricPrinter metricPrinter = new MetricPrinter(metrics);

        businessLogic1.start();
        businessLogic2.start();
        metricPrinter.start();
    }

    public static class MetricPrinter extends Thread {
        private Metrics metrics;

        public MetricPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                double currentAvg = metrics.getAvg();

                System.out.println("Current Average is " + currentAvg);
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }
        }
    }

    public static class Metrics {
        private long count = 0;
        private volatile double avg = 0.0;

        public synchronized void addSample(long sample) {
            double currentSum = avg * count;
            count++;
            avg = (currentSum + sample) / count ;
        }

        public double getAvg() {
            return avg;
        }
    }
}
