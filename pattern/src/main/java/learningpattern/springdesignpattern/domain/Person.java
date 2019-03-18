package learningpattern.springdesignpattern.domain;

public class Person {
    private int id;
    private String name;
    private int age;

    private Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static class PersonBuilder{
      private int id;
      private String name;
      private int age;

      public PersonBuilder setId(int id) {
          this.id = id;
          return  this;
      }

      public PersonBuilder setName(String name) {
          this.name = name;
          return  this;
      }

      public PersonBuilder setAge(int age) {
          this.age = age;
          return  this;
      }



      public  Person build(){
          return new Person(this.id,this.name,this.age);
      }
  }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
