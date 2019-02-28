package com.basic;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CopyOnWriteArrayListT{

   public static void main(String[] args){
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            Thread t = new Thread();
            System.out.println("==self designed==");
            return t;
         }
      });
      threadPoolExecutor.submit(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      });
   }
}