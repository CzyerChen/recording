package com.learning.designparttern.decorator.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 17:06
 */
public class BlueDiamondDecorator implements SkillDecorator {
    private Skill skill;

    BlueDiamondDecorator(Skill skill){
        this.skill = skill;
    }

    @Override
    public int magicHurt() {
        return this.skill.magicHurt()+20;
    }

    @Override
    public int physicalHurt() {
        return this.skill.physicalHurt()+5;
    }

    @Override
    public String description() {
        return this.skill.description()+" 添加蓝宝石";
    }
}
