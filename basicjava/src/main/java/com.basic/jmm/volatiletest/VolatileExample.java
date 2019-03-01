package com.basic.jmm.volatiletest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 01 15:39
 */
public class VolatileExample {
    int a = 0;
    volatile boolean flag = false;

    public void writer() {
        a = 1;                   //1
        flag = true;               //2
    }

    public void reader() {
        if (flag) {                //3
            int i =  a;           //4
        }
    }

}
