package com.learning.designparttern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 14:51
 */
public class AnimalStore {

    private SimpleAnimalFactory simpleAnimalFactory;


    AnimalStore(SimpleAnimalFactory factory){
        this.simpleAnimalFactory = factory;
    }

    public void sellAnimal(String type){
        Animal animal = simpleAnimalFactory.raiseAnimal(type);
        animal.hobby();
    }
}
