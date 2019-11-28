package com.sap.mervyn.thread.util;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Tools {

    private static final Random rnd = new Random();

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
        int sleepTime = rnd.nextInt(maxPauseTime);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println("Exception inside Tools.randomPause, go to interrupt sleep thread");
            Thread.currentThread().interrupt();         // 保留线程中断标志
        }
    }

    public static void randomPause(int maxPauseTime, int minPauseTime) {
        int sleepTime = maxPauseTime == minPauseTime ? minPauseTime : rnd
                .nextInt(maxPauseTime - minPauseTime) + minPauseTime;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
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

    public static String md5sum(final InputStream in) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buff = new byte[1024];
        try (DigestInputStream dis = new DigestInputStream(in, md)) {
            while (dis.read(buff) != -1) {

            }
        }

        byte[] digest= md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String checkSum = bigInt.toString(16);

        while (checkSum.length() < 32) {
            checkSum = "0" + checkSum;
        }

        return checkSum;
    }

    public static String md5sum(final File file) throws IOException, NoSuchAlgorithmException {
        return md5sum(new BufferedInputStream(new FileInputStream(file)));
    }

    public static String md5sum(String str) throws IOException, NoSuchAlgorithmException {
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
        return md5sum(in);
    }

    public static void delayedAction(String prompt, Runnable action, int delay /*seconds*/) {
        Debug.info("%s in %d seconds.", prompt, delay);
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException ignored) {
        }
        action.run();
    }

}
