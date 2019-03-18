package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.interclass.AbstractTool;
import learningpattern.springdesignpattern.interclass.CakeTool;
import learningpattern.springdesignpattern.interclass.MealTool;

/**
 * 子步骤的某些步骤延迟执行
 *
 * AbstarctApplicationContext类使用模板方法，利用多个模板，将实现延迟到子类
 */
public class ModelMethod {

    public static  void main(String[] args){
        AbstractTool tool = new CakeTool();
        tool.construct();

        AbstractTool tool1 = new MealTool();
        tool1.construct();
    }
}
