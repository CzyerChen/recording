- 向大佬们学习，边学习，边记录，边分享
- 用严谨的态度，认真学习，认真领悟
- 不只是为了眼前，苟且生活；更为了，拥抱明天
- 关于代码都是互联网上找的，有些自己敲拿来debug学习的，如有侵权，请联系我我及时删除
- 参考Holis - to be a javaer

**主要包括：** 
1. 排序算法学习
2. redis缓存机制学习
3. 并发编程学习
4. Netty/NIO学习
5. 设计模式学习
6. http学习
7. Java核心数据结构学习
8. 垃圾回收算法学习
9. SpringAOP学习
10. SpringIOC学习
11. 数据库基础学习
12. 数据库优化学习
13. zookeeper一致性学习

**待总结：** 
1. 分布式事务学习
2. 数据库设计与优化学习
3. Docker学习
4. Dubbo学习
5. springcloud学习
6. spring boot学习
7. Elasticsearch学习
8. lucene 学习
9. K8S学习
10. Maven学习
11. 消息队列和管道技术学习
12. tomcat源码学习
13. git使用学习
14. 分布式存储
15. 分布式计算
16. 自动化部署
17. 自动化测试

**TODO-LIST:** 
1. mybatis-spring 底层加载原理
2. jpa-spring 底层加载原理
3. NIO netty/linux中的运用
4. spring5特性总结


### 总体计划 - 把大神的指导作为规划
#### 一、基础篇
1.JVM 内存结构
- 堆 ✔
- 栈 ✔
- 方法区 ✔
- 直接内存 ✔
- 堆和栈的区别 ✔

2.java内存模型
- 内存可见性 ✔
- 重排序
- 顺序一致性
- volatile✔
- 锁✔
- final 

3.垃圾回收
- 内存分配策略 ✔
- 垃圾收集器（CMS,G1，并行） ✔
- GC算法 ✔
- GC参数
- 对象存活的判定

4.JVM参数及调优

5.JAVA对象模型
- oop-klass
- 对象头

6.HotSpot
- 即时编译器
- 编译优化

7.类加载器
- classLoader ✔
- 类加载过程 ✔
- 双亲委派（破坏双亲委派）
- 模块化（jboss modules , osgi.jigsaw）

8.编译与反编译
- javac ✔
- javap
- jad ✔
- CRF

9.Java基础知识
- 源代码：
  - String ✔
  - Integer ✔
  - Long ✔
  - Enum ✔
  - BigDecimal
  - ThreadLocal✔
  - ClassLoader & URLClassLoader ✔
  - ArrayList & LinkedList ✔
  - HashMap & LinkedHashMap & TreeMap & CouncurrentHashMap、HashSet & LinkedHashSet & TreeSet ✔
   
- java变量类型✔
- 熟悉Java String的使用，String的各种函数
  - JDK 6和JDK 7中substring的原理及区别
  - replaceFirst、replaceAll、replace区别
  - String对“+”的重载 ✔
  - String.valueOf和Integer.toString的区别 ✔
  - 字符串的不可变性 ✔
  
- 自动拆箱装箱
  - Integer的缓存机制 ✔
  
- Java中各种关键字的原理及用法
  - transient ✔
  - instanceof ✔
  - volatile ✔
  - synchronized ✔
  - final
  - static ✔
  - const 
  
- 集合类
  - 常用集合类的使用 ✔
  - ArrayList和LinkedList和Vector的区别 
  - SynchronizedList和Vector的区别
  - HashMap、HashTable、ConcurrentHashMap区别 ✔
  - Java 8中stream相关用法
  - apache集合处理工具类的使用
  - 不同版本的JDK中HashMap的实现的区别以及原因
  
- 枚举
  - 枚举的用法  ✔
  - 枚举与单例 ✔
  - Enum类 ✔
  
- Java IO&Java NIO
  - BIO、NIO（多路复用）和AIO的区别 ✔
  - 三种IO的用法与原理 ✔
  - netty ✔
  
- java反射与javassist
  - 反射与工厂模式 ✔
  - java.lang.reflect.*
  
- Java序列化
  - 什么是序列化与反序列化 ✔
  - 为什么序列化 ✔
  - 序列化底层原理
  - 序列化与单例模式
  - protobuf
  - 为什么说序列化并不安全
  
- 注解
  - 元注解
  - 自定义注解 ✔
  - Java中常用注解使用 ✔
  - 注解与反射的结合 ✔
  
- JMS
  - 什么是Java消息服务
  - JMS消息传送模型
  
- JMX
  - java.lang.management.*
  - javax.management.*
  
- 泛型
  - 泛型与继承 ✔
  - 类型擦除  ✔
  - 泛型中K T V E 
  - object等的含义
  - 泛型各种用法
  
- 单元测试
  - junit
  - mock
  - mockito
  - 内存数据库（h2）
  
- 正则表达式
  - java.lang.util.regex.*
  
- 常用的Java工具库
  - commons.lang, commons.*... guava-libraries netty
  
- 什么是API&SPI

- 异常
  - 异常类型 ✔
  - 正确处理异常 ✔
  - 自定义异常 ✔
  
- 时间处理
  - 时区、时令、Java中时间API(不同jdk版本中) ✔
  
- 编码方式
  - 解决乱码问题  ✔
  - 常用编码方式  ✔
  
- 语法糖
  - Java中语法糖原理
  - 解语法糖

- Java并发编程
  - 什么是线程 ✔
  - 与进程的区别 ✔

- 阅读源代码，并学会使用
  - Thread、Runnable、Callable ✔
  - ReentrantLock、ReentrantReadWriteLock  ✔
  - Atomic* ✔
  - Semaphore
  - CountDownLatch ✔
  - ConcurrentHashMap  ✔
  - Executors  ✔

- 线程池
  - 自己设计线程池  ✔
  - submit() 和 execute() ✔

- 线程安全
  - 死锁，死锁如何排查 ✔
  - Java线程调度 ✔
  - 线程安全和内存模型的关系

- 锁
  - CAS ✔
  - 乐观锁与悲观锁 ✔
  - 数据库相关锁机制
  - 分布式锁 ✔
  - 偏向锁、轻量级锁、重量级锁 ✔
  - monitor
  - 锁优化、锁消除、锁粗化
  - 自旋锁 ✔
  - 可重入锁 ✔
  - 阻塞锁
  - 死锁 ✔

- 死锁
  - volatile  ✔
  - happens-before
  - 编译器指令重排和CPU指令重

- synchronized
  - synchronized是如何实现的
  - synchronized和lock之间关系 ✔
  - 不使用synchronized如何实现一个线程安全的单例

- sleep 和 wait
- wait 和 notify
- notify 和 notifyAll
- ThreadLocal ✔
- 写一个死锁的程序
- 写代码来解决生产者消费者问题
- 守护线程
- 守护线程和非守护线程的区别以及用法

#### 二、 进阶篇
1.Java底层知识
- 字节码、class文件格式

- CPU缓存，L1，L2，L3和伪共享

- 尾递归

- 位运算
  - 用位运算实现加、减、乘、除、取余

2.设计模式
- 了解23种设计模式  ✔

- 会使用常用设计模式 ✔

- 单例、策略、工厂、适配器、责任链。 ✔

- 实现AOP ✔

- 实现IOC ✔

- 不用synchronized和lock，实现线程安全的单例模式

- nio和reactor设计模式

3.网络编程知识
- tcp、udp、http、https等常用协议 ✔

- 三次握手与四次关闭、流量控制和拥塞控制、OSI七层模型、tcp粘包与拆包

- http/1.0 http/1.1 http/2之前的区别

- Java RMI，Socket，HttpClient

- cookie 与 session

- cookie被禁用，如何实现session

- 用Java写一个简单的静态文件的HTTP服务器

- 实现客户端缓存功能，支持返回304 实现可并发下载一个文件 使用线程池处理客户端请求 使用nio处理客户端请求 支持简单的rewrite规则 上述功能在实现的时候需要满足“开闭原则”

- 了解nginx和apache服务器的特性并搭建一个对应的服务器  ✔

- 用Java实现FTP、SMTP协议 ✔

- 进程间通讯的方式
  - 什么是CDN？如果实现？
  - 什么是DNS？
  - 反向代理 ✔
  
4.框架知识
  - Servlet线程安全问题
  - Servlet中的filter和listener
  - Hibernate的缓存机制
  - Hiberate的懒加载
  - Spring Bean的初始化 ✔
  - Spring的AOP原理 ✔
  - 自己实现Spring的IOC ✔
  - Spring MVC ✔
  - Spring Boot2.0 ✔
  - Spring Boot的starter原理，自己实现一个starter
  - Spring Security
  
5.应用服务器知识
  - JBoss
  - tomcat ✔
  - jetty
  - Weblogic
  
- 工具
git & svn  ✔
maven & gradle ✔

#### 三、 高级篇
1.新技术
- Java 8  ✔
  - lambda表达式✔
  - Stream API ✔

- Java 9
  - Jigsaw、Jshell
  - Reactive Streams

- Java 10
  - 局部变量类型推断
  - G1的并行Full GC
  - ThreadLocal握手机制

- Spring 5
  - 响应式编程

- Spring Boot 2.0

2.性能优化

- 使用单例、使用Future模式、使用线程池、选择就绪、减少上下文切换、减少锁粒度、数据压缩、结果缓存

3.线上问题分析

- dump获取✔
  - 线程Dump、内存Dump、gc情况

- dump分析
  - 分析死锁、分析内存泄露

- 自己编写各种outofmemory，stackoverflow程序
  - HeapOutOfMemory、 Young OutOfMemory、MethodArea OutOfMemory、ConstantPool OutOfMemory、DirectMemory OutOfMemory、Stack OutOfMemory Stack OverFlow

- 常见问题解决思路
  - 内存溢出、线程死锁、类加载冲突

- 使用工具尝试解决以下问题，并写下总结
  - 当一个Java程序响应很慢时如何查找问题、

  - 当一个Java程序频繁FullGC时如何解决问题

  - 如何查看垃圾回收日志

  - 当一个Java应用发生OutOfMemory时该如何解决

  - 如何判断是否出现死锁

  - 如何判断是否存在内存泄露

4.编译原理知识
- 编译与反编译
- Java代码的编译与反编译✔
- Java的反编译工具✔
- 词法分析，语法分析（LL算法，递归下降算法，LR算法），语义分析，运行时环境，中间代码，代码生成，代码优化

5.操作系统知识
- Linux的常用命令✔
- 进程同步
- 缓冲区溢出
- 分段和分页
- 虚拟内存与主存

6.数据库知识
- MySql 执行引擎✔
- MySQL 执行计划✔
- 如何查看执行计划，如何根据执行计划进行SQL优化✔
- SQL优化
- 事务✔
- 事务的隔离级别✔
- 事务能不能实现锁的功能
- 数据库锁
- 行锁、表锁、使用数据库锁实现乐观锁✔
- 数据库主备搭建✔
- binlog的使用✔
- 内存数据库✔
- h2
- 常用的nosql数据库
  - redis ✔
  - memcached

- 分别使用数据库锁、NoSql实现分布式锁
- 性能调优

7.数据结构与算法知识
  - 简单的数据结构✔
  - 栈、队列、链表、数组、哈希表✔

- 树
  - 二叉树
  - 字典树
  - 平衡树
  - 排序树
  - B树、B+树、R树
  - 多路树
  - 红黑树

- 排序算法
  - 各种排序算法和时间复杂度 深度优先和广度优先搜索 全排列、贪心算法、KMP算法、hash算法、海量数据处理
  

8.大数据知识
- Zookeeper
  - 基本概念
  - 常见用法
  - 一致性算法
  - 节点选举流程
  

- Solr，Lucene，ElasticSearch
  - 在linux上部署solr，solrcloud，，新增、删除、查询索引✔

- Storm，流式计算，了解Spark，S4
  - 在linux上部署使用spark ,并将任务提交✔
  - 在linux上部署storm，用zookeeper做协调，运行storm hello world，local和remote模式运行调试storm topology。

- Hadoop，离线计算
  - HDFS、MapReduce✔

- 分布式日志收集flume，kafka，logstash✔
- 数据挖掘，mahout

9.网络安全知识
- 什么是XSS
  - XSS的防御

- 什么是CSRF
  - 什么是注入攻击
  - SQL注入、XML注入、CRLF注入

- 什么是文件上传漏洞
  - 加密与解密
  - MD5，SHA1、DES、AES、RSA、DSA✔
  

- 什么是DOS攻击和DDOS攻击
  - memcached为什么可以导致DDos攻击、什么是反射型DDoS

- SSL、TLS，HTTPS
  - 如何通过Hash碰撞进行DOS攻击
  - 用openssl签一个证书部署到apache或nginx✔
  
#### 四、架构篇
- 分布式
  - 数据一致性、服务治理、服务降级

- 分布式事务
  - 2PC、3PC、CAP、BASE、 可靠消息最终一致性、最大努力通知、TCC✔

- Dubbo
  - 服务注册、服务发现，服务治理✔
 

- 分布式数据库
  - 怎样打造一个分布式数据库✔
  - 什么时候需要分布式数据库✔
  - mycat(轻量级分布式数据库引擎，最好支持的是mysql,但是对于oracle\postgresql 等都可以通过jdbc的方式支持)✔
  - otter(阿里关于解决异地数据迁移的工具，目前支持mysql\oracle)✔
  - greenplum(内嵌postgresql)✔
  - HBase✔

- 分布式文件系统
  - mfs、fastdfs

- 分布式缓存
  - 缓存一致性
  - 缓存命中率
  - 缓存冗余

- 微服务
  - SOA✔
  - 康威定律

- ServiceMesh
  - Docker & Kubernets ✔
  - Spring Boot✔
  - Spring Cloud✔
  - Istio✔
  
- 高并发
  - 分库分表
  - CDN技术
  
- 消息队列
  - ActiveMQ
  - rabbitMQ✔
  - rocketMQ

- 监控
  - 监控什么
  - CPU、内存、磁盘I/O、网络I/O等
  - 监控手段 zabbix的使用/ELK平台
  - 进程监控、语义监控、机器资源监控、数据波动
  - 监控数据采集
  - 日志、埋点
  - Dapper
  
- 负载均衡
  - tomcat负载均衡
  - Nginx负载均衡✔

- DNS
  - DNS原理、DNS的设计

- CDN
  - 数据一致性

#### 五、 扩展篇
- 云计算
  - IaaS、SaaS、PaaS、虚拟化技术、openstack、Serverlsess

- 搜索引擎
  - Solr✔
  - Lucene✔
  - Nutch
  - Elasticsearch✔

- 权限管理
  - Shiro✔

- 区块链
  - 哈希算法、Merkle树、公钥密码算法、共识算法、Raft协议、Paxos 算法与 Raft 算法、拜占庭问题与算法、消息认证码与数字签名

- 比特币
  - 挖矿、共识机制、闪电网络、侧链、热点问题、分叉

- 以太坊
  - 超级账本

- 人工智能
  - 数学基础、机器学习、人工神经网络、深度学习、应用场景。

- 常用框架
  TensorFlow、DeepLearning4J

- 其他语言
  - Scala 类javay语言
  - Python 
  - Groovy、Go、NodeJs、Swift、Rust



