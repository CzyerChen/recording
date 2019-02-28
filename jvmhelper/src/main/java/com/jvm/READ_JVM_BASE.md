### 基础概念
- 栈
- 堆
- 方法区
- 常量池

### 从一个类的执行看JVM
#### 准备工作
- 首先编写一个简单的类Person 
- 通过javac将.java文件编译成.class文件
- 通过java命令运行.class文件

#### 编译流程
- 源代码 --> 词法分析器 --> token流 --> 语法分析器 -->  语法树/抽象语法树 -->  语义分析器 --> 注释抽象语法树 --> 字节码生成器 --> JVM字节码
- 以上流程可以理解为三个过程： 分析和输入到符号表  --> 注解处理 --> 语义分析和生成class文件
-  语法糖的含义：编译器实现的一些小把戏，可以使效率大大提升，比如泛型（无需墙砖，运行没有警告，只做类型限定）
- jdk的作用： 区分操作系统，使java编译的操作指令能与本地系统兼容（这也是java平台无关性、跨平台的一个原因）
- 泛型擦除，在编写java代码的时候会用到泛型T/?之类的，可以做参数的适配或者限制，但是在编译时期，对应的泛型就会被转化为原生类型（Raw Type，称为裸类型），这个过程叫做泛型擦除
- 类的加载时交给JVM动态加载的，但是也有5种情况下，会`立即`对类进行初始化（class文件加载如JVM容器中）
    - new 的方式创建类的实例，静态相关（访问某个类的静态变量，对某个类的静态变量赋值，调用类的静态方法）
    - 反射的方式，显示地通过Class.forName(...)创建类对象
    - 继承的时候，要初始化子类对象，必须要先初始化一个父类的对象
    - 启动类，在java运行的时候外部显示指定的主类
    - 当使用JDK1.7的动态语言支持时（这个不是很了解）
- 类的动态加载，可以很好的利用内存开销

#### 将类（class）加载入JVM
- 类加载器有：
    - 启动类加载器（Bootstrap Classloader）: 加载java_home jre/lib/rt.jar,用来保障java环境，由C++实现
    - 扩展类加载器（Extension ClassLoader）: 负责加载扩展类，java_home jre/lib/ext或者显示指定的-Djava.ext.dirs指定目录下的jar包  
    - 应用类加载器（AppClassLoader）: 加载应用下Classpath下的jar包中的class
    - 自定义类加载器（User ClassLoader）:
- 工作过程：
    - 双亲委派模型：拿到类加载任务，先向上委派给父加载器执行，如果无法加载，再把任务下放
        - 当AppClassLoader需要加载一个类的时候，它需要将类加载的请求委派给它的父加载器ExtClassLoader执行；
        - ExtClassLoader接受到执行任务，需要继续将这个加载任务请求它的父加载器BootStrapClassLoader去执行；
        - 如果BootStrapClassLoader加载成功，即将目标类加载入JVM ，如果加载失败，继续将加载任务下放给ExtClassLoader;
        - 如果ExtClassLoader加载成功，即将目标类加载入JVM，如果加载失败，继续将加载任务下放给AppClassLoader；
        - 如果AppClassLoader加载成功，即将目标类加载入JVM，如果加载失败，抛出异常ClassNotFoundException.
    - 好处：避免整个JVM容器当中出现多份同样的字节码
    - 说明: 当一个类被加载成功，会将对应的实例缓存起来，下次在请求类加载的时候，无需尝试再次加载
    
- 类加载的详细过程
    - 流程图：
    ![avator](https://raw.githubusercontent.com/CzyerChen/recording/master/img/%E7%B1%BB%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.png)
    - 类的生命周期有7个流程：
      1. 加载（Loading）：查找并加载类的二进制数据，在java队中创建一个Class类对象
      2. 验证（Verification）：文件格式、元数据、字节码、符号引用验证
      3. 准备（Preparation）：为类的静态变量分配内存，将其初始化为默认值
      4. 解析（Resolution）：将类中的符号引用转为直接引用
      5. 初始化（Initialization）： 为类的静态变量赋予正确的初始值
      6. 使用（Using）
      7. 卸载（Unloading） 
    - 连接包含验证(b),准备(c)，初始化(d)

#### JIT即时编译器
- 对于加载的Class文件，对于热点代码（重要代码，重复调用的代码，循环的代码），会将class字节码重新编译优化，转化为机器码，直接让CPU执行，对于非热点代码，直接解析
- 热点代码可以使用热点探测来检测是否为热点代码：采样/计数器，两种方式
- 目前热点探测使用的是计数器方式，检测时需要为每个方法准备`方法调用计数器`,`回边计数器`
- 以上两个计数器有阈值，当计数器超出阈值时，就会触发JIT编译



#### java 内存模型：
http://ifeve.com/java-memory-model-0/


    




