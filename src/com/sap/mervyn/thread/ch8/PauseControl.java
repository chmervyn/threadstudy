package com.sap.mervyn.thread.ch8;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PauseControl extends ReentrantLock {

    private volatile boolean suspended = false;
    private final Condition condSuspended = newCondition();

    /**
     * 暂停线程
     */
    public void requestPause() {
        suspended = true;
    }

    /**
     * 恢复线程
     */
    public void proceed() {
        lock();
        try {
            suspended = false;
            condSuspended.signalAll();
        } finally {
            unlock();
        }
    }

    public void pauseIfNeccessary(Runnable targetAction) throws InterruptedException {
        lock();

        try {
            while (suspended) {
                condSuspended.await();
            }

            targetAction.run();
        } finally {
            unlock();
        }
    }

}
