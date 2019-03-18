package learningpattern.springdesignpattern.domain;

public class SugarDecorator  extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
      super(coffee);
    }
    public double makeMoreCandy() {
        return super.makeMoreCandy()+2;
    }

    public double getPrice(){
       return super.getPrice() +4;
    }
}
