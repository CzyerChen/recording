package learningpattern.springdesignpattern.domain;

public class KeeperObserver extends Observer {
    public void informEvent(){
        System.out.println("keeper:the store is open");
        super.informEvent();
    }
}
