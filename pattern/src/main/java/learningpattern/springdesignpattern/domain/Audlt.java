package learningpattern.springdesignpattern.domain;

import com.test.interclass.ActionList;

public class Audlt implements ActionList {

    @Override
    public void go2School() {

    }

    @Override
    public void goWorking() {
        System.out.println("audlt should go to work");
    }
}
