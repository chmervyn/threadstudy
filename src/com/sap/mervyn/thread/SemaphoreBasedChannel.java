package com.sap.mervyn.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class SemaphoreBasedChannel<P> implements Channel<P> {
    private final BlockingQueue<P> queue;
    private final Semaphore semaphore;

    public SemaphoreBasedChannel(BlockingQueue<P> queue, int flowLimit) {
        this(queue, flowLimit, false);
    }

    public SemaphoreBasedChannel(BlockingQueue<P> queue, int flowLimit, boolean isFair) {
        this.queue = queue;
        semaphore = new Semaphore(flowLimit, isFair);
    }


    @Override
    public void put(P product) throws InterruptedException {
        semaphore.acquire();

        try {
            queue.put(product);
        } finally {
            semaphore.release();
        }

    }

    @Override
    public P take() throws InterruptedException {
        return queue.take();
    }
}
