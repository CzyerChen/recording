#### 1.简单工厂模式
- BeanFactory 是简单工厂，根据传入一个唯一的标识来获得bean对象，不属于23种设计模式之一
- 简单工厂模式的实质是由一个工厂类根据传入的参数，动态决定应该创建哪一个产品类。


#### 2.工厂方法模式
- 为了将对象的创建和使用相分离，即应用程序将对象的创建及初始化职责交给工厂对象
```text
import java.util.Random;
public class StaticFactoryBean {
      public static Integer createRandom() {
           return new Integer(new Random().nextInt());
       }
}
```


#### 3.单例模式
- Spring中类加载的默认方式都是单例，可以通过singleton=“true|false” 或者 scope=“？”来指定


#### 4.适配器模式
- 在Spring的Aop中，使用的Advice（通知）来增强被代理类的功能。
- Spring实现这一AOP功能的原理就使用代理模式（1、JDK动态代理。2、CGLib字节码生成技术代理。）对类进行方法级别的切面增强，即，生成被代理类的代理类， 并在代理类的方法前，设置拦截器，通过执行拦截器重的内容增强了代理方法的功能，实现的面向切面编程
- Adapter类接口：Target
```text
public interface AdvisorAdapter {

boolean supportsAdvice(Advice advice);

      MethodInterceptor getInterceptor(Advisor advisor);

}
```
- MethodBeforeAdviceAdapter类，Adapter
```text
class MethodBeforeAdviceAdapter implements AdvisorAdapter, Serializable {

      public boolean supportsAdvice(Advice advice) {
            return (advice instanceof MethodBeforeAdvice);
      }

      public MethodInterceptor getInterceptor(Advisor advisor) {
            MethodBeforeAdvice advice = (MethodBeforeAdvice) advisor.getAdvice();
      return new MethodBeforeAdviceInterceptor(advice);
      }

}
```


#### 第五种：装饰器模式（Decorator）
- 场景：需要链接多个数据库，并且操作业务的时候，也需要在不同的数据库之间做业务切换，如何实现？
- 首先想到在spring的applicationContext中配置所有的dataSource。这些dataSource可能是各种不同类型的，比如不同的数据库：Oracle、SQL Server、MySQL等，也可能是不同的数据源：比如apache 提供的org.apache.commons.dbcp.BasicDataSource、spring提供的org.springframework.jndi.JndiObjectFactoryBean等。然后sessionFactory根据客户的每次请求，将dataSource属性设置成不同的数据源，以到达切换数据源的目的。
- spring中用到的包装器模式在类名上有两种表现：一种是类名中含有Wrapper，另一种是类名中含有Decorator。基本上都是动态地给一个对象添加一些额外的职责。



#### 第六种：代理模式（Proxy）
- spring的Proxy模式在aop中有体现，比如JdkDynamicAopProxy和Cglib2AopProxy。
- Proxy.newProxyInstance

#### 第七种：观察者（Observer）
- 定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新
- Spring中的ApplicationListener 和EventListener 都是例子


#### 第八种：策略（Strategy）
- 定义一系列的算法，把它们一个个封装起来，并且使它们可相互替换。本模式使得算法可独立于使用它的客户而变化。 
- spring中在实例化对象的时候用到Strategy模式在SimpleInstantiationStrategy中有如下代码说明了策略模式的使用情况：

#### 第九种：模板方法（Template Method）
- 定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。Template Method使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤
- 其实一些抽象类的设计，都是利用了模板方法的思想，将方法的实现通过子类重写来适配多种实现











