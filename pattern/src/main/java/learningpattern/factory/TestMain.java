package learningpattern.factory;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 14:54
 */
public class TestMain {

    public static void main(String[] args){
        //简单工厂
      /* AnimalStore animalStore = new AnimalStore(new SimpleAnimalFactory());
       animalStore.sellAnimal("你逗它开心");*/

      //工厂方法
      /*  PetAnimalStore1 petAnimalStore1 = new PetAnimalStore1();
        petAnimalStore1.sellAnimal("它逗你开心");*/

      //抽象工厂
        AbstractAnimalStore petAnimalStore2 = new PetAnimalStore2();
        petAnimalStore2.sellCat().hobby();
        petAnimalStore2.sellDog().hobby();
    }
}
