package com.learning.core.lock;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 28 15:24
 */
public class TestMain implements Runnable {
    static int sum;
    private  UnReetrantLock lock;

    TestMain(UnReetrantLock lock){
        this.lock = lock;
    }


    public static void main(String[] args){
        UnReetrantLock lock1 = new UnReetrantLock();

        for(int i = 0; i< 20;i++){
            TestMain testMain = new TestMain(lock1);
            Thread thread = new Thread(testMain);
            thread.start();
        }

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(sum);


    }

    @Override
    public void run() {
        this.lock.lock();
        sum++;
        this.lock.unLock();
    }


}
