#### 一. 23种设计模式
- `创建型模式`：
    单例模式、抽象工厂模式、建造者模式、工厂模式、原型模式
- `结构型模式`：
    适配器模式、桥接模式、装饰模式、组合模式、外观模式、享元模式、代理模式
- `行为型模式`：
    模版方法模式、命令模式、迭代器模式、观察者模式、中介者模式、备忘录模式、解释器模式、状态模式、策略模式、职责链模式(责任链模式)、访问者模式


#### 二. 设计模式有很多，在jdk源码当中就涉及(可以结合jdk学习，也可以看我的小demo)：

`代理模式`：代理模式可以体现在springAOP中，采用代理的方法，来控制对对应方法的访问和操作

`策略模式`:关注点分离的思想，将变与不变的操作分离，可以支持用户的自定义输入，Array.sort方法允许用户传入Compare方法

`模板方法`: 提供统一接口，供不同的继承和实现

`装饰者模式`：将原有数据增添新的方法和处理，将它转换为新的接口，需要装饰类参数需要传递被装饰类

`适配器模式`：adapter，将不同的数据类型转换为统一的接口向外提供服务

`迭代器模式`：iterator

`享元模式`：IntegerCache

`单例模式`：控制全局仅有一个对象存在，比如各种数据库连接等


#### 三. 当前代码介绍单例方法、回调方法、装饰者模式、策略模式、模板方法，以下一一介绍

- 设计模式是一个很抽象的概念，小白们觉得很神奇，大神们觉得得心应手
- 它描述的是代码组织层面的概念，是被反复实践总结、代码设计的经验
- 根据一些模式的书写规范，可以达到代码思路清晰，扩展性强，后期维护优雅

#### 1. 单例模式
- `单例模式的使用前提`:
  避免多个实例造成资源的浪费，增大垃圾回收的压力，并且多个实例并存可能导致调用的错误
- `单例模式的含义`：
  使用单例，能够保证整个应用中有且仅有一个实例对象
- `单例模式的使用场景`：
  保证内存中对象的唯一性，比如常用工具类、线程池、缓存、数据库、配置文件等
- `单例的设计思想`
  1) 私有化类的构造函数
  2) 通过new在本类中创造对象
  3) 定义一个公共可访问的方法，将创建的对象返回
- `单例模式的类型`
  
1)懒汉式：采用懒加载的想法，用到的时候再初始化使用，但是在多线程的情况下容易出现竞态条件，导致线程不安全

**普通版本：**
```java
public class Singleton {
 
	private static Singleton instance=null;
	
	private Singleton() {};
	
	public static Singleton getInstance(){
		
		if(instance==null){
			instance=new Singleton();
		}
		return instance;
	}
}

```
**加锁版本：**
当然硬是要使用懒汉式，可以在获取对象的getInstance方法上面加锁来保证安全，可是就丧失了性能，后面会介绍更优的方法实现
```java
public class Singleton {
 
	private static Singleton instance=null;
	
	private Singleton() {};
	
	public static synchronized Singleton getInstance(){
		
		if(instance==null){
			instance=new Singleton();
		}
		return instance;
	}
}

```

2)饿汉式：不管三七二十一，在应用初始化加载的时候就加载这个类的实例，虽然对一些必要的类比如配置文件读取，当然就希望如此，但是对于一些单例的暂且不用的对象，初始化就加载会加大内存调度的浪费

**可用1**
```java
public class Singleton {
 
	private static Singleton instance=new Singleton();
	private Singleton(){};
	public static Singleton getInstance(){
		return instance;
	}
}
```
    优点：简单，在类加载的时候就完成了实例化，避免了线程的同步问题

    缺点：由于在类加载的时候就实例化了，所以没有达到懒加载的效果，没有考虑使用就初始化加载会造成内存的浪费

**可用2**
```java
public class Singleton{
 
	private static Singleton instance = null;
	
	static {
		instance = new Singleton();
	}
 
	private Singleton() {};
 
	public static Singleton getInstance() {
		return instance;
	}
}
```
    也有初始化加载，通过静态代码块的方式实现初始化


  3) 双重校验锁：是懒汉式的一种变种，是在使用的时候进行对象加载，依靠volatile实现内存可见，依靠synchronized实现同步方法，保证延迟加载和线程安全
  
```java
public class Singleton {

    public volatile  Singleton _INSTANCE = null;

    public Singleton getInstance(){
        if(_INSTANCE == null){
            synchronized (Singleton.class){
                if(_INSTANCE == null){
                    _INSTANCE =  new Singleton();
                }
            }
        }
        return  _INSTANCE;
    }
}
```

4)内部类[推荐用]
```java
public class Singleton{
 
	
	private Singleton() {};
	
	private static class SingletonHolder{
		private static Singleton instance=new Singleton();
	} 
	
	public static Singleton getInstance(){
		return SingletonHolder.instance;
	}
}
```
    这种方式跟饿汉式方式采用的机制类似，但又有不同。
    
    两者都是采用了类装载的机制来保证初始化实例时只有一个线程。不同的地方在饿汉式方式是只要Singleton类被装载就会实例化，没有Lazy-Loading的作用，而静态内部类方式在Singleton类被装载时
    并不会立即实例化，而是在需要实例化时，调用getInstance方法，才会装载SingletonHolder类，从而完成Singleton的实例化。
    类的静态属性只会在第一次加载类的时候初始化，所以在这里，JVM帮助我们保证了线程的安全性，在类进行初始化时，别的线程是无法进入的。

5)枚举[极推荐使用]：但只支持JDK1.5
```java
public enum SingletonEnum {
	
	 instance; 
	 
	 private SingletonEnum() {}
	 
	 public void method(){
	 }
}
```
访问方式：
```
SingletonEnum.instance.method();
```
    不仅能避免多线程同步问题，而且还能防止反序列化重新创建新的对象。可能是因为枚举在JDK1.5中才添加，所以在实际项目开发中，很少见人这么写过，这种方式也是最好的一种方式，如果在开发中JDK满足要求的情况下建议使用这种方式。

#### 2. 工厂模式
例子好像不是特别的好，跟着看定义有点怪怪的

1)抽象工厂模式
     Animal + Cat + Dog + AbstractAnimalStore + PetAnimalStore2 + TestMain

    定义接口，由子类确定接口的实现方法

    抽象工厂模式是工厂方法模式的升级版本，他用来创建一组相关或者相互依赖的对象。他与工厂方法模式的区别就在于，工厂方法模式针对的是一个产品等级结构；而抽象工厂模式则是针对的多个产品等级结构

    在抽象工厂模式中，有一个产品族的概念：所谓的产品族，是指位于不同产品等级结构中功能相关联的产品组成的家族

2)简单工厂模式
    Animal + Cat + Dog + AnimalStore + SimpleAnimalFactory + TestMain

    将对象的初始化放在工厂父类中做判断
    
3)静态工厂模式
    
    项目中的辅助类
    
4)工厂方法模式
    Animal + Cat + Dog + PetAnimalStore1 + AnimalStore1 + TestMain

    将对象的初始化放在工厂子类中做判断

#### 3.策略模式
    策略模式（Strategy Pattern）：定义了算法族，分别封装起来，让它们之间可相互替换，此模式让算法的变化独立于使用算法的客户。将变与不变的特质很好的分开，支持属性的自定义
 
要求：
- 封装变化：把可能变化的代码封装起来，能够提供灵活的变动
- 多用组合，少用继承，组合可以不改变当前类灵活的修改实现，降低耦合
- 针对接口编程，不针对实现

代码见Role + Profile + Skill + LuBan7hao +BianQue


#### 4.模板方法模式
    模板方法：模版方法使得子类可以在不改变算法结构的情况下，重新定义算法的步骤。

代码见 OneMeal + BreakFast + Lunch +Dinner +TestMain

#### 5.装饰者模式
    装饰者模式：为原有的实现动态添加一些责任，提供弹性的替代方式
   

