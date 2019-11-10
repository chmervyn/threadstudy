package com.sap.mervyn.thread.util;

import java.util.Random;

public class Tools {

    private Tools() {}

    public static void randomPause(int maxPauseTime) {
        int sleepTime = new Random().nextInt(maxPauseTime);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println("Exception inside Tools.randomPause, go to interrupt sleep thread");
            Thread.currentThread().interrupt();
        }
    }

}
