package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Tools;

public class ThreadStartVisibility {
    private static int data = 0;

    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Tools.randomPause(50);

                System.out.println(data);
            }
        };

        data = 1;

        thread.start();

        Tools.randomPause(50);

        data = 2;
    }

}
