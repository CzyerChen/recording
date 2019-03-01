package com.basic.jmm.volatiletest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 01 15:17
 */
public class OperationEntryWithLock {

    private volatile  int j =0;


    public  synchronized  void setJ(int j ){
        this.j = j;
    }

    public void  increment(){
        int j = getJ();
        j++;
        setJ(j);
    }

    public  synchronized  int getJ(){
        return  this.j;
    }
}
