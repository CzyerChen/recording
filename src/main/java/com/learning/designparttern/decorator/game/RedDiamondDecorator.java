package com.learning.designparttern.decorator.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 17:01
 */
public class RedDiamondDecorator implements SkillDecorator {

    private Skill skill;
    RedDiamondDecorator(Skill skill){
        this.skill =skill;
    }


    @Override
    public int magicHurt() {
        return this.skill.magicHurt()+2;
    }

    @Override
    public int physicalHurt() {
        return this.skill.physicalHurt()+15;
    }

    @Override
    public String description() {
        return this.skill.description()+" 添加红宝石";
    }
}
