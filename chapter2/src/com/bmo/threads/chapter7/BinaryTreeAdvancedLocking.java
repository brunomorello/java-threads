package com.bmo.threads.chapter7;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BinaryTreeAdvancedLocking {

    public static final int HIGHEST_PRICE = 1000;
    public static final int ITERATOR_TEST = 10000000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        Random random = new Random();

        for (int i = 0; i < ITERATOR_TEST; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numOfReaderThreads = 7;
        List<Thread> threadList = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numOfReaderThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < ITERATOR_TEST; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            threadList.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();

        for (Thread reader : threadList) {
            reader.start();
        }

        for (Thread reader : threadList) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.println(String.format("reading took %d ms", (endReadingTime - startReadingTime)));

    }
    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = reentrantReadWriteLock.readLock();
        private Lock writeLock = reentrantReadWriteLock.writeLock();

        public int getNumberOfItemsInPriceRange(int lowerBond, int upperBond) {
            lock.lock();
//            readLock.lock();

            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBond);
                Integer toKey = priceToCountMap.floorKey(upperBond);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for (int numOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numOfItemsForPrice;
                }
                return sum;
            } finally {
                lock.unlock();
//                readLock.unlock();
            }
        }

        public void addItem(int price) {
            lock.lock();
//            writeLock.lock();
            try {
                Integer numOfItemsForPrice = priceToCountMap.get(price);
                if (numOfItemsForPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, numOfItemsForPrice + 1);
                }
            } finally {
                lock.unlock();
//                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            lock.lock();
//            writeLock.lock();
            try {
                Integer numOfItemsForPrice = priceToCountMap.get(price);
                if (numOfItemsForPrice == null || numOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numOfItemsForPrice -1);
                }
            } finally {
                lock.unlock();
//                writeLock.unlock();
            }
        }
    }
}
