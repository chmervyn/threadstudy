package com.sap.mervyn.thread.singleton;

public class EnumBasedSingletonExample {


    private static enum Singleton {
        INSTANCE();

        Singleton() {
            System.out.println("Singleton init......");
        }

        public void someService() {
            System.out.println("someService invoked......");
        }
    }

    public static void main(String[] args) {
        Singleton.INSTANCE.someService();
    }

}
