> JVM类加载主要有5个流程：加载、验证、准备、解析、初始化
```text
加载 ---> 验证 ----> 准备 ---> 解析 ---->初始化 (解析和初始化交换顺序，实现动态绑定)
                                         |
         卸载 <---- 使用 <----------------| 
```

### 1.加载Class对象
- 通过类的完全限定名称获取定义该类的二进制字节流
- 将该字节流表示的静态存储结构转换为方法区的运行时存储结构
- 会在内存中生成一个代表这个类的java.lang.Class对象，作为方法区这个类的各种数据的入口
- 这个对象可以是文件获取，或者动态生成的

### 2.验证Class
- 确保Class文件的字节流中包含的信息是否符合当前虚拟机的要求，并且不会危害虚拟机自身的安全

### 3.准备
- 正式为类变量分配内存并设置类变量的初始值阶段，为这些类在方法区中分配内存空间

### 4.解析
- 虚拟机将常量池中的符号引用替换为直接引用的过程

### 5.符号引用
```text
符号引用与虚拟机实现的布局无关，

引用的目标并不一定要已经加载到内存中。

各种虚拟机实现的内存布局可以各不相同，

但是它们能接受的符号引用必须是一致的，

因为符号引用的字面量形式明确定义在Java虚拟机规范的Class文件格式
```
### 6.直接引用
```text
直接引用可以是指向目标的指针，相对偏移量或是一个能间接定位到目标的句柄

如果有了直接引用，那引用的目标必定已经在内存中存在
```
### 7.初始化
- 初始化是类加载的最后一个阶段，除了在加载阶段可以自定义类加载器，其他的过程都是由JVM完成，初始化完成才开始真正执行java代码

### 类初始化这个动作如何发生
- 主动引用会发生初始化：虚拟机规范中并没有强制约束何时进行加载，但是规范严格规定了有且只有下列五种情况必须对类进行初始化
```text
1.遇到 new、getstatic、putstatic、invokestatic 这四条字节码指令时，如果类没有进行过初始化，则必须先触发其初始化

2.使用 java.lang.reflect 包的方法对类进行反射调用的时候

3.当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化

4.当虚拟机启动时，用户需要指定一个要执行的主类

5.当使用 JDK 1.7 的动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果为 REFgetStatic, REFputStatic, REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化

```
- 被动引用不发生初始化
```text
注意以下几种情况不会执行类初始化： 
1. 通过子类引用父类的静态字段，只会触发父类的初始化，而不会触发子类的初始化。 
2. 定义对象数组，不会触发该类的初始化。 
3. 常量在编译期间会存入调用类的常量池中，本质上并没有直接引用定义常量的类，不会触发定义常量所在的类。 
4. 通过类名获取Class对象，不会触发类的初始化。 
5. 通过Class.forName加载指定类时，如果指定参数initialize为false时，也不会触发类初始化，其实这个参数是告诉虚拟机，是否要对类进行初始化。 
6. 通过ClassLoader默认的loadClass方法，也不会触发初始化动作。
```

### 8.类构造器client
- 初始化阶段是执行类构造器<client>方法的过程
- <client>方法是由编译器自动收集类中的类变量的赋值操作和静态语句块中的语句合并而成的
- 虚拟机会保证子<client>方法执行之前，父类的<client>方法已经执行完毕，如果一个类中没有对静态变量赋值也没有静态语句块，那么编译器可以不为这个类生成<client>()方法

### 9.类加载器
- 类加载器根据作用范围分为：启动类加载器（Bootstrap）、扩展类加载器（Extension）、应用类加载器（Application）、自定义类加载器
- 启动类加载器（Bootstrap）：加载JAVA_HOME/lib下的class,-Xbootclasspath参数指定路径
- 扩展类加载器（Extension）：加载JAVA_HOME/jre/lib/ext下的class,通过java.ext.dirs系统变量指定路径中的类库
- 应用类加载器（Application）:加载应用classpath下的class
- 自定义类加载器，通过集成ClassLoader,加载指定路径下的类
- JVM通过双亲委派模型进行类的加载，当然我们也可以通过继承java.lang.ClassLoader实现自定义的类加载器

### OSGI 动态模型系统 open service gateway initialtive
```text
OSGi服务平台提供在多种网络设备上无需重启的动态改变构造的功能。

为了最小化耦合度和促使这些耦合度可管理，OSGi技术提供一种面向服务的架构，它能使这些组件动态地发现对方

OSGi旨在为实现Java程序的模块化编程提供基础条件，基于OSGi的程序很可能可以实现模块级的热插拔功能，当程序升级更新时，可以只停用、重新安装然后启动程序的其中一部分

它在提供强大功能同时，也引入了额外的复杂度，因为它不遵守了类加载的双亲委托模型

```

