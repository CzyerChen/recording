package learningpattern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 14:43
 */
public class Cat extends Animal {

    Cat(){
         this.name = "你逗它开心";
    }

    @Override
    public void hobby() {
        System.out.println("动物的特点有：高冷，粘人，有超高魅力值");
    }
}
