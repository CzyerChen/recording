package learningpattern.springdesignpattern.domain;

public abstract class ParentAnimal {
    public String name;
    public String something;

    public ParentAnimal(String name, String something) {
        this.name = name;
        this.something = something;
    }
}
