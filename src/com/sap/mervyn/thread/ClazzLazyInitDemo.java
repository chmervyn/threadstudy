package com.sap.mervyn.thread;

import com.sap.mervyn.thread.util.Debug;

public class ClazzLazyInitDemo {

    static class Collaborator {
        static int number = 1;
        static boolean flag = true;

        static {
            Debug.info("Collaborator initializing... ");
        }
    }

    public static void main(String[] args) {
        Debug.info(Collaborator.class.hashCode());
        Debug.info(Collaborator.number);
        Debug.info(Collaborator.flag);
    }
}
