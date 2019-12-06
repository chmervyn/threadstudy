package com.sap.mervyn.thread.ch1;

public class WelcomeApp {

    public static void main(String[] args) {
        Thread welcomeThread = new WelcomeThread();
        welcomeThread.start();

        // 输出“当前线程”的线程名称
        System.out.printf("1.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }

}

class WelcomeThread extends Thread {
    // 在该方法中实现线程的任务处理逻辑
    @Override
    public void run() {
        System.out.printf("2.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }
}
