package com.sap.mervyn.thread.singleton;

public class IncorrectDCLSingleton {

    private static IncorrectDCLSingleton instance = null;

    private IncorrectDCLSingleton() {}

    /**
     * 对于双检索单例模式，依然有潜在问题
     * 同步块的new IncorrectDCLSingleton(),涉及3个操作
     *      1. objeRef = allocate(IncorrectDCLSingleton.class)
     *      2. invokeConstructor(objRef)
     *      3. instance = objRef
     * JIT编译器可能将上诉步骤中的2，3重排序，instance不为null，但是初始化还没完成
     * 这个时候如果有其他线程走到第一重检查，发现instance不为null，直接返回instance，但此时instance初始化还没有完成，导致后续的异常
     * 防止JIT编译器在临界区内重排序，用volatile修饰instance
     * @return
     */
    public static IncorrectDCLSingleton getInstance() {
        if (instance == null) {
            synchronized (IncorrectDCLSingleton.class) {
                if (instance == null) {
                    instance = new IncorrectDCLSingleton();
                }
            }
        }

        return instance;
    }

    public void someService() {

    }

}
