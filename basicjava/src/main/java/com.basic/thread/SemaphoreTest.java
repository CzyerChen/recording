package com.basic.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    public void test(){
        Semaphore semephore = new Semaphore(10);
        try {
            semephore.acquire();
            try {
                // do something
            }catch (Exception e){

            }finally {
                semephore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
