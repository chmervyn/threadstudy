package com.sap.mervyn.thread;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.SimpleTimeZone;

public abstract class AbstractStatTask implements Runnable {
    private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private final Calendar calendar;
    private final SimpleDateFormat sdf;
    private final int sampleInterval;
    protected final StatProcessor recordProcessor;

    public AbstractStatTask(int sampleInterval, int traceIdDiff, String expectedOperationName, String expectedExternalDeviceList) {
        this(sampleInterval, new RecordProcessor(sampleInterval, traceIdDiff, expectedOperationName, expectedExternalDeviceList));
    }

    public AbstractStatTask(int sampleInterval, StatProcessor recordProcessor) {
        SimpleTimeZone stz = new SimpleTimeZone(0, "UTC");
        this.sdf = new SimpleDateFormat(TIME_STAMP_FORMAT);
        sdf.setTimeZone(stz);
        this.calendar = Calendar.getInstance();
        this.sampleInterval = sampleInterval;
        this.recordProcessor = recordProcessor;
    }

    protected abstract void doCalculate() throws IOException;

    @Override
    public void run() {
        try {
            doCalculate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Map<Long, DelayItem> result = recordProcessor.getResult();
        report(result);
    }

    protected void report(Map<Long, DelayItem> summaryResult) {
        int sampleCount;
        final PrintStream ps = System.out;
        ps.printf("%s\t\t%s\t%s\t%s%n",
                "Timestamp", "AvgDelay(ms)", "TPS", "SampleCount");
        for (DelayItem delayStatData : summaryResult.values()) {
            sampleCount = delayStatData.getSampleCount().get();
            ps.printf("%s%8d%8d%8d%n",
                    getUTCTimeStamp(delayStatData
                            .getTimeStamp()), delayStatData.getTotalDelay().get()
                            / sampleCount,
                    sampleCount
                            / sampleInterval, sampleCount);
        }
    }

    private String getUTCTimeStamp(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
        String tempTs = sdf.format(calendar.getTime());
        return tempTs;
    }


}
