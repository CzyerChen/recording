package com.learning.designparttern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:23
 */
public class PetAnimalStore2 implements AbstractAnimalStore {

    @Override
    public Animal sellCat() {
        return new Cat();
    }

    @Override
    public Animal sellDog() {
        return new Dog();
    }
}
