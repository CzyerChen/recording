package learningpattern.springdesignpattern.domain;

public class Window extends CarComponent {
    @Override
    public void beChecked(Worker worker) {
        worker.checkComponents(this);
    }
}
