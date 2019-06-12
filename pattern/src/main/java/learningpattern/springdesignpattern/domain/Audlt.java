package learningpattern.springdesignpattern.domain;


import learningpattern.springdesignpattern.interclass.ActionList;

public class Audlt implements ActionList {

    @Override
    public void go2School() {

    }

    @Override
    public void goWorking() {
        System.out.println("audlt should go to work");
    }
}
