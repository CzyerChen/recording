package com.learning.designparttern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:48
 */
public abstract class Role {
    protected  String name;
    protected  Profile profile;
    protected  Skill skill;


    public Role setProfile(Profile profile) {
        this.profile = profile;
        return  this;
    }


    public Role setSkill(Skill skill) {
        this.skill = skill;
        return  this;
    }

    protected void showRoleInfo(){
        this.profile.showProfile();
    }

    protected void showAttackInfo(){
        this.skill.showSkill();
    }


}
