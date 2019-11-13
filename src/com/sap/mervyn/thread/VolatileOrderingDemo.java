package com.sap.mervyn.thread;

import com.sap.mervyn.thread.annotation.Actor;
import com.sap.mervyn.thread.annotation.ConcurrencyTest;
import com.sap.mervyn.thread.annotation.Expect;
import com.sap.mervyn.thread.annotation.Observer;
import com.sap.mervyn.thread.util.TestRunner;

@ConcurrencyTest(iterations = 200000)
public class VolatileOrderingDemo {

    private int dataA = 0;
    private long dataB = 0L;
    private String dataC = null;
    private volatile boolean ready = false;

    @Actor
    public void writer() {
        dataA = 1;
        dataB = 10000L;
        dataC = "Content......";
        ready = true;
    }

    @Observer({
            @Expect(desc = "Normal", expected = 1),
            @Expect(desc = "Impossible", expected = 2),
            @Expect(desc = "ready not true", expected = 3),
    })
    public int reader() {
        int result = 0;
        boolean allIsOk;

        if (ready) {
            allIsOk = (dataA == 1) && (dataB == 10000L) && ("Content......".equals(dataC));
            result = allIsOk ? 1 : 2;
        } else {
            result = 3;
        }

        return result;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        TestRunner.runTest(VolatileOrderingDemo.class);
    }

}
