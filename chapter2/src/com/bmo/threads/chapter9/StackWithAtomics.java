package com.bmo.threads.chapter9;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class StackWithAtomics {

    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            stack.push(i);
        }

        List<Thread> threadList = new ArrayList<>();

        int pushingThreads = 2;
        int popingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });

            thread.setDaemon(true);
            threadList.add(thread);
        }

        for (int i = 0; i < popingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });

            thread.setDaemon(true);
            threadList.add(thread);
        }

        for (Thread t : threadList) {
            t.start();
        }

        Thread.sleep(10000);

        System.out.println(String.format(Locale.US, "%,d operations were performed", stack.getCounter()));
    }

    public static class LockFreeStack<T> {

        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);
        public void push(T value) {
            StackNode<T> newHeadValue = new StackNode<>(value);

            while (true) {
                StackNode<T> currentHead = head.get();
                newHeadValue.next = currentHead;
                if (head.compareAndSet(currentHead, newHeadValue)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHeadNode;

            while (currentHead != null) {
                newHeadNode = currentHead.next;
                if (head.compareAndSet(currentHead, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return counter.get();
        }
    }

    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if (head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }
    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            this.next = next;
        }
    }
}
