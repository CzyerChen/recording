package learningpattern.springdesignpattern.interclass;

public class ActionMakerImpl implements ActionMaker {
    @Override
    public void dailyAction(int age) {
        ActionMaker actionMaker = new DailyLife(age);
        actionMaker.dailyAction(age);
    }
}
