package com.sap.mervyn.thread.ch7;

import com.sap.mervyn.thread.ch7.diningphilosopher.DiningPhilosopherProblem;

public class DeadLockRecoveryDemo {

    public static void main(String[] args) throws Exception {
        new DeadLockDetector().start();

        DiningPhilosopherProblem.main(args);
    }

}
