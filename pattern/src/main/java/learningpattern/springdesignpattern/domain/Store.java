package learningpattern.springdesignpattern.domain;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private  boolean isOpen;
    private List<Observer> listeners;

    public boolean isOpen() {
        return isOpen;
    }

    public List<Observer> getListeners() {
        if(listeners == null ){
            this.listeners = new ArrayList<Observer>();
        }
        return listeners;
    }

    public void open() {
        isOpen = true;
    }

    public void informAllObservers(){
        for(Observer observer: listeners){
                observer.informEvent();
        }
    }

    public  void addListner(Observer observer){
        getListeners().add(observer);
    }


}
