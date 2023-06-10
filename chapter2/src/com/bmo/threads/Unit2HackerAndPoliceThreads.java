package com.bmo.threads;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Unit2HackerAndPoliceThreads {

    public static final int MAX_PWD = 9999;

    public static void main(String[] args) {
        Random random = new Random();

        Vault vault = new Vault(random.nextInt(MAX_PWD));

        List<Thread> threadList = Arrays.asList(new AscendingHackerThread(vault), new DescendingHackerThread(vault), new PoliceThread());
        threadList.forEach(thread -> thread.start());
    }

    private static class Vault {
        private int pwd;

        public Vault(int pwd) {
            this.pwd = pwd;
        }

        public boolean isPwdCorrect(int gues) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return this.pwd == gues;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }

        public void pwdBroken(int guess) {
            System.out.println(this.getName() + " guessed the password " + guess);
            System.exit(0);
        }
    }

    public static class AscendingHackerThread extends HackerThread {
        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PWD; guess++) {
                if (vault.isPwdCorrect(guess)) {
                    this.pwdBroken(guess);
                }
            }
        }
    }

    public static class DescendingHackerThread extends HackerThread {
        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_PWD; guess >= 0; guess--) {
                if (vault.isPwdCorrect(guess)) {
                    this.pwdBroken(guess);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(i);
            }
            System.out.println("Game over!");
            System.exit(0);
        }
    }
}