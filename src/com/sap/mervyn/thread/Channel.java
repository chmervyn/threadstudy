package com.sap.mervyn.thread;

public interface Channel<P> {

    void put(P product) throws InterruptedException;

    P take() throws InterruptedException;


}
