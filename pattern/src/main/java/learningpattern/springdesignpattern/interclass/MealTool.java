package learningpattern.springdesignpattern.interclass;


public class MealTool extends AbstractTool {
    @Override
    public void material() {
        System.out.println("原材料 蔬菜肉");
    }

    @Override
    public void makeDeal() {
        System.out.println("铁锅炒菜");
    }
}
