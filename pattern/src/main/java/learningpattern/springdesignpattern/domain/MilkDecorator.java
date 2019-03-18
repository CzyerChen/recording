package learningpattern.springdesignpattern.domain;

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    public double makeMoreMilk() {
        return super.makeMoreMilk() +8;
    }

    public double getPrice(){
        return super.getPrice()+6;
    }

}
