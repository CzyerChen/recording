package learningpattern.decorator.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 16:50
 */
public class Shoes implements Skill {
    @Override
    public int magicHurt() {
        return +10;
    }

    @Override
    public int physicalHurt() {
        return +30 ;
    }

    @Override
    public String description() {
        return "购买鞋子";
    }
}
