package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.interclass.ActionMaker;
import learningpattern.springdesignpattern.interclass.ActionMakerImpl;

/**
 * Spring使用适配器处理不同的servlet容器中加载时编织。
 * 在面向切面编程中使用load time weaving 一种方式实在类加载的是后在AspectJ的方面注入字节码，另一种是在编译时候注入或对已经编译的类进行静态注入
 * classloading.joss
 * 第一人称视角，实行拿来主义
 */
public class AdapterModel {

    public  static void main(String[] args){
        ActionMaker actionMaker = new ActionMakerImpl();
        actionMaker.dailyAction(11);
        actionMaker.dailyAction(40);
        actionMaker.dailyAction(18);
    }
}
