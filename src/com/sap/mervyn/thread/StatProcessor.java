package com.sap.mervyn.thread;

import java.util.Map;

public interface StatProcessor {
    void process(String record);

    Map<Long, DelayItem> getResult();
}
