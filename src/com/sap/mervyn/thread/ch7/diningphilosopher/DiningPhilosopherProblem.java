package com.sap.mervyn.thread.ch7.diningphilosopher;

import java.lang.reflect.Constructor;

public class DiningPhilosopherProblem {

    public static void main(String[] args) throws Exception {
        int numberOfPhilosopher;
        numberOfPhilosopher = args.length > 0 ? Integer.valueOf(args[0]) : 2;

        Chopstick[] chopsticks = new Chopstick[numberOfPhilosopher];
        for (int i = 0; i < numberOfPhilosopher; i++) {
            chopsticks[i] = new Chopstick(i);
        }

        //String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "DeadLockingPhilosopher";
        //String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "BuggyLckBasedPhilosopher";
        //String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "GlobalLckBasedPhilosopher";
        //String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "FixedPhilosopher";
        //String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "FixedLockBasedPhilosopher";
        String philosopherImplClassName = args.length > 1 ? String.valueOf(args[1]) : "RecoverablePhilosopher";

        createPhilosopher(philosopherImplClassName, chopsticks);
    }

    private static void createPhilosopher(String philosopherImplClassName, Chopstick[] chopsticks) throws Exception {
        Class<Philosopher> philosopherClass = (Class<Philosopher>) Class.forName(DiningPhilosopherProblem.class.getPackage().getName() + "." + philosopherImplClassName);
        Constructor<Philosopher> constructor = philosopherClass.getConstructor(int.class, Chopstick.class, Chopstick.class);

        for (int i = 0; i < chopsticks.length; i++) {
            Philosopher philosopher = constructor.newInstance(i, chopsticks[i], chopsticks[(i + 1) % chopsticks.length]);
            philosopher.start();
        }
    }

}
