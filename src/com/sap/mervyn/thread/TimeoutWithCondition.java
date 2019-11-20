package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutWithCondition {

    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static boolean ready = false;
    protected static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            for ( ; ; ) {
                lock.lock();

                ready = random.nextInt(100) < 5 ? true : false;

                if (ready) {
                    condition.signal();
                }

                lock.unlock();

                Tools.randomPause(500);
            }
        });

        t.setDaemon(true);
        t.start();

        waiter(1000);
    }

    private static void waiter(final long timeout) throws InterruptedException {
        if (timeout < 0) throw new IllegalArgumentException();

        final Date deadline = new Date(System.currentTimeMillis() + timeout);

        boolean continueToWait = true;

        lock.lock();
        while (!ready) {
            Debug.info("still not ready, continue to wait: %s", continueToWait);

            if (!continueToWait) {
              Debug.error("wait timed out, unable to execute target action!");
              return;
            }

            continueToWait = condition.awaitUntil(deadline);
        }

        guarededAction();

        lock.unlock();
    }

    private static void guarededAction() {
        Debug.info("Take some action.");
    }

}
