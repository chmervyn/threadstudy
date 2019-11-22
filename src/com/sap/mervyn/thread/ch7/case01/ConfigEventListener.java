package com.sap.mervyn.thread.ch7.case01;

import java.util.Map;

public interface ConfigEventListener {

    void onConfigLoaded(Configuration cfg);

    void onConfigUpdated(String name, int newVersion, Map<String, String> properties);

}
