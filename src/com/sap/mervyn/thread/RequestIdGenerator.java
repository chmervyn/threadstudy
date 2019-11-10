package com.sap.mervyn.thread;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestIdGenerator implements CircularSeqGenerator {
    private static RequestIdGenerator INSTANCE = new RequestIdGenerator();
    private static final int SEQ_UPPER_LIMIT = 999;
    private int sequence = -1;

    private RequestIdGenerator() {}

    public static RequestIdGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public int nextSequence() {
        if (sequence >= SEQ_UPPER_LIMIT) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }

    public String nextID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String timeStamp = sdf.format(new Date());
        DecimalFormat df = new DecimalFormat("000");

        int sequenceNo = nextSequence();
        return "0049" + timeStamp + df.format(sequenceNo);
    }

}
