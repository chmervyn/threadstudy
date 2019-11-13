package com.sap.mervyn.thread;

public class SpeculativeLoadExample {
    private boolean ready = false;
    private int[] data = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

    public void writer() {
        int[] newData = new int[] {1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < newData.length; i++) {
            // 此处包含读内存的操作
            newData[i] = newData[i] - i;
        }

        data = newData;     //语句1
        // 此处包含写内存的操作
        ready = true;       //语句2
    }

    public int reader() {
        int sum = 0;
        int[] snapshot;

        if (ready) {    // 语句3 （if语句）
            snapshot = data;
            for (int i = 0; i < snapshot.length; i++) {     // 语句4 (for循环语句)
                sum += snapshot[i];      // 语句5
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        SpeculativeLoadExample test = new SpeculativeLoadExample();
        Runnable executeReader = () -> System.out.println(test.reader());
        Runnable executeWriter = () -> test.writer();

        new Thread(executeWriter).start();
        new Thread(executeReader).start();
    }


}
