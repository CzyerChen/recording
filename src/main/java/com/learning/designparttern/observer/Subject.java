package com.learning.designparttern.observer;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:24
 */
public interface Subject {

    public void register(Observer observer);

    public void remove(Observer observer);

    public void notifyAllObservers();
}
