### AOP的概念
是一种面向切面编程，使用代理的设计模式，通过代理，实现对目标方法的访问控制，使得业务之间解耦
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


