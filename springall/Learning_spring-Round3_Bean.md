> bean的五种作用域：singleton单例，prototype原型，request, session, global session

### singleton 单例模式 -- 多线程下不安全
- Spring IoC容器中只会存在一个共享的Bean实例，无论有多少个Bean引用它，始终指向同一对象。该模式在多线程下是不安全的。
- Singleton作用域是Spring中的缺省作用域，也可以显示的将Bean定义为singleton模式
- <bean id="userDao" class="com.ioc.UserDaoImpl" scope="singleton"/>

### prototype:原型模式
- 每次通过Spring容器获取prototype定义的bean时，容器都将创建一个新的Bean实例，每个Bean实例都有自己的属性和状态
- 而singleton全局只有一个对象
- 根据经验，对有状态的bean使用prototype作用域，而对无状态的bean使用singleton作用域

### request
- 在一次Http请求中，容器会返回该Bean的同一实例。而对不同的Http请求则会产生新的Bean，而且该bean仅在当前Http Request内有效,当前Http请求结束，该bean实例也将会被销毁
- 在一次Http Session中，容器会返回该Bean的同一实例。而对不同的Session请求则会创建新的实例，该bean实例仅在当前Session内有效。
- <bean id="loginAction" class="com.cnblogs.Login" scope="request"/>

### session
- 在一次Http Session中，容器会返回该Bean的同一实例。而对不同的Session请求则会创建新的实例，该bean实例仅在当前Session内有效。
- 在一次Http Session中，容器会返回该Bean的同一实例。而对不同的Session请求则会创建新的实例，该bean实例仅在当前Session内有效。
- <bean id="userPreference" class="com.ioc.UserPreference" scope="session"/>

### global Session：
- 在一个全局的Http Session中，容器会返回该Bean的同一个实例，仅在使用portlet context时有效


### Spring Bean的生命周期
- 实例化：初始化为bean new的方式
- IOC依赖注入 ：通过Spring对包注解的扫描，将依赖的Bean优先实例化放到容器中
- setBeanName：
如果这个Bean已经实现了BeanNameAware接口，会调用它实现的setBeanName(String)方法，此处传递的就是Spring配置文件中Bean的id值
- BeanFactoryAware实现：
如果这个Bean已经实现了BeanFactoryAware接口，会调用它实现的setBeanFactory，setBeanFactory(BeanFactory)传递的是Spring工厂自身
- ApplicationContextAware实现：
如果这个Bean已经实现了ApplicationContextAware接口，会调用setApplicationContext(ApplicationContext)方法，传入Spring上下文。面对应用开发，一般都是实现ApplicationContextAware，而不是直接去操作BeanFactory
- postProcessBeforeInitialization接口实现-初始化预处理:
postProcessBeforeInitialization接口实现-初始化预处理
- init-methood
- postProcessAfterInitialization:如果这个Bean关联了BeanPostProcessor接口，将会调用postProcessAfterInitialization(Object obj, String s)方法。
- Destroy过期自动清理阶段:
当Bean不再需要时，会经过清理阶段，如果Bean实现了DisposableBean这个接口，会调用那个其实现的destroy()方法
- destory-method 自配置清理
- 最后，如果这个Bean的Spring配置中配置了destroy-method属性，会自动调用其配置的销毁方法
