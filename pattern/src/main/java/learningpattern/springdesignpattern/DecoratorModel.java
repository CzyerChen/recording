package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.domain.BlackCoffee;
import learningpattern.springdesignpattern.domain.Coffee;
import learningpattern.springdesignpattern.domain.MilkDecorator;
import learningpattern.springdesignpattern.domain.SugarDecorator;

/**
 * 第三人称，上帝视角，将不同的组件拿来组装
 * 给对象添加角色
 * 咖啡 --> 黑咖啡 ，加糖，加奶
 * Spring管理缓存的同步事务当中有装饰器的设计模式
 * TransactionAwareCacheDecorator  是cache的装饰器
 *
 */
public class DecoratorModel {
    public  static void main(String[] args){
        Coffee coffee = new MilkDecorator(new SugarDecorator(new BlackCoffee()));
        System.out.println("咖啡价格："+coffee.getPrice());
    }
}
