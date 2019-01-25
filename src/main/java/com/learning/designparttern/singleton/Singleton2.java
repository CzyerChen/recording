package com.learning.designparttern.singleton;


/**
 * 暴力锁机制，直接锁住对象，消耗很大，线程安全版 单例模式
 */
public class Singleton2 {
    private Singleton2 (){}

    private static Singleton2 singleton = null;

    public static Singleton2 getInstance() {
        synchronized (Singleton2.class) {
            if (singleton == null) {
                singleton = new Singleton2();
            }
        }
        return singleton;
    }
}
