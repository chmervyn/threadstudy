package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class CASBasedCounter {
    private volatile long count;

    /**
     * 这里使用AtomicLongFieldUpdater只是为了便于讲解和运行该实例
     * 实际上更多的情况是我们不适用AtomicLongFieldUpdater，
     * 而是使用java.util.concurrent.atomic包下的其他更为直接的类,如AtomicLong
     */
    private final AtomicLongFieldUpdater<CASBasedCounter> fieldUpdater;

    public CASBasedCounter() {
        fieldUpdater = AtomicLongFieldUpdater.newUpdater(CASBasedCounter.class, "count");
    }

    public long value() { return count; }

    public void increment() {
        long oldValue;
        long newValue;

        do {
            oldValue = count;           // 读取共享变量当前值
            newValue = oldValue + 1;    // 计算共享变量的新值
        } while (/* 调用CAS来更新共享变量的值 */ !compareAndSwap(oldValue, newValue));
    }

    private boolean compareAndSwap(long oldValue, long newValue) {
        return fieldUpdater.compareAndSet(this, oldValue, newValue);
    }

    public static void main(String[] args) throws InterruptedException {
        final CASBasedCounter counter = new CASBasedCounter();
        Thread t;
        Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            t = new Thread(() -> {
                Tools.randomPause(50);
                counter.increment();
            });

            threads.add(t);
        }

        for (int i = 0; i < 8; i++) {
            t = new Thread(() -> {
                Tools.randomPause(50);
                System.out.println(counter.value());
            });

            threads.add(t);
        }

        Tools.startAndWaitTerminated(threads);

        Debug.info("final count: " + counter.value());

    }




}
