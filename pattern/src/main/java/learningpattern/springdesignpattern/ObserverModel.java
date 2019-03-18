package learningpattern.springdesignpattern;



import learningpattern.springdesignpattern.domain.EmployeeObserver;
import learningpattern.springdesignpattern.domain.KeeperObserver;
import learningpattern.springdesignpattern.domain.Observer;
import learningpattern.springdesignpattern.domain.Store;

/**
 * 观察者模式运用的很多，也很好理解，在队列上有很好的实践
 * 队列里面有事件，每个事件有各样的监听着，当事件发生动作，每一个观察者就能被通知到
 * Spring 中ApplicationListener的实现
 *
 */
public class ObserverModel {
    public  static void main(String[] args){
        Observer keeper = new KeeperObserver();
        Observer employee= new EmployeeObserver();

        Store store = new Store();
        store.addListner(keeper);
        store.addListner(employee);

        store.open();
        store.informAllObservers();

        System.out.println(keeper.isInformed());
        System.out.println(employee.isInformed());

    }
}
