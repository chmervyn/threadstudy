package com.sap.mervyn.thread.ch7.diningphilosopher;

import com.sap.mervyn.thread.util.Debug;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class FixedLockBasedPhilosopher extends BuggyLckBasedPhilosopher {

    public FixedLockBasedPhilosopher(int id, Chopstick left, Chopstick right) {
        super(id, left, right);
    }

    @Override
    protected boolean pickUpChopstick(Chopstick chopstick) {
        final ReentrantLock lock = LOCK_MAP.get(chopstick);
        boolean pickedUp = false;
        boolean lockAcquired = false;

        try {
            lockAcquired = lock.tryLock(20, TimeUnit.MILLISECONDS);

            if (!lockAcquired) {
                // 锁申请失败
                Debug.info("%s is trying to pick up %s on his %s,"
                                + "but it is held by other philosopher ...%n",
                        this, chopstick, chopstick == left ? "left" : "right");
                return false;
            }
        } catch (InterruptedException e) {
            Chopstick theOtherChopstick = chopstick == left ? right : left;
            if (LOCK_MAP.get(theOtherChopstick).isHeldByCurrentThread()) {
                theOtherChopstick.putDown();
                LOCK_MAP.get(theOtherChopstick).unlock();
            }

            return false;
        }

        try {
            Debug.info("%s is picking up %s on his %s...%n",
                    this, chopstick, chopstick == left ? "left" : "right");
            chopstick.pickUp();
            pickedUp = true;
        } catch (Exception e) {
            // 不大可能走到这里
            if (lockAcquired) {
                lock.unlock();
            }
            pickedUp = false;
            e.printStackTrace();
        }

        return pickedUp;
    }
}
