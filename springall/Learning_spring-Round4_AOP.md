### AOP的概念
- 是一种面向切面编程，使用代理的设计模式，通过代理，实现对目标方法的访问控制，使得业务之间解耦
- 所谓"切面"，简单说就是那些与业务无关，却为业务模块所共同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块之间的耦合度，并有利于未来的可操作性和可维护性

### AOP使用场景
```text
1. Authentication 权限
2. Caching 缓存
3. Context passing 内容传递
4. Error handling 错误处理
5. Lazy loading 懒加载
6. Debugging 调试
7. logging, tracing, profiling and monitoring 记录跟踪 优化 校准
8. Performance optimization 性能优化
9. Persistence 持久化
10. Resource pooling 资源池
11. Synchronization 同步
12. Transactions 事务
```
### AOP的核心概念
- 切面Aspect:类是对物体特征的抽象，切面就是对横切关注点的抽象
使用@Aspect注解实现
- 横切关注点：对哪些方法进行拦截，拦截后怎么处理，这些关注点称之为横切关注点
- 连接点joinpoint：被拦截到的点，因为Spring只支持方法类型的连接点，所以在Spring中连接点指的就是被拦截到的方法，实际上连接点还可以是字段或者构造器。一个连接点总是表示一个方法的执行
- 切入点：pointcut:对连接点进行拦截定义，切入点表发是如何和连接点匹配是AOP的核心，spring缺省使用AspectJ切入点语法
- 通知 advice ：分为前置，后置，异常，最终，和环绕，以拦截器做通知模型，并维护一个以连接点为中心的拦截器链
- 前置通知：before advicee 在连接点前执行通知
- 后置通知： after returning advice 在连接点正常完成之后执行的通知
- 异常通知：aftre throwing advice: 方法因异常退出时发出的通知
- 最终通知：after finally advice 连接点推出的时候执行的通知
- 环绕通知 Around Advice 包围一个连接点的通知，在前后完成自定义行为

- 目标对象 ：代理的目标对象，一个或多个切面所通知的对象，被通知对象
- 织入 weave:将切面应用到目标对象并导致代理对象创建的过程，将切面连接到其他应用程序类型或者对象上，并创建一个被通知对象
- 引入 introduction ：在不修改代码的前提下，引入可以在运行期为类动态地添加一些方法或字段。
- AOP代理：AOP框架创建的对象，用来实现切面契约，使用JDK默认的动态代理或者CGLIB动态代理，看实现方式不同而定

### AOP的两种代理方法
####  jdk动态代理，默认的实现，需要类实现接口的方式
- JDK动态代理主要涉及到java.lang.reflect包中的两个类：Proxy和InvocationHandler
- InvocationHandler是一个接口，通过实现该接口定义横切逻辑，并通过反射机制调用目标类的代码，动态将横切逻辑和业务逻辑编制在一起- Proxy利用InvocationHandler动态创建一个符合某一接口的实例，生成目标类的代理对象。

#### CGLIB动态代理，需要类执行继承的方法，通过底层字节码技术对子类进行拦截
- CGLib全称为Code Generation Library，是一个强大的高性能，高质量的代码生成类库，可以在运行期扩展Java类与实现Java接口，CGLib封装了asm，可以再运行期动态生成新的class。
- JDK创建代理有一个限制，就是只能为接口创建代理实例，而对于没有通过接口定义业务方法的类，则可以通过CGLib创建动态代理。

### AOP的实现方法
方法有动态代理和静态代理两种，一般需要有三种对象：接口、真实对象类、代理类

1.静态代理
- 静态代理，接口、真实类实现接口，并且通过方法向外传递，代理类通过注入一个真实类的对象，将真实类的方法通过封装后传递出去
- 代码见 BaseInterface  + BaseInterfaceImpl + BaseInterfaceImplHelper

2.jdk动态代理
- jdk动态代理是jdk的默认动态代理方法
- 基于接口，通过反射，实现类的动态代理
```java
class Test{
    public static  void main(String[] args){
        MyInvocationHandler handler = new MyInvocationHandler(new InterfaceTImpl());//继承InvocationHandler ,实现invoke方法，实现拦截
        InterfaceT proxyService = (InterfaceT) Proxy.newProxyInstance(/**当前类加载器*/Test.class.getClassLoader(), /**class列表*/new Class[] { InterfaceT.class },/*拦截器*/ handler);
        proxyService.sayHello();
    }
}

```
- 通过invoke方法，首先需要判断当前类在已加载的类当中是否存在，如果存在获取类，判断类是否为接口，不是接口就会报错（这就是jdk动态代理的基础条件），如果一切权限判定都通过的话，就对字节码通过类加载器反射，生成对象


3. CGlib动态代理
- CGlib动态代理是利用底层字节码技术，依靠继承，实现方法拦截

### 动态代理的对比 --- JDK & CGLIB
- JDK中实现代理时，要求代理类必须是继承接口的类，因为JDK最后生成的proxy class其实就是实现了被代理类所继承的接口并且继承了java中的Proxy类，通过反射找到接口的方法，调用InvocationHandler的invoke 方法实现拦截
- CGLIB中的动态代理是JDK proxy的一个很好的补充，，最后生成的proxy class是一个继承被代理类的class，通过重写被代理类中的非final的方法实现代理

因而：
- JDK proxy：代理类必须实现接口 
- CGLIB: 代理类不能是final,代理的方法也不能是final,依靠继承的原理

### AOP 基本就是依靠静态代理和动态代理做方法的增强
- 数据缓存，数据校验
- 日志拦截
- 身份校验
- 资源回收处理
.....

### JDK动态代理
https://blog.csdn.net/sunnycoco05/article/details/78845878

### CGLIB动态代理
https://blog.csdn.net/sunnycoco05/article/details/78853148

### 使用spring的代理方法，动态使用JDK代理和CGLIB代理
如果声明接口定义的使用JDK动态代理，如果没有声明接口定义的使用CGLIB动态代理
https://blog.csdn.net/sunnycoco05/article/details/78901449

### 一个例子
```text
@Aspect
public class TransactionDemo {
@Pointcut(value="execution(* com.yangxin.core.service.*.*.*(..))")
public void point(){//切点
}
@Before(value="point()")
public void before(){//前置通知
System.out.println("transaction begin");
}
@AfterReturning(value = "point()")
public void after(){//后置通知
System.out.println("transaction commit");
}
@Around("point()")
public void around(ProceedingJoinPoint joinPoint) throws Throwable{//环绕通知
System.out.println("transaction begin");
joinPoint.proceed();
System.out.println("transaction commit");
}
}
```