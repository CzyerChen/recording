package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.domain.Animal;

/**
 * 工厂方法是指通过一个静态定义的方法，通过公共静态方法对对象进行初始化
 */
public class GongchangModel {
   public  static void main(String[] args){
       Animal animal = Animal.getObject("cat");
       System.out.println(animal.toString());

   }
}
