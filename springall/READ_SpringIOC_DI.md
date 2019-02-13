以上内容涉及了很多jdk比较深入的东西，这边也总结以下spring中相关涉及的知识点

**IOC: 控制反转**
 
**DI：依赖注入** 

- IOC 依赖 DI,使容器负责组件的装配
- 支持依赖注入、依赖检查、自动装配、支持集合、指定初始化方法和销毁方法、支持回调某些方法
- Bean的生命周期，都交由容器来管理，控制Bean的依赖注入
- spring的设计使用两个接口代表容器：BeanFactory / ApplicationContext
- spring类加载，BeanFactory，读取xml配置文件，加载在Map中，key为BeanName,Value使Bean实例，只提供注册和获取，使低级容器
- ApplicationContext比BeanFactory提供了更多的功能，是高级容器，可以进行资源的获取、支持各种消息

### 手写简易Spring
https://www.cnblogs.com/ITtangtang/p/3978349.html

