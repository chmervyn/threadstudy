package com.sap.mervyn.thread.ch8;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadMonitorDemo {
    volatile boolean inited = false;
    static int threadIndex = 0;
    final static Logger LOGGER = Logger.getAnonymousLogger();
    final BlockingQueue<String> channel = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        ThreadMonitorDemo demo = new ThreadMonitorDemo();
        demo.init();

        for (int i = 0; i < 100; i++) {
            demo.service("test-" + i);
        }

        TimeUnit.MILLISECONDS.sleep(2000);
        System.exit(0);
    }

    public synchronized void init() {
        if (inited) {
            return;
        }
        Debug.info("init...");
        WorkerThread wt = new WorkerThread();
        wt.setName("Worker0-" + threadIndex++);
        wt.setUncaughtExceptionHandler((t, e) -> {
            Debug.info("Current thread is `t`:%s, it is still alive:%s",
                    Thread.currentThread() == t, t.isAlive());

            String threadInfo = t.getName();
            LOGGER.log(Level.SEVERE, threadInfo + " terminated", e);

            LOGGER.info("About to restart " + threadInfo);

            inited = false;
            init();
        });

        wt.start();
        inited = true;
    }

    public void service(String message) throws InterruptedException {
        channel.put(message);
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            Debug.info("Do something important...");
            String msg;

            try {
                for ( ; ; ) {
                    msg = channel.take();
                    process(msg);
                }
            } catch (InterruptedException e) {

            }
        }

        private void process(String message) {
            Debug.info(message);

            int i = (int) (Math.random() * 100);
            if (i < 2) {
                throw new RuntimeException("test");
            }

            Tools.randomPause(100);
        }
    }



}
