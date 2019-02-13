package learningpattern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:53
 */
public class LuBan7hao extends Role {

    LuBan7hao(String name){
        this.name = name;
        this.profile = new Profile("鲁班七号","尚不明确","鲁班大师，智商250，模板，金刚模板");
        this.skill = new Skill("普通暴击","大炮");
    }
}
