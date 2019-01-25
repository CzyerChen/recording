package com.learning.designparttern.decorator.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:58
 */
public class Knife implements Skill {
    @Override
    public int magicHurt() {
        return +50;
    }

    @Override
    public int physicalHurt() {
        return +10;
    }

    @Override
    public String description() {
        return "携带小刀";
    }


}
