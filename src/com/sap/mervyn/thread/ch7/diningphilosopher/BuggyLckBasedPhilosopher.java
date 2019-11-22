package com.sap.mervyn.thread.ch7.diningphilosopher;

import com.sap.mervyn.thread.util.Debug;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BuggyLckBasedPhilosopher extends AbstractPhilosopher {

    protected final static ConcurrentHashMap<Chopstick, ReentrantLock> LOCK_MAP;
    static {
        LOCK_MAP = new ConcurrentHashMap<>();
    }

    public BuggyLckBasedPhilosopher(int id, Chopstick left, Chopstick right) {
        super(id, left, right);
        LOCK_MAP.putIfAbsent(left, new ReentrantLock());
        LOCK_MAP.putIfAbsent(right, new ReentrantLock());
    }

    @Override
    public void eat() {
        if (pickUpChopstick(left) && pickUpChopstick(right)) {
            try {
                doEat();
            } finally {
                putDownChopsticks(right, left);
            }
        }
    }

    protected boolean pickUpChopstick(Chopstick chopstick) {
        final ReentrantLock lock = LOCK_MAP.get(chopstick);
        lock.lock();

        try {
            Debug.info("%s is picking up %s on his %s...%n", this, chopstick, chopstick == left ? "left" : "right");
            chopstick.pickUp();
        } catch (Exception e) {
            e.printStackTrace();
            lock.unlock();
            return false;
        }

        return true;
    }

    private void putDownChopsticks(Chopstick chopstick1, Chopstick chopstick2) {
        try {
            putDownChopstick(chopstick1);
        } finally {
            putDownChopstick(chopstick2);
        }
    }

    protected void putDownChopstick(Chopstick chopstick) {
        final ReentrantLock lock = LOCK_MAP.get(chopstick);
        try {
            Debug.info("%s is putting down %s on his %s...%n", this, chopstick, chopstick == left ? "left" : "right");
            chopstick.putDown();
        } finally {
            lock.unlock();
        }
    }

}
