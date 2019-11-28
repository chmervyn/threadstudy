package com.sap.mervyn.thread.ch8;

import com.sap.mervyn.thread.util.Debug;
import com.sap.mervyn.thread.util.Tools;
import sun.nio.ch.ThreadPool;

import java.io.File;
import java.util.concurrent.*;

public class TaskResultRetrievalDemo {

    private static final int N_CPU = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, N_CPU * 2, 4, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        TaskResultRetrievalDemo demo = new TaskResultRetrievalDemo();
        Future<String> future = demo.recognizeImage("/Users/i347764/Pictures/SAPDesktop.png");

        doSomething();

        try {
            Debug.info(future.get());
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void doSomething() {
        Tools.randomPause(200);
    }

    private Future<String> recognizeImage(final String imageFile) {
        return executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return doRecognizeImage(new File(imageFile));
            }
        });
    }

    protected String doRecognizeImage(File imageFile) {
        String result;
        String[] simulatedResults = {"苏K271LU", "苏K272LU", "苏K273LU"};
        result  = simulatedResults[(int)(Math.random() * simulatedResults.length)];
        Tools.randomPause(100);

        return result;
    }

}
