package com.sap.mervyn.thread.ch7.diningphilosopher;

import com.sap.mervyn.thread.util.Debug;

public class FixedPhilosopher extends AbstractPhilosopher {
    private final Chopstick one;
    private final Chopstick theOther;

    public FixedPhilosopher(int id, Chopstick left, Chopstick right) {
        super(id, left, right);

        int leftHash = System.identityHashCode(left);
        int rightHash = System.identityHashCode(right);

        if (leftHash < rightHash) {
            one = left;
            theOther = right;
        } else if (leftHash > rightHash) {
            one = right;
            theOther = left;
        } else {
            one = null;
            theOther = null;
        }
    }

    @Override
    public void eat() {
        if (one != null) {
            synchronized (one) {
                Debug.info("%s is picking up %s on his %s...%n", this, one,
                        one == left ? "left" : "right");
                one.pickUp();
                synchronized (theOther) {
                    Debug.info("%s is picking up %s on his %s...%n", this,
                            theOther, theOther == left ? "left" : "right");
                    theOther.pickUp();
                    doEat();
                    theOther.putDown();
                }

                one.putDown();
            }
        } else {
            synchronized (FixedPhilosopher.class) {
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
}
