package com.learning.designparttern.observer;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:35
 */
public class TestMain {

    public static  void main(String[] args){
        LearningSubject learning = new LearningSubject();
        Observer observer1 = new Observer1(learning);
        Observer observer2 = new Observer2(learning);

        learning.setMsg("发来贺电，通告喜讯");
        learning.setMsg("今天的你还好吗");
    }
}
