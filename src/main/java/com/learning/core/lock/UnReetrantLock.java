package com.learning.core.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Desciption：不可重入锁
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 28 14:59
 */
public class UnReetrantLock {

    private AtomicReference<Thread>  owner= new AtomicReference<Thread>();

    public  void lock(){
        Thread current = Thread.currentThread();
        //经典的锁自旋操作
        for(;;){
            // 如果锁未被占用，则设置当前线程为锁的拥有者,将当前值与预期值对比，如果相等返回true, 不等返回false,都对当前值更新
            //Compare And Set : False return indicates that the actual value was not equal to the expected value
            if(!owner.compareAndSet(null,current)){
                return;
            }
        }
    }

    public void unLock(){
        Thread current = Thread.currentThread();
        // 只有锁的拥有者才能释放锁
        owner.compareAndSet(current,null);
    }


}
