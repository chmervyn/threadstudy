package com.sap.mervyn.thread.ch7.diningphilosopher;

import com.sap.mervyn.thread.util.Debug;

public class DeadLockingPhilosopher extends AbstractPhilosopher {

    public DeadLockingPhilosopher(int id, Chopstick left, Chopstick right) {
        super(id, left, right);
    }

    @Override
    public void eat() {
        synchronized (left) {
            Debug.info("%s is picking up %s on his left...%n", this, left);
            left.pickUp();
            synchronized (right) {
                Debug.info("%s is picking up %s on his right...%n", this, right);
                right.pickUp();
                doEat();
                right.putDown();
            }

            left.putDown();
        }
    }


}
