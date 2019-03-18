package learningpattern.springdesignpattern.domain;

import com.test.interclass.AnimalInterface;

public class Cat extends ParentAnimal implements AnimalInterface {

    public Cat(String name, String something) {
        super(name, something);
    }

    @Override
    public void eat() {
        System.out.println(super.name+" 喜欢吃 "+super.something+" 喜欢薄荷");
    }
}
