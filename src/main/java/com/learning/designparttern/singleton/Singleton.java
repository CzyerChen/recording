package com.learning.designparttern.singleton;

/**
 * 饿汉模式 推荐版 单例模式
 */
public class Singleton {
    private static class SingletonHolder{
        private static final Singleton INSTANCE = new Singleton();
    }
    private Singleton(){}
    private static final Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
