package learningpattern.springdesignpattern.domain;

import com.test.interclass.AnimalInterface;

public class Dog extends ParentAnimal implements AnimalInterface{


    public Dog(String name, String something) {
        super(name, something);
    }

    @Override
    public void eat() {
        System.out.println(super.name+" 喜欢吃 "+super.something+" 喜欢玩水");
    }
}
