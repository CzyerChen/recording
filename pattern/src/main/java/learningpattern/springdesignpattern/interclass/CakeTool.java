package learningpattern.springdesignpattern.interclass;

import com.test.interclass.AbstractTool;

public class CakeTool extends AbstractTool {

    @Override
    public void material() {
        System.out.println("原材料 面粉");
    }

    @Override
    public void makeDeal() {
        System.out.println("通过烤箱烘焙");
    }
}
