package com.sap.mervyn.thread.ch9;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicTaskResultHandlingDemo {
    private final static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        final String host = args.length > 0 ? args[0] : "baidu.com";
        final AsyncTask<Integer> asyncTask = new AsyncTask<Integer>() {
            private final Random rnd = new Random();
            private final String targetHost = host;

            @Override
            protected void onResult(Integer result) {
                // 将结果保存到数据库
                saveToDatabase(result);
            }

            @Override
            public Integer call() throws Exception {
                return pingHost();
            }

            private Integer pingHost() {
                // 模拟实际操作耗时
                Tools.randomPause(2000);
                // 模拟的探测结果码
                return Integer.valueOf(rnd.nextInt(4));
            }

            private void saveToDatabase(Integer result) {
                Debug.info(targetHost + " status: " + result);
                Tools.randomPause(1000);
            }

            @Override
            public String toString() {
                return "Ping " + targetHost + ", " + super.toString();
            }
        };

        ses.scheduleAtFixedRate(asyncTask, 0, 3, TimeUnit.SECONDS);

        Tools.delayedAction("The ScheduledExecutorService will be shutdown", () -> ses.shutdown(), 60);
    }
}
