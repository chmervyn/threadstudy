package com.sap.mervyn.thread.ch7;

import com.sap.mervyn.thread.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NestedMonitorLockoutDemo1 {
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    private int processed = 0;
    private int accepted = 0;

    public static void main(String[] args) throws InterruptedException {
        NestedMonitorLockoutDemo1 demo = new NestedMonitorLockoutDemo1();
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

    protected synchronized void doProcess(String msg) throws InterruptedException {
        // 不要在临界区内调用BlockingQueue的阻塞方法！那样会导致嵌套监视器锁死
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
                    String msg = queue.take();
                    doProcess(msg);
                }
            } catch (InterruptedException e) {

            }
        }
    }


}
