package com.basic.jmm.volatiletest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 01 15:10
 */
public class VolatileTest {

    public static  void main(String[] args){
        OperationEntry entry = new OperationEntry();
        entry.setI(100);
        entry.increment();
        System.out.println(entry.getI());
        OperationEntryWithLock entry1 = new OperationEntryWithLock();
        entry1.setJ(100);
        entry1.increment();
        System.out.println(entry1.getJ());
    }
}
