package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.domain.Person;

/**
 * 主要是通过获取配置传给父类内部构造器,类似于Lombok builder的操作
 */
public class JianshezheModel {
   public  static void main(String[] args){
       Person person = new Person.PersonBuilder()
               .setId(11)
               .setName("name")
               .setAge(20)
               .build();

       System.out.println(person.toString());
   }
}
