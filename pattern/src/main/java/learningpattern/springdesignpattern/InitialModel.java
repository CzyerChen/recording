package learningpattern.springdesignpattern;


import learningpattern.springdesignpattern.domain.Robot;

/**
 * 原型是通过复制已经存在的对象，创建一个实力对象，这个副本是真正的副本，所有的属性都和原对象一致
 * AbstractBeanFactory使用一种特定的原型模型，他将初始化Bean原型作用域
 * Bean的定义是基于配置文件xml中的定义，
 */
public class InitialModel {

    public static  void main(String[] args) throws CloneNotSupportedException {
        Robot first = new Robot("Nancy1111");
        Robot second = (Robot) first.clone();
        System.out.println(first.toString());
        System.out.println(second.toString());
    }
}
