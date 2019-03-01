package com.basic.jmm.volatiletest;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 01 15:11
 */
public class OperationEntry {

    private  volatile  int i =0;

    public  void setI(int i){
        this.i = i;
    }

    public  int getI(){
        return  this.i;
    }

    public  void  increment(){
        i++;
    }

}
