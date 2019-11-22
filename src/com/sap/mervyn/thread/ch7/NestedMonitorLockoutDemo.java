package com.sap.mervyn.thread.ch7;

import com.sap.mervyn.thread.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NestedMonitorLockoutDemo {
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    private int processed = 0;
    private int accepted = 0;

    public static void main(String[] args) throws InterruptedException {
        NestedMonitorLockoutDemo demo = new NestedMonitorLockoutDemo();
        demo.start();

        int i = 0;
        while (i-- < 100000) {
            demo.accept("message" + i);
            Tools.randomPause(100);
        }
    }

    public void start() {
        new WorkerThread("Consumer").start();
    }

    public synchronized void accept(String msg) throws InterruptedException {
        // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
        queue.put(msg);
        accepted++;
    }

    protected synchronized void doProcess() throws InterruptedException {
        // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
        String msg = queue.take();
        System.out.println("Process: " + msg);
        processed++;
    }

    class WorkerThread extends Thread {

        public WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    doProcess();
                }
            } catch (InterruptedException e) {

            }
        }
    }


}
