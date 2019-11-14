package com.sap.mervyn.thread.singleton;

public class StaticHolderSingleton {

    private StaticHolderSingleton() {
        System.out.println("StaticHolderSingleton init......");
    }

    private static class InstanceHolder {
        final static StaticHolderSingleton INSTANCE = new StaticHolderSingleton();
    }

    public static StaticHolderSingleton getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void someService() {
        System.out.println("someService invoked......");
    }

    public static void main(String[] args) {
        StaticHolderSingleton.getInstance().someService();
    }

}
