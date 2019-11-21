package com.sap.mervyn.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskRunner {
    private final BlockingQueue<Runnable> channel;
    private volatile Thread workerThread;

    public TaskRunner(BlockingQueue<Runnable> channel) {
        this.channel = channel;
        this.workerThread = new WorkerThread();
    }

    public TaskRunner() {
        this(new LinkedBlockingQueue<>());
    }

    public void init() {
        final Thread t = workerThread;
        if (t != null) {
            t.start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        channel.put(task);
    }

    class WorkerThread extends Thread {

        @Override
        public void run() {
            Runnable task;

            try {
                for ( ; ; ) {
                    task = channel.take();
                    task.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
