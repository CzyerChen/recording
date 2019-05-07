package com.basic.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    private Lock lock = new ReentrantLock();//默认参数false,是非公平锁
    private Condition condition = lock.newCondition();//创建Condition
    /**
     * await signal
     * wait notify
     */
    public  void test(){
        try{
            lock.lock();
            //1 wait
            System.out.print("尝试wait");
            condition.await();
            //2. 使用signal唤醒
            condition.signal();
            for (int i = 0; i < 5; i++) {
                System.out.println("ThreadName=" + Thread.currentThread().getName()+ (" " + (i + 1)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
