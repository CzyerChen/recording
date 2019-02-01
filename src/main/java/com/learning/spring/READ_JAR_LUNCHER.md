之前看了很多关于Spring是如何加载对象，Springboot是如何启动的文章，看文章总是感觉很虚，今天就把代码整出来看了看

springboot文档（中文版）：https://qbgbook.gitbooks.io/spring-boot-reference-guide-zh/X.%20Appendices/D.3.2.%20Exploded%20archives.html

`这边分享的是通过Springboot-maven-plugin打的jar包如何启动的`

#### 1.首先将打的jar包解压，然后用IDE编辑器open
我们会看到几个目录
- META-INF：程序入口，里面有熟悉的MENIFEST.MF描述文件，会记录start-Class,args这些重要的启动参数,以及pom文件
```
#MENIFEST.MF
Manifest-Version: 1.0
Implementation-Title: executable-jar
Implementation-Version: 1.0-SNAPSHOT
Archiver-Version: Plexus Archiver
Built-By: Format
Start-Class: loader.AppMain
Implementation-Vendor-Id: loader.springframe.learning
Spring-Boot-Version: 2.0.0.RELEASE
Created-By: Apache Maven 3.6.0
Build-Jdk: 1.8.0_19
Implementation-Vendor: Pivotal Software, Inc.
Main-Class: org.springframework.boot.loader.JarLauncher
```
- BOOT-INF/lib目录：存放第三方jar包
- org/springframework/boot/loader : 今天研究的重点，主要是描述一个springboot的jar包，如何通过它的方式把所有的class组织起来运行
（因为这边的应用代码和lib代码不在同一个父目录下，不想shade等插件打的jar包，所有的class文件全部平铺在同一个父文件夹下）
- BOOT-INF/class：应用文件的字节码以及相关配置文件

### 2.主要关注org/springframework/boot/loader下的文件
- 首先可能无从下手，不清楚是什么类是主类，找不到入口，可以先看MENIFEST.MF
- 可以看到主类（Main-Class,这个jar运行的主类）和启动类（Start-Class，这个程序运行的启动类）,在主类里面看到了一个MAIN函数

- 找到了主类，一看有几种Launcher,看看他们分别是做什么的
    - JarLauncher ： 启动jar包使用
    - WarLauncher ： 启动ar包使用
    - PropertiesLauncher有一些特殊的性质，它们可以通过外部属性来启用（系统属性，环境变量，manifest实体或application.properties）

- 关于archive下文件（在launch过程中也会用到）
    - 归档文件的基础抽象类
        - arFileArchive就是jar包文件的抽象
          比如getUrl会返回这个Archive对应的URL，getManifest方法会获得Manifest数据等
        - ExplodedArchive是文件目录的抽象

- 来看JarLauncher
```
public static void main(String[] args) throws Exception {
        (new JarLauncher()).launch(args);
    }
```
一个main方法，使用了launch方法，构造的时候使用父类构造方法：
```
class JarLauncher extends ExecutableArchiveLauncher
```
```
protected ExecutableArchiveLauncher(Archive archive) {
        this.archive = archive;
    }
```

- 初始化之后就查看launch方法
```
protected void launch(String[] args) throws Exception {
        //在系统中注册一个自定义url处理器，org.springframework.boot.loader.jar.Handler
        //如果URL中没有指定处理器，会去系统属性中查询,获取默认值
        JarFile.registerUrlProtocolHandler();
        
        // getClassPathArchives方法在会去找lib目录下对应的第三方依赖JarFileArchive，同时也会项目自身的JarFileArchive
        // 根据getClassPathArchives得到的JarFileArchive集合去创建类加载器ClassLoader
        //这里会构造一个LaunchedURLClassLoader类加载器，这个类加载器继承URLClassLoader，并使用这些JarFileArchive集合的URL构造成URLClassPath
        // LaunchedURLClassLoader类加载器的父类加载器是当前执行类JarLauncher的类加载器
        ClassLoader classLoader = this.createClassLoader(this.getClassPathArchives());
        
        
        // getMainClass方法会去项目自身的Archive中的Manifest中找出key为Start-Class的类
        // 调用重载方法launch
        this.launch(args, this.getMainClass(), classLoader);
    }
```

- 这就简单的找到了正常在IDE当中找到了启动类，然后就是Spring的一些启动原则，比如加载监听类，初始化容器，加载热点代码到JVM容器，实例化需要注入的类
- 还需要提一下自定义类加载器，之前介绍类加载器的时候，说明了启动类加载器->扩展类加载器->应用类加载器->自定义类加载器，自定义类加载器主要处理一些特殊的在打包是对字节码做加密或者其他处理的情况下，在启动的时候，我们需要以解密或者其他方式将其加载出来，因而我们需要自定义，告知容器我的启动类等,需要重写findClass和loadClass方法
```
  protected  void launch(){
        JarFile.registerUrlProtocolHandler();
        // 构造LaunchedURLClassLoader类加载器，这里使用了2个URL，分别对应jar包中依赖包spring-boot-loader和spring-boot，使用 "!/" 分开，需要org.springframework.boot.loader.jar.Handler处理器处理
        LaunchedURLClassLoader classLoader = null;
        try {
            classLoader = new LaunchedURLClassLoader(
                    new URL[] {
                            new URL("jar:file:/Users/.../recording/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-loader-2.0.0.RELEASE.jar!/")
                            , new URL("jar:file:/Users/.../recording/target/executable-jar-1.0-SNAPSHOT.jar!/lib/spring-boot-2.0.0.RELEASE.jar!/")
                    },
                    LaunchedURLClassLoaderTest.class.getClassLoader());
        // 加载类
        // 这2个类都会在第二步本地查找中被找出(URLClassLoader的findClass方法)
        classLoader.loadClass("org.springframework.boot.loader.JarLauncher");
        classLoader.loadClass("org.springframework.boot.SpringApplication");
        // 在第三步使用默认的加载顺序在ApplicationClassLoader中被找出
        classLoader.loadClass("org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
```