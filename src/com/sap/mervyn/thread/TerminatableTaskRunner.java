package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Debug;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminatableTaskRunner implements TaskRunnerSpec {
    private final BlockingQueue<Runnable> channel;
    private volatile Thread workerThread;

    private volatile boolean inUse = true;
    private AtomicInteger reservations = new AtomicInteger(0);

    public TerminatableTaskRunner(BlockingQueue<Runnable> channel) {
        this.channel = channel;
        workerThread = new WorkerThread();
    }

    public TerminatableTaskRunner() {
        this(new LinkedBlockingQueue<>());
    }


    @Override
    public void init() {
        final Thread t = workerThread;
        if (t != null) {
            t.start();
        }
    }

    @Override
    public void submit(Runnable task) throws InterruptedException {
        channel.put(task);
        reservations.incrementAndGet();         // 1
    }

    public void shutDown() {
        Debug.info("Shutting down service...");
        inUse = false;          // 2

        final Thread t = workerThread;
        if (t != null) {
            t.interrupt();          // 3
        }
    }

    class WorkerThread extends Thread {

        @Override
        public void run() {
            Runnable task;

            try {
                for ( ; ; ) {
                    if (!inUse && reservations.get() <= 0) {            // 4
                        break;
                    }

                    task = channel.take();

                    try {
                        task.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    reservations.decrementAndGet();             // 5
                }
            } catch (InterruptedException e) {
                workerThread = null;
            }

            Debug.info("worker thread terminated.");
        }
    }
}
