package com.learning.designparttern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 15:01
 */
abstract public class AnimalStore1 {

    public abstract Animal raiseAnimal(String type);

    public void sellAnimal(String type){
        Animal animal = raiseAnimal(type);
        animal.hobby();
    }
}
