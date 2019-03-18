package learningpattern.springdesignpattern.interclass;

import com.test.domain.Audlt;
import com.test.domain.Child;

public class DailyLife implements ActionMaker {
    private ActionList actionList;

    public DailyLife(int age) {
        actionList = getActionByAge(age);
    }

    @Override
    public void dailyAction(int age) {
        if (age < 18) {
            actionList.go2School();
        } else if (age >= 18) {
            actionList.goWorking();
        }
    }

    private ActionList getActionByAge(int age) {
        if (isYoung(age)) {
            return new Child();
        }
        return new Audlt();
    }

    private boolean isYoung(int age) {
        if (age < 18) {
            return true;
        } else {
            return false;
        }
    }


}
