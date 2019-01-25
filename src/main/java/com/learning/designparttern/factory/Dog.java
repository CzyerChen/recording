package com.learning.designparttern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 14:46
 */
public class Dog  extends Animal{
    Dog(){
        this.name = "它逗你开心";
    }

    @Override
    public void hobby() {
        System.out.println("动物的特点有：呆、蠢、萌、傻");
    }
}
