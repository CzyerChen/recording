package learningpattern.springdesignpattern.domain;

public class CoffeeDecorator extends Coffee {
    protected  Coffee  coffee;

    public CoffeeDecorator(Coffee coffee){
        this.coffee = coffee;
    }

    @Override
    public double makeMoreCandy() {
        return coffee.makeMoreCandy();
    }

    @Override
    public double makeMoreMilk() {
        return coffee.makeMoreMilk();
    }

    public double getPrice(){
        return  this.coffee.price;
    }
}
