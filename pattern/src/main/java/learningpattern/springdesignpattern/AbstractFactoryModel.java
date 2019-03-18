package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.interclass.AnimalActions;
import learningpattern.springdesignpattern.interclass.AnimalFactory;

public class AbstractFactoryModel {

    public static  void main(String[] args){
        AnimalFactory factory = new AnimalActions();
        factory.eatSomething("猫","睡觉");
    }
}
