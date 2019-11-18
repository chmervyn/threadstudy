package com.sap.mervyn.thread.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class Tools {

    private Tools() {}

    public static void startAndWaitTerminated(Thread... threads) throws InterruptedException {
        if (threads == null) throw new IllegalArgumentException("threads is null");

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    public static void startAndWaitTerminated(Iterable<Thread> threads) throws InterruptedException {
        if (threads == null) throw new IllegalArgumentException("threads is null");

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    public static void randomPause(int maxPauseTime) {
        int sleepTime = new Random().nextInt(maxPauseTime);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println("Exception inside Tools.randomPause, go to interrupt sleep thread");
            Thread.currentThread().interrupt();
        }
    }

    public static void silentClose(Closeable... closeables) {
        if (closeables == null) return;

        for (Closeable closeable : closeables) {
            if (closeable == null) continue;

            try {
                closeable.close();
            } catch (Exception ignored) {
                // do nothing
            }

        }
    }

    public static void split(String str, String[] result, char delimeter) {
        int partsCount = result.length;
        int posOfDelimeter;
        int fromIndex = 0;
        String recordField;
        int i = 0;
        while (i < partsCount) {
            posOfDelimeter = str.indexOf(delimeter, fromIndex);
            if (posOfDelimeter == -1) {
                recordField = str.substring(fromIndex);
                result[i] = recordField;
                break;
            }

            recordField = str.substring(fromIndex, posOfDelimeter);
            result[i] = recordField;
            i++;
            fromIndex = posOfDelimeter + 1;
        }


    }

}
