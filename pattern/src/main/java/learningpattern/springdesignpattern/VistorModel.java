package learningpattern.springdesignpattern;

import learningpattern.springdesignpattern.domain.Car;
import learningpattern.springdesignpattern.domain.CarComponent;
import learningpattern.springdesignpattern.domain.Engineer;
import learningpattern.springdesignpattern.domain.Worker;

/**
 * 访问者模式 Spring在Beans配置过程中使用了访问者设计模式
 * 通过另一种类型的对象来使一个对象访问
 *
 * 工程师检修汽车，汽车有汽车零件，只有当工程师有检修权限，才能对车的每一个零件进行检查，不是工程师的人员，默认车是合格的
 * 汽车零件接口（轮子，窗户，车门。。。被检修动作），工程师接口（工程师，非工程师，检修操作），汽车有一个是否合格的标志
 *
 * BeanDefinitionVistor对象，用于解析Bean元数据，将其解析为String
 */
public class VistorModel {

    public  static void main(String[] args){
        Worker worker = new Engineer();
        CarComponent car = new Car();
        car.beChecked(worker);
    }
}
