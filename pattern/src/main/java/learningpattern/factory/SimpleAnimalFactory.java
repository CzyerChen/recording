package learningpattern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 14:47
 */
public class SimpleAnimalFactory {

    public Animal raiseAnimal(String type){
        Animal animal = null;
        if("你逗它开心".equals(type)){
            animal = new Cat();
        }else  if("它逗你开心".equals(type)){
            animal = new Dog();
        }
        return animal;
    }
}
