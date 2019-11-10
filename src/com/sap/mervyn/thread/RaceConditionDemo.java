package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Tools;

import java.util.Arrays;

public class RaceConditionDemo {

    public static void main(String[] args) {
        int numberOfThreads = args.length > 0 ? Short.valueOf(args[0]) : Runtime.getRuntime().availableProcessors();
        Thread[] workerThreads = new Thread[numberOfThreads];

        for (int i = 0; i < workerThreads.length; i++) {
            workerThreads[i] = new WorkerThread(i, 10);
        }

        Arrays.stream(workerThreads).forEach(workerThread -> workerThread.start());
    }

    static class WorkerThread extends Thread {
        private final int requestCount;

        public WorkerThread(int id, int requestCount) {
            super("worker-" + id);
            this.requestCount = requestCount;
        }

        @Override
        public void run() {
            int i = requestCount;
            String requestID;

            RequestIdGenerator requestIDGen = RequestIdGenerator.getInstance();
            while (i-- > 0) {
                requestID = requestIDGen.nextID();
                processRequest(requestID);
            }
        }

        private void processRequest(String requestID) {
            Tools.randomPause(50);
            System.out.printf("%s got requestID: %s %n", Thread.currentThread().getName(), requestID);
        }
    }

}
