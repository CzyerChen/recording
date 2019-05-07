以上内容涉及了很多jdk比较深入的东西，这边也总结以下spring中相关涉及的知识点

### IOC: 控制反转
```text
Spring 通过一个配置文件描述 Bean 及 Bean 之间的依赖关系，

利用 Java 语言的反射功能实例化 Bean 并建立 Bean 之间的依赖关系。 

Spring 的 IoC 容器在完成这些底层工作的基础上，

还提供了 Bean 实例缓存、生命周期管理、 Bean 实例代理、事件发布、资源装载等高级服务
```
- Spring 启动时读取应用程序提供的Bean配置信息，
并在Spring容器中生成一份相应的Bean配置注册表，

- 然后根据这张注册表实例化Bean，装配好Bean之间的依赖关系，为上层应用提供准备就绪的运行环境。
- 其中Bean缓存池为HashMap实现
```text
Bean 配置信息：xml configuration autowired
1.读取Bean配置信息

Spring容器：Bean定义注册表 HashMap
2.根据BEAN注册表实例化BEAN

BEAN实现类
3.将Bean实例放到Spring容器中

BEAN缓存池
应用程序
4.使用bean
```

#### IOC容器的实现
- BeanFactory 是 Spring 框架的基础设施，面向 Spring 本身；
- ApplicationContext 面向使用 Spring 框架的开发者，几乎所有的应用场合我们都直接使用 ApplicationContext 而非底层的 BeanFactory。

#### DI：依赖注入的四种方式
- 构造器注入
- setter注入
- 静态工厂注入
- 实例注入

- IOC 依赖 DI,使容器负责组件的装配
- 支持依赖注入、依赖检查、自动装配、支持集合、指定初始化方法和销毁方法、支持回调某些方法
- Bean的生命周期，都交由容器来管理，控制Bean的依赖注入
- spring的设计使用两个接口代表容器：BeanFactory / ApplicationContext
- spring类加载，BeanFactory，读取xml配置文件，加载在Map中，key为BeanName,Value使Bean实例，只提供注册和获取，使低级容器
- ApplicationContext比BeanFactory提供了更多的功能，是高级容器，可以进行资源的获取、支持各种消息

#### 5种装配方式
- 手动装配，自动装配
- 手动装配包括：xml装配，构造器装配，setter装配
- 自动装配有五种方式：
```text
no:不进行自动装配

byName:通过参数名 自动装配

byType：通过参数类型自动装配

constructor：这个方式类似于byType， 但是要提供给构造器参数，如果没有确定的带参数的构造器参数类型，将会抛出异常

autodetect：首先尝试使用constructor来自动装配
```

####   BeanDefinitionRegistry注册表
- Spring 配置文件中每一个节点元素在 Spring 容器里都通过一个 BeanDefinition 对象表示，它描述了 Bean 的配置信息。而 BeanDefinitionRegistry 接口提供了向容器手工注册 BeanDefinition 对象的方法。
```text
public interface BeanDefinitionRegistry extends AliasRegistry {
    void registerBeanDefinition(String var1, BeanDefinition var2) throws BeanDefinitionStoreException;

    void removeBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String var1);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String var1);
}

```

#### BeanFactory 顶层接口
```text
public interface BeanFactory {
    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String var1) throws BeansException;

    <T> T getBean(String var1, @Nullable Class<T> var2) throws BeansException;

    Object getBean(String var1, Object... var2) throws BeansException;

    <T> T getBean(Class<T> var1) throws BeansException;

    <T> T getBean(Class<T> var1, Object... var2) throws BeansException;

    boolean containsBean(String var1);

    boolean isSingleton(String var1) throws NoSuchBeanDefinitionException;

    boolean isPrototype(String var1) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String var1, ResolvableType var2) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String var1, @Nullable Class<?> var2) throws NoSuchBeanDefinitionException;

    @Nullable
    Class<?> getType(String var1) throws NoSuchBeanDefinitionException;

    String[] getAliases(String var1);
}

```
#### ListableBeanFactory 查看一些Bean的个数等
```text
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String var1);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(ResolvableType var1);

    String[] getBeanNamesForType(@Nullable Class<?> var1);

    String[] getBeanNamesForType(@Nullable Class<?> var1, boolean var2, boolean var3);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> var1) throws BeansException;

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> var1, boolean var2, boolean var3) throws BeansException;

    String[] getBeanNamesForAnnotation(Class<? extends Annotation> var1);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> var1) throws BeansException;

    @Nullable
    <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> var2) throws NoSuchBeanDefinitionException;
}
```
#### 父子级联  --- 可以获取父容器的信息，父容器不能获取子容器的信息
- Spring 的 IoC 容器可以建立父子层级关联的容器体系
- 比如在 Spring MVC 中，展现层 Bean 位于一个子容器中，而业务层和持久层的 Bean 位于父容器中。这样，展现层 Bean 就可以引用业务层和持久层的 Bean，而业务层和持久层的 Bean 则看不到展现层的 Bean
```text
public interface HierarchicalBeanFactory extends BeanFactory {
    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String var1);
}

```

#### ConfigurableBeanFactory --增强了 IoC 容器的可定制性，它定义了设置类装载器、属性编辑器、容器初始化后置处理器
```text
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void setParentBeanFactory(BeanFactory var1) throws IllegalStateException;

    void setBeanClassLoader(@Nullable ClassLoader var1);

    @Nullable
    ClassLoader getBeanClassLoader();

    void setTempClassLoader(@Nullable ClassLoader var1);

    @Nullable
    ClassLoader getTempClassLoader();

    void setCacheBeanMetadata(boolean var1);

    boolean isCacheBeanMetadata();

    void setBeanExpressionResolver(@Nullable BeanExpressionResolver var1);

    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();

    void setConversionService(@Nullable ConversionService var1);

    @Nullable
    ConversionService getConversionService();

    void addPropertyEditorRegistrar(PropertyEditorRegistrar var1);

    void registerCustomEditor(Class<?> var1, Class<? extends PropertyEditor> var2);

    void copyRegisteredEditorsTo(PropertyEditorRegistry var1);

    void setTypeConverter(TypeConverter var1);

    TypeConverter getTypeConverter();

    void addEmbeddedValueResolver(StringValueResolver var1);

    boolean hasEmbeddedValueResolver();

    @Nullable
    String resolveEmbeddedValue(String var1);

    void addBeanPostProcessor(BeanPostProcessor var1);

    int getBeanPostProcessorCount();

    void registerScope(String var1, Scope var2);

    String[] getRegisteredScopeNames();

    @Nullable
    Scope getRegisteredScope(String var1);

    AccessControlContext getAccessControlContext();

    void copyConfigurationFrom(ConfigurableBeanFactory var1);

    void registerAlias(String var1, String var2) throws BeanDefinitionStoreException;

    void resolveAliases(StringValueResolver var1);

    BeanDefinition getMergedBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    boolean isFactoryBean(String var1) throws NoSuchBeanDefinitionException;

    void setCurrentlyInCreation(String var1, boolean var2);

    boolean isCurrentlyInCreation(String var1);

    void registerDependentBean(String var1, String var2);

    String[] getDependentBeans(String var1);

    String[] getDependenciesForBean(String var1);

    void destroyBean(String var1, Object var2);

    void destroyScopedBean(String var1);

    void destroySingletons();
}
```

#### AutowireCapableBeanFactory  -- 自动装配的工厂
```text
定义了将容器中的 Bean 按某种规则（如按名字匹配、按类型匹配等）进行自动装配的方法
```

#### SingletonBeanRegistry -- 运行期间注册单例（放置在HashMap中）
```text
对于单实例（ singleton）的 Bean 来说，BeanFactory会缓存 Bean 实例，所以第二次使用 getBean() 获取 Bean 时将直接从 IoC 容器的缓存中获取 Bean 实例。Spring 在 DefaultSingletonBeanRegistry 类中提供了一个用于缓存单实例 Bean 的缓存器，它是一个用HashMap 实现的缓存器，单实例的 Bean 以 beanName 为键保存在这个HashMap 中

public interface SingletonBeanRegistry {
    void registerSingleton(String var1, Object var2);

    @Nullable
    Object getSingleton(String var1);

    boolean containsSingleton(String var1);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}

```

#### 装载APPlicationContext
```text
1. ClassPathXmlApplicationContext：默认从类路径加载配置文件

2. FileSystemXmlApplicationContext：默认从文件系统中装载配置文件

3. ApplicationEventPublisher：让容器拥有发布应用上下文事件的功能，包括容器启动事件、关闭事件等。

4. MessageSource：为应用提供 i18n 国际化消息访问的功能；

5. ResourcePatternResolver ： 所 有 ApplicationContext 实现类都实现了类似于PathMatchingResourcePatternResolver 的功能，可以通过带前缀的 Ant 风格的资源文件路径装载 Spring 的配置文件。

6. LifeCycle：该接口是 Spring 2.0 加入的，该接口提供了 start()和 stop()两个方法，主要用于控制异步处理过程。在具体使用时，该接口同时被 ApplicationContext 实现及具体 Bean 实现， ApplicationContext 会将 start/stop 的信息传递给容器中所有实现了该接口的 Bean，以达到管理和控制 JMX、任务调度等目的。

7. ConfigurableApplicationContext 扩展于 ApplicationContext，它新增加了两个主要的方法： refresh()和 close()，让 ApplicationContext 具有启动、刷新和关闭应用上下文的能力。在应用上下文关闭的情况下调用 refresh()即可启动应用上下文，在已经启动的状态下，调用 refresh()则清除缓存并重新装载配置信息，而调用close()则可关闭应用上下文
```
#### WebApplicationContext -- web 应用上下文
- 从WebApplicationContext 中可以获得 ServletContext 的引用，整个 Web 应用上下文对象将作为属性放置到 ServletContext 中，以便 Web 应用环境可以访问 Spring 应用上下文
```text
public interface WebApplicationContext extends ApplicationContext {
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
    String SCOPE_REQUEST = "request";
    String SCOPE_SESSION = "session";
    String SCOPE_APPLICATION = "application";
    String SERVLET_CONTEXT_BEAN_NAME = "servletContext";
    String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";
    String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";

    @Nullable
    ServletContext getServletContext();
}
```


### 手写简易Spring
https://www.cnblogs.com/ITtangtang/p/3978349.html

