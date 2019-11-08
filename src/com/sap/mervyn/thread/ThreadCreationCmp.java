package com.sap.mervyn.thread;

public class ThreadCreationCmp {

    public static void main(String[] args) {
        Thread t;
        CountingTask ct = new CountingTask();

        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("available processors: " + numberOfProcessors);

        for (int i = 0; i < (numberOfProcessors >>> 2); i++) {
            t = new Thread(ct);
            t.start();
        }

        for (int i = 0; i < (numberOfProcessors >>> 2); i++) {
            t = new CountingThread();
            t.start();
        }


    }

    static class Counter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int value() {
            return count;
        }
    }

    static class CountingTask implements Runnable {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i  < 100; i++) {
                doSomething();
                counter.increment();
            }

            System.out.println("Counting task: " + counter.value());
        }

        private void doSomething() {
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CountingThread extends Thread {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i  < 100; i++) {
                doSomething();
                counter.increment();
            }

            System.out.println("Counting thread: " + counter.value());
        }

        private void doSomething() {
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
