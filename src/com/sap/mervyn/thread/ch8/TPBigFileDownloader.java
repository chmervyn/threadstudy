package com.sap.mervyn.thread.ch8;

import com.sap.mervyn.thread.BigFileDownloader;
import com.sap.mervyn.thread.DownloadTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TPBigFileDownloader extends BigFileDownloader {
    private static final int N_CPU = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, N_CPU * 2, 4, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(N_CPU * 8), new ThreadPoolExecutor.CallerRunsPolicy());

    public TPBigFileDownloader(String file) throws Exception {
        super(file);
    }

    public static void main(String[] args) throws Exception {
        String url = "https://www-eu.apache.org/dist//ant/binaries/apache-ant-1.9.14-bin.zip";
        TPBigFileDownloader downloader = new TPBigFileDownloader(url);
        long reportInterval = 10;

        final int taskCount = N_CPU * 8;
        downloader.download(taskCount, reportInterval * 1000);
    }

    @Override
    protected void dispatchWork(final DownloadTask dt, int workerIndex) {
        executor.submit(() -> {
            try {
                dt.run();
            } catch (Exception e) {
                e.printStackTrace();
                cancelDownload();
            }
        });
    }

    @Override
    protected void doCleanup() {
        executor.shutdownNow();
        super.doCleanup();
    }




}
