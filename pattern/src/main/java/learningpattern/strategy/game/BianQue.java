package learningpattern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:00
 */
public class BianQue extends Role {

    BianQue(String name){
        this.name = name;
        this.profile = new Profile("扁鹊大人","男","治疗很昂贵");
        this.skill = new Skill("我毒","扔个大毒瓶子");
    }
}
