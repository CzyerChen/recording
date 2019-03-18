package learningpattern.springdesignpattern.domain;

public class EmployeeObserver extends Observer{
    public void informEvent(){
        System.out.println("employee: the store is open");
        super.informEvent();
    }
}
