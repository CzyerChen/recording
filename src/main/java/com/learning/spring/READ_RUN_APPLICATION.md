简单查看了JarLuncher如何启动一个springboot项目，接下来看找到主类，怎么将整个程序RUN起来

#### 一、对于启动流程非常复杂，借用了别人的流程
    先大致感受一下,以下是前辈的总结，本人在2.0.0源码流程里面看的时候，大致是顺的，就是这个流程可能还是简易了一些
    
    对于2.0.0和1.x.x看下来还是有一些差异的，包括监听器信号的发送，局部初始化位置的调整等
    
1. SpringApplication.run
2. 初始化监听器
3. 发布ApplicationStartEvent事件
4. 装配环境和参数
5. 发布ApplicationEnvirenmentPreparedEvent事件
6. 打印Banner
7. 创建ApplicationContext
8. 装配Context
9. 发布ApplicationPreparedEvent事件，空，并未执行
10. 注册，加载...
11. 发布ApplicationPreparedEvent事件，执行
12. refreshContext
13. afterContext
14. 发布ApplicationReadyEvent事件

#### 二、以上是前辈总结的流程，比较清晰，可以结合源码慢慢缕
- 1.写一个Main的run方法通过Debug一步步跟进去看
```java
@SpringBootApplication
public class AppMain {
    public static void main(String[] args) {
        //第一步，一个run方法
        SpringApplication.run(AppMain.class, args);
    }
}

```

- 2.走进RUN方法几层，到达SpringApplication
```
public ConfigurableApplicationContext run(String... args) {
//StopWatch是spring的一个util工具，主要是用来记录程序的运行时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
        this.configureHeadlessProperty();
        //第二步，初始化一个监听器
        SpringApplicationRunListeners listeners = this.getRunListeners(args);
        //第三步，发布ApplicationStartedEvent事件
        listeners.starting();

        try {
        //第四步，装配运行环境和参数
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
            this.configureIgnoreBeanInfo(environment);
            //第六步，打印Baanner
            Banner printedBanner = this.printBanner(environment);
            //第八步，装配Context
            context = this.createApplicationContext();
            this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
            this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            
            //第十二步，refreshContext
            this.refreshContext(context);
            
            //第十三步，afterContext
            this.afterRefresh(context, applicationArguments);
            
            stopWatch.stop();
            if (this.logStartupInfo) {
                (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
            }

           
            listeners.started(context);
            this.callRunners(context, applicationArguments);
        } catch (Throwable var9) {
            this.handleRunFailure(context, listeners, exceptionReporters, var9);
            throw new IllegalStateException(var9);
        }
        //第十四步，发布ApplicationReadyEvent事件
        listeners.running(context);
        return context;
    }
```
#### 三、初始化监听器 SpringApplicationRunListeners，这个全局监听器用到了观察者模式
```
SpringApplicationRunListeners listeners = this.getRunListeners(args);
listeners.starting();
```

#### 四、发布ApplicationStartingEvent
通过listener的starting方法，发布了一个应用启动的事件
```
 public void starting() {
        this.initialMulticaster.multicastEvent(new ApplicationStartingEvent(this.application, this.args));
    }
``` 
#### 五、装配参数和环境 prepareEnvironment
1.getOrCreateEnvironment 装配大的容器环境，判断是Servlet还是标准容器
 
2.configureEnvironment 主要包括应用配置文件的读取
- configurePropertySources，加载默认配置defaultProperties、加载main函数运行args
- configureProfiles ,根据环境装配配置
     - 由配置参数spring.profiles.active决定，然后就将主配置和分环境配置的文件都加载进来
- environmentPrepared 发布ApplicationEnvirenmentPreparedEvent事件
- bindToSpringApplication 将加载的ConfigurableEnvironment环境绑定给main函数
- convertToStandardEnvironmentIfNecessary 将当前环境转换成标准环境
    
3.configureIgnoreBeanInfo 设置全局参数 spring.beaninfo.ignore = true

4.printBanner 这边主要配置其实日志打印的内容，启动会看到一个Springboot的标志，就在这个里面重写，自己就可以自定Banner,并且会对是log还是system.out进行判断

5.创建ApplicationContext createApplicationContext  根据容器类型选择context的Class，并通过反射生成对应实例
```
 protected ConfigurableApplicationContext createApplicationContext() {
        Class<?> contextClass = this.applicationContextClass;
        if (contextClass == null) {
            try {
                switch(this.webApplicationType) {
                case SERVLET:
                    contextClass = Class.forName("org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext");
                    break;
                case REACTIVE:
                    contextClass = Class.forName("org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext");
                    break;
                default:
                    contextClass = Class.forName("org.springframework.context.annotation.AnnotationConfigApplicationContext");
                }
            } catch (ClassNotFoundException var3) {
                throw new IllegalStateException("Unable create a default ApplicationContext, please specify an ApplicationContextClass", var3);
            }
        }

        return (ConfigurableApplicationContext)BeanUtils.instantiateClass(contextClass);
    }
```
6.getSpringFactoriesInstances -> createSpringFactoriesInstances
```
private <T> List<T> createSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, ClassLoader classLoader, Object[] args, Set<String> names) {
        List<T> instances = new ArrayList(names.size());
        Iterator var7 = names.iterator();

        while(var7.hasNext()) {
            String name = (String)var7.next();

            try {
                Class<?> instanceClass = ClassUtils.forName(name, classLoader);
                Assert.isAssignable(type, instanceClass);
                Constructor<?> constructor = instanceClass.getDeclaredConstructor(parameterTypes);
                T instance = BeanUtils.instantiateClass(constructor, args);
                instances.add(instance);
            } catch (Throwable var12) {
                throw new IllegalArgumentException("Cannot instantiate " + type + " : " + name, var12);
            }
        }

        return instances;
    }
```
基本就是获取class文件，进行接口和权限判断，获取类加载器，使用构造方法，newInstance

forName方法是类加载的核心,不论是spring的方式通过Xml加载beanDefinition后加载，通过一个个的BeanName来加载对象，还是springboot这种自动加载，自动装配的方式，都需要使用这个方法根据BeanName和类加载器获取Class对象

其实，你看源码也能知道她最后都是在MAP里面拿值，那是因为无论那种方法，在读配置文件或者解析配置文件时，都会将对应的信息放入Map中

 - 关于commonClassCache这个Map,官方说明：Map with common "java.lang" class name as key and corresponding Class as value.

```
 public static Class<?> forName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = (Class)commonClassCache.get(name);
        }

        if (clazz != null) {
            return clazz;
        } else {
            Class elementClass;
            String elementName;
            if (name.endsWith("[]")) {
                elementName = name.substring(0, name.length() - "[]".length());
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else if (name.startsWith("[L") && name.endsWith(";")) {
                elementName = name.substring("[L".length(), name.length() - 1);
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else if (name.startsWith("[")) {
                elementName = name.substring("[".length());
                elementClass = forName(elementName, classLoader);
                return Array.newInstance(elementClass, 0).getClass();
            } else {
                ClassLoader clToUse = classLoader;
                if (classLoader == null) {
                    clToUse = getDefaultClassLoader();
                }

                try {
                    return clToUse != null ? clToUse.loadClass(name) : Class.forName(name);
                } catch (ClassNotFoundException var9) {
                    int lastDotIndex = name.lastIndexOf(46);
                    if (lastDotIndex != -1) {
                        String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);

                        try {
                            return clToUse != null ? clToUse.loadClass(innerClassName) : Class.forName(innerClassName);
                        } catch (ClassNotFoundException var8) {
                            ;
                        }
                    }

                    throw var9;
                }
            }
        }
    }
```

  - loadSpringFactories 加载spring工厂，用这个MultiValueMap<String, String> map做缓存 以classLoader为key,LinkedMultiValueMap为value
  - loadFactoryNames
  - 然后就成功加载了一堆实例
  
7.prepareContext 发布ApplicationPreparedEvent事件
  - 注册一个单例的实例加载器  ：context.getBeanFactory().registerSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator", this.beanNameGenerator);
  - 设置一个ApplicationContext初始化器：ApplicationContextInitializer initializer
  - 以上完成了设置Context的环境变量、注册Initializers、beanNameGenerator等
  - listeners.contextPrepared(context) 向监听器发布ApplicationPreparedEvent事件
  - 走到最后呢是一个空方法体，可以看一下,也就是说在这一步其实还没有发布这个事件
  ```
 public void contextPrepared(ConfigurableApplicationContext context) {
    }

```

8.装载Context
   - 注册几个单例的Bean
```
      context.getBeanFactory().registerSingleton("springApplicationArguments", applicationArguments);
      //如果printBanner为空
      context.getBeanFactory().registerSingleton("springBootBanner", printedBanner);
  ```
  - 装载beanNameGenerator，resourceLoader，environment 给BeanDefinitionLoader
```
  this.load(context, sources.toArray(new Object[0]));
  listeners.contextLoaded(context);
  
  BeanDefinitionLoader loader = this.createBeanDefinitionLoader(this.getBeanDefinitionRegistry(context), sources);

```
9.ApplicationPreparedEvent真正发布，需要从listeners.contextLoaded(context)往下走可以看到
```
public void contextLoaded(ConfigurableApplicationContext context) {
        ApplicationListener listener;
        for(Iterator var2 = this.application.getListeners().iterator(); var2.hasNext(); context.addApplicationListener(listener)) {
            listener = (ApplicationListener)var2.next();
            if (listener instanceof ApplicationContextAware) {
                ((ApplicationContextAware)listener).setApplicationContext(context);
            }
        }

        this.initialMulticaster.multicastEvent(new ApplicationPreparedEvent(this.application, this.args, context));
    }
```
10.this.refreshContext(context)：装配context beanfactory等非常重要的核心组件,流程如下
```
  public void refresh() throws BeansException, IllegalStateException {
        Object var1 = this.startupShutdownMonitor;
        synchronized(this.startupShutdownMonitor) {
            this.prepareRefresh();
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            this.prepareBeanFactory(beanFactory);

            try {
                this.postProcessBeanFactory(beanFactory);
                this.invokeBeanFactoryPostProcessors(beanFactory);
                this.registerBeanPostProcessors(beanFactory);
                this.initMessageSource();
                this.initApplicationEventMulticaster();
                this.onRefresh();
                this.registerListeners();
                this.finishBeanFactoryInitialization(beanFactory);
                this.finishRefresh();
            } catch (BeansException var9) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                }

                this.destroyBeans();
                this.cancelRefresh(var9);
                throw var9;
            } finally {
                this.resetCommonCaches();
            }

        }
    }
```
finishRefresh,发送RefreshedEvent
```
this.publishEvent((ApplicationEvent)(new ContextRefreshedEvent(this)));
```
11.afterRefresh
```
protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
    }
```
12.StopWatch停止- 记录程序的运行时间 stopWatch.stop();

13.listeners.started(context)，发布started事件
```
        context.publishEvent(new ApplicationStartedEvent(this.application, this.args, context));

```
14.context.publishEvent(new ApplicationStartedEvent(this.application, this.args, context));

15.callRunners(ApplicationContext context, ApplicationArguments args)：开启运行启动类：ApplicationRunner，CommandLineRunner两种

16.listeners.running(context) ,发布ApplicationReadyEvent事件
```
        context.publishEvent(new ApplicationReadyEvent(this.application, this.args, context));

```


#### 这样ApplicationContext就初始完了
这边一份网上看到的介绍的比较零碎，可能不是2.x版本的（流程有点不一样），不过对于里面的步骤有中文解释：https://blog.csdn.net/qq_26093341/article/details/80889916

  

    




