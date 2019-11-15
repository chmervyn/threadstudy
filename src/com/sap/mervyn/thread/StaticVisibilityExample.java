package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.HashMap;
import java.util.Map;

public class StaticVisibilityExample {
    private static Map<String, String> taskConfig;

    static {
        Debug.info("The class being initialized...");
        taskConfig = new HashMap<String, String>();                 // 1
        taskConfig.put("url", "https://github.com/chmervyn");       // 2
        taskConfig.put("timeout", "1000");                          // 3
    }

    public static void changeConfig(String url, int timeout) {
        taskConfig = new HashMap<String, String>();                 // 4
        taskConfig.put("url", url);                                 // 5
        taskConfig.put("timeout", String.valueOf(timeout));         // 6
    }

    public static void init() {
        // 该线程至少能够看到语句1~3的操作结果，而能否看到语句4～6的操作结果是没有保障的
        Thread t = new Thread(() -> {
            String url = taskConfig.get("url");
            String timeout = taskConfig.get("timeout");
            doTask(url, Integer.valueOf(timeout));
        });

        t.start();
    }

    private static void doTask(String url, int timeout) {

        Tools.randomPause(500);
    }


}
