package learningpattern.springdesignpattern.domain;

public class Robot implements Cloneable{
    private String name;

    public String getName() {
        return name;
    }

    public Robot(String name) {
        this.name = name;
    }

    public Object clone() throws CloneNotSupportedException {
        return  super.clone();
    }

    @Override
    public String toString() {
        return "Robot{" +
                "name='" + name + '\'' +
                '}';
    }
}
