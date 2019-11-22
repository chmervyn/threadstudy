package com.sap.mervyn.thread.ch7;

import com.sap.mervyn.thread.util.Debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DeadLockDetector extends Thread {
    static final ThreadMXBean tmb = ManagementFactory.getThreadMXBean();

    private final long monitorInterval;

    public DeadLockDetector(long monitorInterval) {
        super("DeadLockDetector");
        setDaemon(true);
        this.monitorInterval = monitorInterval;
    }

    public DeadLockDetector() {
        this(2000);
    }

    public static ThreadInfo[] findDeadlockedThreads() {
        long[] ids = tmb.findDeadlockedThreads();
        return ids == null ? new ThreadInfo[0] : tmb.getThreadInfo(ids);
    }

    public static Thread findThreadById(long threadId) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (threadId == thread.getId()) {
                return thread;
            }
        }

        return null;
    }

    public static boolean interruptThread(long threadID) {
        Thread thread = findThreadById(threadID);
        if (thread != null) {
            thread.interrupt();
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        ThreadInfo[] threadInfoList;
        ThreadInfo ti;
        int i = 0;

        try {
            for ( ; ; ) {
                // 检测系统中是否存在死锁
                threadInfoList = findDeadlockedThreads();
                if (threadInfoList.length > 0) {
                    ti = threadInfoList[i++ % threadInfoList.length];
                    Debug.error("Deadlock detected,trying to recover"
                                    + " by interrupting%n thread(%d,%s)%n",
                            ti.getThreadId(),
                            ti.getThreadName());
                    interruptThread(ti.getThreadId());
                    continue;
                } else {
                    Debug.info("No deadlock found!");
                    i = 0;
                }

                Thread.sleep(monitorInterval);
            }
        } catch (InterruptedException e) {
        }
    }
}
