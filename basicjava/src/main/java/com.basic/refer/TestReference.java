package com.basic.refer;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Desciption：区分引用是为了更好地GC，更好地进行生命周期管理
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 30 14:25
 */
public class TestReference {
    public static  void main(String[] args){
        //强引用，默认引用类型，GC可达情况，即使内存溢出也不会被回收
        String a ="haha";

        //软引用，弱于强引用，根据JVM内存情况判定是否回收
        SoftReference<String> stringSoftReference = new SoftReference<>(new String("haha"));
        System.out.println("softReference "+stringSoftReference.get());

        //弱引用，弱于软引用，生命周期最短，在下一次GC之前就会被回收
        WeakReference<String> weakReference = new WeakReference<>(new String("haha"));
        System.gc();
        if (weakReference.get() == null) {
            System.out.println("weakReference 已被回收");
        }

        // 虚引用类型不会影响对象的生命周期，持有的引用随时可被GC回收
        PhantomReference<String> phantomReference = new PhantomReference<>(new String("haha"),new ReferenceQueue<String>());
        System.out.println("phantomReference "+phantomReference.get());
    }
}
