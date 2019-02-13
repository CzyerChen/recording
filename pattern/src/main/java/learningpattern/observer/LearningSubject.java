package learningpattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 25 19:27
 */
public class LearningSubject implements Subject {
   private List<Observer> list = new ArrayList<>();
   private String msg;

    @Override
    public void register(Observer observer) {
     list.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        int i = list.indexOf(observer);
        list.remove(i);
    }

    @Override
    public void notifyAllObservers() {
         for(Observer observer:list){
             observer.notice(msg);
         }
    }

    public void setMsg(String msg) {
        this.msg = msg;
        notifyAllObservers();
    }
}
