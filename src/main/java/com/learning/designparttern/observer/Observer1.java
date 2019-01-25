package com.learning.designparttern.observer;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:33
 */
public class Observer1 implements Observer{
    private Subject subject;
    Observer1(Subject subject){
        this.subject = subject;
        subject.register(this);
    }

    @Override
    public void notice(String msg) {
        System.out.println("Observer1 通知消息:"+msg);
    }
}
