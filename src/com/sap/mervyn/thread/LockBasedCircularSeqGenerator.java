package com.sap.mervyn.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedCircularSeqGenerator implements CircularSeqGenerator {
    private final int MAX_SEQUENCE = 999;
    private int sequence = -1;
    private final Lock lock = new ReentrantLock();

    @Override
    public int nextSequence() {
        lock.lock();

        try {
            if (sequence >= MAX_SEQUENCE)  sequence = 0;
            else sequence++;
            return sequence;
        } finally {
            lock.unlock();
        }
    }


}
