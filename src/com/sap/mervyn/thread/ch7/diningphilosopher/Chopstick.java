package com.sap.mervyn.thread.ch7.diningphilosopher;

public class Chopstick {
    private final int id;
    private Status status = Status.PUT_DOWN;

    public static enum Status {
        PICKED_PU, PUT_DOWN
    }

    public Chopstick(int id) {
        this.id = id;
    }

    public void pickUp() {
        this.status = Status.PICKED_PU;
    }

    public void putDown() {
        this.status = Status.PUT_DOWN;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Chopstick-" + id;
    }
}
