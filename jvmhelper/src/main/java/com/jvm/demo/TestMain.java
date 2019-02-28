package com.jvm.demo;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 31 14:23
 */
public class TestMain {
    public static  void main(String[] args){
        Person person = new Person();
        person.setName("claire");
        person.setAge(11);

        System.out.println(person);
    }
}
