### 特点
- 轻量级
- 控制反转 IOC
- 面向切面 AOP
- 容器
- 框架集合Collection


### 核心组件
```text
Data Access /Integration
JDBC
ORM
OXM
JMS
Transactions


Web
Web
Servlet
Protlet
Structs

AOP

Aspects

Instrucmentation

Core Container
Beans
Core
Context
Expression Language

Test
```
### 常用模块
- 核心容器：
提供Spring功能，BeanFactory,是工厂模式实现的，使用控制反转IOC模式将应用程序的配置和依赖性规范与实际应用代码分开
- Spring上下文：
是一个配置文件，向Spring提供上下文信息，包括企业服务，JNDI EJB 电子邮件，国际化，校验，调度等

- Spring AOP ：
通过配置管理特性,Spring AOP 模块直接将切面的编程功能集成到了Spring框架中，可以将一些通用任务，如安全，食物，日志等集中进行管理，提高了复用性和管理便捷性

- Spring DAO :
为JDBC DAO抽象层提供了有意义的异常层次结构，可用该结构来管理异常处理和不同数据库供应商抛出的错误信息，异常层次结构简化了错误处理
并且极大的降低了需要编写的异常代码数量

- Spring ORM :
 Spring框架插入了若干个ORM框架，提供ORM的对象关系工具，包括JDO Hibernatte iBatis Map,遵从Dao异常层次结构和Spring的通用事务
 
- Spring Web：
Web上下文模块建立在应用程序的上下文模块之上，还简化了处理多部分请求以及将请求参数绑定到域对象的工作上

- Spring MVC:
通过策略接口，MVC框架变成一个高度可配置的，容纳大量视图技术


### 常用注解
- @Controller 控制层组件，标记为Spring MVC Controller对象，并查看有没有@RequestMapping注解，将request请求header部分的值绑定到方法参数上
- @RestController = @Controller + @RequestBody
- @Component 泛指组件
- @Repository 标识仓库，主要用于ORM的领域设计部分只是
- @Service 用于标注业务层组件
- @ResponseBody : 异步请求返回json
- @RequestMapping 请求的接口映射
- @Autowired 对类成员变量 方法构造函数进行标注
- @PathVariable 用于请求URL中的模板变量映射到参数上
- @RequestParam Springmvc控制层获取参数
- @RequestHeader
- @ModelAttribute
- @SessionAttribute 将值写入session
- @Valid 进行参数校验
- @CookieValue 用来获取cookie中的值

### 第三方框架
- 权限：shiro:认证 授权 加密 会话管理 与web集成 缓存
- 缓存：Ehcache 非分布式内存缓存框架，快速 / redis 分布式内存缓存框架，使用ANSI C语言编写的框架
- 持久层框架：mybatis 使用原生jdbc语句，存储过程，高级映射 / hibernate 优秀的orm框架，使用领域设计思想封装原生SQL， 易用，入门门槛稍微高一点
- 定时任务：spring schduler 可是简单执行定期任务/quartz 能够实现复杂的定时任务，支持动态定时任务
- 校验框架 hibernate validator 支持对bean字段基于注解的校验 / oval 对象数据校验

 
