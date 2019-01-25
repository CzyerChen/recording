package com.learning.designparttern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:00
 */
public class TestMain {
    public static  void main(String[] args){
        Role a = new LuBan7hao("鲁班七号");
       /* a.showRoleInfo();
        a.showAttackInfo();*/
        a.setProfile(new Profile("鲁班小子","男","冲锋枪，突突突"));
        a.setSkill(new Skill("我突突突你","扔个大招，人头都是我的"));
        a.showRoleInfo();
        a.showAttackInfo();
    }
}
