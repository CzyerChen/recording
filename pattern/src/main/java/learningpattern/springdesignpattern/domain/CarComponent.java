package learningpattern.springdesignpattern.domain;

public abstract class CarComponent {
    public boolean correct = false;

    public abstract  void beChecked(Worker worker);

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
