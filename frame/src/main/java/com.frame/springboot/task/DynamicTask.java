package com.frame.springboot.task;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 19:16
 */
public class DynamicTask implements Runnable {
    @Override
    public void run() {
        System.out.println("This is a dynamic task demo");
    }
}
