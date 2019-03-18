package learningpattern.springdesignpattern.domain;

public abstract class Observer {
    private boolean isInformed;

    public void informEvent(){
        this.isInformed = true;
    }

    public boolean isInformed() {
        return isInformed;
    }
}
