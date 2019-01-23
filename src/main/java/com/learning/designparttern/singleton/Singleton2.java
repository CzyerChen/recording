package com.learning.designparttern.singleton;

/**
 * 描述：线程安全版 单例模式
 * Created by 子木 on 2016/2/28.
 */

/**
 * 暴力锁机制，直接锁住对象，小号很大
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
