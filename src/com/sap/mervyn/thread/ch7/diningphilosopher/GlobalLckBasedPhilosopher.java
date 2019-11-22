package com.sap.mervyn.thread.ch7.diningphilosopher;

import com.sap.mervyn.thread.util.Debug;

public class GlobalLckBasedPhilosopher extends AbstractPhilosopher {

    private final static Object GLOBAL_LOCK = new Object();

    public GlobalLckBasedPhilosopher(int id, Chopstick left, Chopstick right) {
        super(id, left, right);
    }

    @Override
    public void eat() {
        synchronized (GLOBAL_LOCK) {
            Debug.info("%s is picking up %s on his left...%n", this, left);
            left.pickUp();
            Debug.info("%s is picking up %s on his right...%n", this, right);
            right.pickUp();
            doEat();
            right.putDown();
            left.putDown();
        }
    }


}
