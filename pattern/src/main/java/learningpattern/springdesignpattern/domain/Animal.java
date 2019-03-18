package learningpattern.springdesignpattern.domain;

public class Animal {
    private String zhonglei;
    private int weight;

    public String getZhonglei() {
        return zhonglei;
    }

    public void setZhonglei(String zhonglei) {
        this.zhonglei = zhonglei;
    }

    public int getWeight() {
        return weight;
    }


    public void setWeight(int weight) {
        this.weight = weight;
    }

    private Animal(String zhonglei, int weight) {
        this.zhonglei = zhonglei;
        this.weight = weight;
    }

    public  static Animal getObject(String name){
        Animal animal = null;
        switch (name) {
            case "monkey":animal = new Animal("猴子",20);break;
            case "cat":animal = new Animal("猫",5);break;
            case "dog":animal = new Animal("狗",50);break;
        }
        return  animal;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "zhonglei='" + zhonglei + '\'' +
                ", weight=" + weight +
                '}';
    }
}
