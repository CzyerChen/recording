package learningpattern.springdesignpattern.domain;

import com.test.interclass.ActionList;

public class Child  implements ActionList{

    @Override
    public void go2School() {
        System.out.println("child should go to school ");
    }

    @Override
    public void goWorking() {

    }
}
