package learningpattern.springdesignpattern.domain;

public abstract class Coffee {
    protected double sugar = 0;
    protected double milk = 0;
    protected double price = 5;

    public abstract double makeMoreCandy();
    public abstract double makeMoreMilk();

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price += price;
    }
}
