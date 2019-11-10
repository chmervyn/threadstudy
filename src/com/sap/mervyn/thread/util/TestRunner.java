package com.sap.mervyn.thread.util;

import com.sap.mervyn.thread.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@ConcurrencyTest
public class TestRunner {
    private static final Semaphore FLOW_CONTROL = new Semaphore(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MAX_PRIORITY);
            t.setDaemon(false);
            return t;
        }
    });

    private volatile boolean stop = false;
    private final AtomicInteger runs = new AtomicInteger(0);
    private final int iterations;
    private final int thinkTime;
    private final Method publishMethod;
    private final Method observerMethod;
    private volatile Method setupMethod = null;
    private final Object testCase;
    private final SortedMap<Integer, ExpectInfo> expectMap;

    public TestRunner(Method publishMethod, Method observerMethod,
                      Method setupMethod, Object testCase) {
        this.publishMethod = publishMethod;
        this.observerMethod = observerMethod;
        this.setupMethod = setupMethod;
        this.testCase = testCase;
        this.expectMap = parseExpects(getExpects(observerMethod));
        ConcurrencyTest testCaseAnn = testCase.getClass().getAnnotation(ConcurrencyTest.class);
        iterations = testCaseAnn.iterations();
        thinkTime = testCaseAnn.thinkTime();
    }

    private static class ExpectInfo {
        public final String description;
        private final AtomicInteger counter;

        public ExpectInfo(String description) { this(description, 0); }

        public ExpectInfo(String description, int hitCount) {
            this.description = description;
            this.counter = new AtomicInteger(hitCount);
        }

        public int hit() {
            return counter.incrementAndGet();
        }

        public int count() {
            return counter.get();
        }
    }

    private static Expect[] getExpects(final Method observerMethod) {
        Observer observerAnn = observerMethod.getAnnotation(Observer.class);
        Expect[] expects = observerAnn.value();
        return expects;
    }

    private static SortedMap<Integer, ExpectInfo> parseExpects(final Expect[] expects) {
        SortedMap<Integer, ExpectInfo> map = new ConcurrentSkipListMap<>();
        Arrays.stream(expects).forEach(expect -> map.put(Integer.valueOf(expect.expected()), new ExpectInfo(expect.desc())));

        return map;
    }

    public static void runTest(Class<?> testCaseClazz) throws IllegalAccessException, InstantiationException {
        Object testCase = testCaseClazz.newInstance();
        Method publishMethod = null;
        Method observerMethod = null;
        Method setupMethod = null;

        for (Method method : testCaseClazz.getMethods()) {
            if (method.getAnnotation(Actor.class) != null) {
                publishMethod = method;
            }

            if (method.getAnnotation(Observer.class) != null) {
                observerMethod = method;
            }

            if (method.getAnnotation(Setup.class) != null) {
                setupMethod = method;
            }
        }

        TestRunner runner = new TestRunner(publishMethod, observerMethod, setupMethod, testCase);
        runner.doTest();
    }

    protected void doTest() {

        Runnable publishTask = new Runnable() {
            @Override
            public void run() {
                try {
                    publishMethod.invoke(testCase, new Object[] {});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    FLOW_CONTROL.release(1);
                }
            }
        };

        Runnable observerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    int result = -1;

                    try {
                        result = Integer.valueOf(observerMethod.invoke(testCase, new Object[]{}).toString());
                        ExpectInfo expectInfo = expectMap.get(Integer.valueOf(result));

                        if (expectInfo != null) {
                            expectInfo.hit();
                        } else {
                            expectInfo = new ExpectInfo("unexpected", 1);
                            ((ConcurrentMap<Integer, ExpectInfo>) expectMap).putIfAbsent(result, expectInfo);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } finally {
                    FLOW_CONTROL.release(1);
                }
            }
        };

        CountDownLatch latch;
        while (!stop) {
            latch = createLatch();
            if (setupMethod != null) {
                try {
                    setupMethod.invoke(testCase, new Object[] {});
                } catch (Exception e) {
                    break;
                }
            }

            schedule(observerTask, latch);
            schedule(publishTask, latch);

            if (runs.incrementAndGet() >= iterations) {
                break;
            }

            if (thinkTime > 0) {
                Tools.randomPause(thinkTime);
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                ;
            }
        }

        EXECUTOR_SERVICE.shutdown();

        try {
            EXECUTOR_SERVICE.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ;
        }

        report();
    }

    private static class DummyLatch extends CountDownLatch {

        public DummyLatch(int count) { super(count); }

        @Override
        public void await() throws InterruptedException {
            ;
        }

        @Override
        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public void countDown() { ; }

        @Override
        public long getCount() { return 0; }

    }

    private CountDownLatch createLatch() {
        CountDownLatch latch;

        if (setupMethod != null) {
            latch = new CountDownLatch(2);
        } else {
            latch = new DummyLatch(2);
        }

        return latch;
    }

    protected void report() {
        ExpectInfo ei;
        StringBuilder sb = new StringBuilder();
        sb.append("\n\r<<Simple Concurrency Test Framework report>>:");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        sb.append("\n\r===========================" + sdf.format(new Date()) + "=================================");

        for (Map.Entry<Integer, ExpectInfo> entry : expectMap.entrySet()) {
            ei = entry.getValue();
            sb.append("\n\rexpected:" + entry.getKey() + "		occurrences:"
                    + ei.count() + "		==>\t" + ei.description);
        }

        sb.append("\n\r=====================================END=============================================");
        System.out.println(sb);
    }

    protected void schedule(final Runnable task, final CountDownLatch latch) {
        try {
            FLOW_CONTROL.acquire(1);
        } catch (InterruptedException e) {
            latch.countDown();
            return;
        }

        EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    FLOW_CONTROL.release(1);
                    latch.countDown();
                }
            }
        });
    }
}
