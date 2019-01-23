package com.learning.core.thread.threadlocal;

import java.util.concurrent.TimeUnit;

/**
 * 描述:可以看到线程每个有不同的启动时间,但是结束时间也会不相同.
 * Created by bysocket on 16/3/8.
 */
public class SafeTest {
    public static void main(String[] args) {
        SafeTask task = new SafeTask();
        for (int i = 0 ; i < 10; i++) {
            Thread thread = new Thread(task);
            thread.start();

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
