package learningpattern.springdesignpattern.domain;

public abstract class Human {
    public int age;

    public abstract void  goWorking();

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Human(int age) {
        this.age = age;
    }
}
