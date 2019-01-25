package com.learning.designparttern.observer;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:35
 */
public class Observer2 implements Observer{
    private Subject subject;
    Observer2(Subject subject){
        this.subject = subject;
        subject.register(this);
    }

    @Override
    public void notice(String msg) {
        System.out.println("Observer2 通知消息:"+msg);
    }
}
