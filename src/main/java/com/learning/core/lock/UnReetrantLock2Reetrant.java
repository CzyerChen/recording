package com.learning.core.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Desciption：不可重入锁的一个可重入修改
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 28 15:35
 */
public class UnReetrantLock2Reetrant {

    private AtomicReference<Thread> owner = new AtomicReference<Thread>();
    private int state = 0;

    public void lock() {
        Thread current = Thread.currentThread();

        //如果当前锁线程和当前线程是同一个，直接判断返回，无需对锁进行判断了
        if (current == owner.get()) {
            state++;
            return;
        }
        //经典的锁自旋操作
        for (; ; ) {
            if (!owner.compareAndSet(null, current)) {
                return;
            }
        }
    }

    public void unLock() {
        Thread current = Thread.currentThread();
        if (current == owner.get()) {
            if (state != 0) {
                state--;
            } else {
                // 只有没有线程再使用当前对象的时候，才将当前锁释放
                owner.compareAndSet(current, null);
            }
        }

    }
}
