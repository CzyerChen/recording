package learningpattern.decorator.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 17:08
 */
public class TestMain {
    public  static  void  main(String[] args){
        System.out.println("------------有红宝石加成的鞋子---------------");
        Skill skill = new RedDiamondDecorator(new Shoes());
        System.out.println("法术伤害："+skill.magicHurt());
        System.out.println("物理伤害："+skill.physicalHurt());
        System.out.println("描述："+skill.description());

        System.out.println("------------有蓝宝石加成的小刀---------------");
        Skill skill1 = new BlueDiamondDecorator(new Knife());
        System.out.println("法术伤害："+skill1.magicHurt());
        System.out.println("物理伤害："+skill1.physicalHurt());
        System.out.println("描述："+skill1.description());
    }
}
