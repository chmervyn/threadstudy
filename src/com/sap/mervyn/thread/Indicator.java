package com.sap.mervyn.thread;

import java.util.concurrent.atomic.AtomicLong;

public class Indicator {

    private static final Indicator INSTANCE = new Indicator();

    private final AtomicLong requestCount = new AtomicLong(0);

    private final AtomicLong successCount = new AtomicLong(0);

    private final AtomicLong failureCount = new AtomicLong(0);

    private Indicator() { }

    public static Indicator getInstance() {
        return INSTANCE;
    }

    public void newRequestReceived() {
        requestCount.getAndIncrement();
    }

    public void newRequestProcessed() {
        successCount.getAndIncrement();
    }

    public void requestProcessedFailed() {
        failureCount.getAndIncrement();
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailureCount() {
        return failureCount.get();
    }

    public void reset() {
        requestCount.set(0L);
        successCount.set(0L);
        failureCount.set(0L);
    }

    @Override
    public String toString() {
        return "Counter [" +
                "requestCount=" + requestCount +
                ", successCount=" + successCount +
                ", failureCount=" + failureCount +
                "]";
    }
}
