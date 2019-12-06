package com.sap.mervyn.thread.ch9;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledTaskDemo {
    private static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        int maxConsumption;
        int minConsumption;

        if (args.length >= 2) {
            minConsumption = Integer.valueOf(args[0]);
            maxConsumption = Integer.valueOf(args[1]);
        } else {
            minConsumption = 1000;
            maxConsumption = 1000;
        }

        ses.scheduleAtFixedRate(new SimulatedTask("scheduleAtFixedRate", maxConsumption, minConsumption), 0, 2, TimeUnit.SECONDS);
        ses.scheduleWithFixedDelay(new SimulatedTask("scheduleWithFixedDelay", maxConsumption, minConsumption), 0, 1, TimeUnit.SECONDS);
        Thread.sleep(20000);

        ses.shutdown();
    }

    private static class SimulatedTask implements Runnable {
        private String name;
        // 模拟任务执行耗时
        private final int maxConsumption;
        private final int minConsumption;
        private final AtomicInteger seq = new AtomicInteger(0);

        public SimulatedTask(String name, int maxConsumption, int minConsumption) {
            this.name = name;
            this.maxConsumption = maxConsumption;
            this.minConsumption = minConsumption;
        }

        @Override
        public void run() {
            try {
                // 模拟任务执行耗时
                Tools.randomPause(maxConsumption, minConsumption);
                Debug.info(name + "run-" + seq.incrementAndGet());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
