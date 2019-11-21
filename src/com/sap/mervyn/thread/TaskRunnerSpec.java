package com.sap.mervyn.thread;

public interface TaskRunnerSpec {

    void init();

    void submit(Runnable task) throws InterruptedException;

}
