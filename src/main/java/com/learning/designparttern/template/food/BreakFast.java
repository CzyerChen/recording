package com.learning.designparttern.template.food;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:30
 */
public class BreakFast extends OneMeal{
    BreakFast(String name){
        this.name = name;
    }

    @Override
    protected void cook() {
        System.out.println(this.name +":放上米饭，开始煮粥");
    }
}
