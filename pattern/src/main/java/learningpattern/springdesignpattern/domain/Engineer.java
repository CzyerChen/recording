package learningpattern.springdesignpattern.domain;

public class Engineer implements Worker {
    @Override
    public void checkComponents(CarComponent carComponent) {
        carComponent.setCorrect(true);
    }

    @Override
    public String getName() {
        return "qualified";
    }
}
