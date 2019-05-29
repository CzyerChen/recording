### springmvc的请求流程
1.浏览器发起http请求，请求到DispatcherServlet

2.HandlerMapping 寻找处理器 寻找到处理器Controller

3.调用处理器，DispatcherServlet将请求提交到Controller

4.5.调用业务处理和返回结果：Controller调用业务逻辑处理后，返回ModelAndView

6.7.处理视图映射并返回模型： DispatcherServlet查询一个或多个ViewResoler视图解析器，找到ModelAndView指定的视图

8.Http响应：ModelAndView反馈浏览器HTTP,视图负责将结果显示到客户端        


### springboot的原理
#### spring boot有什么特点
- 创立独立的Spring应用程序
- 嵌入tomcat，无需war部署
- 简化maven配置，做好很好的自我版本管理
- 自动化配置java bean 通过IOC和DI ，做了很好的bean周期的管理，autoconfiguration
- 提供生产就绪型功能，提供一些监控和健康检查
- 没有代码生成和XML配置的需求

### JPA的原理
- JPA是一套ORM的规范，很典型实现JPA规范的就是Hibernate框架，它利用了很好的领域设计理念，将数据库底层交互做了完美的封装，并在保证吞吐和效率的前提下，做了更好更多的适配

#### 事务：表示一组操作要么都执行要么都不执行，具有ACID的特点，A原子性，C一致性,I隔离性，D持久性
#### 本地事务
- 紧密依赖于底层资源管理器（例如数据库连接 )，事务处理局限在当前事务资源内。此种事务处理方式不存在对应用服务器的依赖，因而部署灵活却无法支持多数据源的分布式事务
- 手动连接的话通过conn.setAutoCommit(false）conn.commit();conn.rollback();

#### 分布式事务
- 通过JTA接口，实现编程式事务，或者以来容器提供响应式事务
- 两阶段提交：准备阶段 提交阶段
```text
1准备阶段
事务协调者(事务管理器)给每个参与者(资源管理器)发送Prepare消息，每个参与者要么直接返回失败(如权限验证失败)，要么在本地执行事务，写本地的redo和undo日志，但不提交，到达一种“万事俱备，只欠东风”的状态

2提交阶段：
如果协调者收到了参与者的失败消息或者超时，直接给每个参与者发送回滚(Rollback)消息；否则，发送提交(Commit)消息；参与者根据协调者的指令执行提交或者回滚操作，释放所有事务处理过程中使用的锁资源。(注意:必须在最后阶段释放锁资源)

```