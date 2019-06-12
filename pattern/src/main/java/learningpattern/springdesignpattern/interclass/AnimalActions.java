package learningpattern.springdesignpattern.interclass;


import learningpattern.springdesignpattern.domain.Cat;
import learningpattern.springdesignpattern.domain.Dog;

public class AnimalActions implements AnimalFactory {
    @Override
    public void eatSomething(String name,String something) {
        switch (name){
            case "猫":
                Cat cat = new Cat(name,something);cat.eat();break;
            case "狗":
                Dog dog = new Dog(name,something);dog.eat();break;
        }
    }
}
