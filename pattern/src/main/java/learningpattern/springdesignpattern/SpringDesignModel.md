### 设计模式一
#### 1.解释器设计模式
- 此模式基于表达式和评估器，比如一些js spEL表达式，类似于name = #name

#### 2.建设者模式
- 属于创建对象三剑客的第一种模式，典型的就是类似于lombok的builder操作，通过.name().age()设置属性，而不是通过常用的setName的方式设置，这是通过将值传递给内部构造器的方式进行初始化，内部静态类接受链接方法的调用，比如作用域、工厂方法、属性等。
- Spring中BeanDefinitionBuilder


#### 3.工厂方法
- Spring中我们可以通过工厂方式创建Bean
- 创建对象模式三剑客第二种模式，，通过公共静态方法对象进行初始化，成为工厂方法，需要一个接口来创建对象，例如String.valueOf()


#### 4.抽象工厂
- Spring中工厂的例子是BeanFactory,通过它，我们可以从Spring的容器访问Bean,根据策略，getBean方法可以返回已创建的对象
- 有分为ClassPathXmlApplicationContext,XmlWebApplicationContext.....
- 抽象工厂可以理解为提供所需对象的东西，定义了构建对象的方法，抽象工厂就是定义接口行为，通过实现类来具体实现对应的方法，然后通过多态来定义对象


### 设计模式二
#### 结构模式：代理模式，复合模式
- 5.代理模式，ProxyFactoryBean
- 6.复合模式 ,基于具有共同行为的多个对象的存在，用于构建更大的对象
    - Spring中BeanMetadataElement接口，用于配置Bean对象，它是所有继承对象的基本界面


#### 行为模式：策略模式，模板方法
- 7.策略模式，MethodNameResolver
- 8.模板方法 AbstractApplicationContext中使用了模板方法，

### 设计模式三
#### 9.原型模式
- 通过复制一个已存在的对象来创建一个对象的实例
- AbstractBeanFactory就是利用一种特定的原型设计模式，初始化bean原型作用域

#### 10.对象池模式
- 例如数据库连接池，是改善我们想使用巨型对象的响应时间，最好是重用已有的对象

#### 11.观察者模式
- Spring中观察者设计模式用于将应用程序上下文相关的时间传输到ApplicationListener，将每一个监视器注册到事件上，当指定时间发生变化，监听器就需要进行通知
- ApplicationEventMulticaster负责管理不同的listener和向他们发布事件


### 设计模式四
#### 12.适配器模式
- 适配器比如API接口需要统一的输入输出，其中用什么代码去执行获取结果都可以适配
- 或者就是钻孔，小钻头就钻小孔，大钻头就钻大孔
- Spring使用适配器迷失来处理不同Servlet容器中的加载时编织


#### 13.装饰器模式
- 上帝视角，通过组合，添加对象，达到1+1>2的效果
- Spring用于管理缓存同步事务

#### 14.单例模式
- Spring中的单例和Java中的单例略有差别
- java中的单例将实例的数量限制在给定类加载器管理的整个空间当中，而Spring是将实例的数量限制的作用域放在整个应用程序的上下文
- AbstractBeanFactory有单例运用的影子

### 设计模式四
#### 15.行为模式
- 用于处理Bean工厂的后置处理的命令模式
- 用于定义Bean参数转换为面向对象（String，Object的实例）参数的访问者模式

#### 16.命令模式
- 命令别的对象做，自己不做

#### 17.访问者模式
- Bean配置文件的配置





