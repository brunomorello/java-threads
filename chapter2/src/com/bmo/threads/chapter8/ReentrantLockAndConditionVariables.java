package com.bmo.threads.chapter8;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockAndConditionVariables {

    /**
     * Two ways to do same thing
     */
    class SomeClass1 {
        boolean isCompleted = false;

        public synchronized void declareSuccess() throws InterruptedException {
            while (!isCompleted) {
                wait();
            }

            System.out.println("Success!!");
        }

        public synchronized void finishWork() {
            isCompleted = true;
            notify();
        }
    }

    class SomeClass2 {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        boolean isCompleted = false;

        public void declareSuccess() throws InterruptedException {
            lock.lock();
            try {
                while (!isCompleted) {
                    condition.await();
                }
            }
            finally {
                lock.unlock();
            }

            System.out.println("Success!!");
        }

        public void finishWork() {
            lock.lock();
            try {
                isCompleted = true;
                condition.signal();
            }
            finally {
                lock.unlock();
            }
        }
    }
}
