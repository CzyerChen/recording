package learningpattern.springdesignpattern.domain;

import java.util.Collections;

public class BlackCoffee extends Coffee {

    @Override
    public double makeMoreCandy() {
        return 0.0;
    }

    @Override
    public double makeMoreMilk() {
        return 0.0;
    }

    public double getPrice(){
        return  this.price;
    }
}
