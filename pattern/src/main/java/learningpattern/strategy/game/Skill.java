package learningpattern.strategy.game;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:51
 */
public class Skill {
    private String commonAttack;
    private String specialAttack;

    public Skill(String commonAttack, String specialAttack) {
        this.commonAttack = commonAttack;
        this.specialAttack = specialAttack;
    }

    public String getCommonAttack() {
        return commonAttack;
    }

    public void setCommonAttack(String commonAttack) {
        this.commonAttack = commonAttack;
    }

    public String getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(String specialAttack) {
        this.specialAttack = specialAttack;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "commonAttack='" + commonAttack + '\'' +
                ", specialAttack='" + specialAttack + '\'' +
                '}';
    }

    public void showSkill(){
        System.out.println("英雄技能:"+this.toString());
    }
}
