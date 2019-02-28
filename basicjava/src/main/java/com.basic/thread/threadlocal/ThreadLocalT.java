package com.basic.thread.threadlocal;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 30 14:49
 */
public class ThreadLocalT<T> /*implements Runnable*/ {

    private final int threadLocalHashCode = nextHashCode();
    private static final int HASH_INCREMENT = 0x61c88647;
    private static AtomicInteger nextHashCode = new AtomicInteger();


    static class ThreadLocalMapT {//静态内部类
        private ThreadLocalT.ThreadLocalMapT.Entry[] table;
        /**
         * The number of entries in the table.
         */
        private int size = 0;

        static class Entry extends WeakReference<ThreadLocalT<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocalT<?> k, Object v) {
                super(k);
                value = v;
            }
        }

        private ThreadLocalT.ThreadLocalMapT.Entry getEntry(ThreadLocalT<?> key) {
            //计算key的hash值
            int i = key.threadLocalHashCode & (table.length - 1);
            //获取entry对象
            ThreadLocalT.ThreadLocalMapT.Entry e = table[i];
            if (e != null && e.get() == key) {
                //获取对应的值
                return e;
            }else {
                return getEntryAfterMiss(key, i, e);
            }
        }

        private ThreadLocalT.ThreadLocalMapT.Entry getEntryAfterMiss(ThreadLocalT<?> key, int i, ThreadLocalT.ThreadLocalMapT.Entry e) {
            ThreadLocalT.ThreadLocalMapT.Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                ThreadLocalT<?> k = e.get();
                if (k == key) {
                    return e;
                }
                if (k == null) {
                    //清理key 为null的数据，这个清理没有很及时
                    expungeStaleEntry(i);
                }else {
                    //下一个hash地址进行探测
                    i = nextIndex(i, len);
                }
                e = tab[i];
            }
            return null;
        }

        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // (6)定位key，将对应的value引用解除
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocalT<?> k = e.get();
                //(7)如果key为null,则去掉对value的引用。
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null) {
                            h = nextIndex(h, len);
                        }
                        tab[h] = e;
                    }
                }
            }
            return i;
        }

        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * Set the value associated with key.
         *
         * @param key the thread local object
         * @param value the value to be set
         */
        private void set(ThreadLocalT<?> key, Object value) {

            // We don't use a fast path as with get() because it is at
            // least as common to use set() to create new entries as
            // it is to replace existing ones, in which case, a fast
            // path would fail more often than not.

            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);

            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                ThreadLocalT<?> k = e.get();

                if (k == key) {
                    e.value = value;
                    return;
                }

                if (k == null) {
                    /*Replace a stale entry encountered during a set operation
                     * with an entry for the specified key*/
                    //replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            //如果容量超过阈值，需要重哈希
            /*if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();*/
        }

        /**
         * Remove the entry for key.
         */
        private void remove(ThreadLocalT<?> key) {
            //(1)计算当前ThreadLocal变量所在table数组位置，尝试使用快速定位方法
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            //(2)这里使用循环是防止快速定位失效后，变量table数组
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                //（3）或许当前值
                if (e.get() == key) {
                    //(4)找到则调用WeakReference的clear方法清除对ThreadLocal的弱引用
                    e.clear();
                    //(5)清理key 为null的数据
                    expungeStaleEntry(i);
                    return;
                }
            }
        }
    }

    /**
     * 用于获取当前线程的副本变量值
     * @return
     */
    public T get() {
        ThreadT t = ThreadT.currentThread(); //当前线程
        ThreadLocalT.ThreadLocalMapT map = getMap(t); //获取map对象
        if (map != null) {
            //获取map中当前线程的entry对象
            T result = (T) map.getEntry(this);
            return result;
        }
        return setInitialValue();
    }



    /**
     * 用于设置当前线程的副本变量值。
     * @param value
     */
    public void set(T value){
        ThreadT t = ThreadT.currentThread();
        ThreadLocalMapT map = getMap(t);
        if (map != null) {
            map.set(this, value);
        }else {
            //没有map对象，就创建一个初始化容量的map
            //createMap(t, value);
        }
    }

    /**
     * 用于删除当前线程的副本变量值,一个map的remove操作
     */
    public void remove(){
        ThreadLocalMapT m = getMap(ThreadT.currentThread());
        if (m != null) {
            m.remove(this);
        }
    }

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    private ThreadLocalT.ThreadLocalMapT getMap(ThreadT t) {
        //返回本地线程中存有的map
        return t.threadLocalMapT;
    }


    private T setInitialValue() {
        T value = initialValue();
        ThreadT t = ThreadT.currentThread();
        ThreadLocalT.ThreadLocalMapT map = getMap(t);
        if (map != null) {
            map.set(this, value);
        }else{
            //如果没有map对象，就创建一个新的map
            //createMap(t, value);
        }

        return value;
    }

    protected T initialValue() {

        return null;
    }


}
