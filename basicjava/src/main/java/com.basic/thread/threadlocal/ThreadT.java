package com.basic.thread.threadlocal;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 30 15:03
 */
public class ThreadT  implements  Runnable{
    ThreadLocalT.ThreadLocalMapT threadLocalMapT = new ThreadLocalT.ThreadLocalMapT();
    public static native ThreadT currentThread();
    ThreadT(){

    }
    @Override
    public void run() {

    }

}
