## 一.手写RPC框架
> rpc原理就是通过读取接口定义，然后通过jdk动态代理，依赖反射，实现方法的调用

调用流程图（亲手画的高清图哦）：
![avatar](https://raw.githubusercontent.com/CzyerChen/recording/master/img/rpc%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B.png)

- Producer Server
1. 接受socket消息，解析需要调用的类名称
2. 将消息通过类名反射生成对应类
3. 转为调用本地方法
4. 传入获取的参数，得到返回值
5. 将处理结果转化为消息，并发送

- Consumer Client
1. 调用某个接口的方法
2. 将远程调用转为消息
3. 找到服务提供者地址，并发送消息
4. 获取服务提供者的处理结果
代码请见子模块rpc-producer/rpc-consumer

## 二.利用netty实现高性能rpc调用尝试
作为一个小菜鸡，边练习，边分享所得，并且总结比较优雅的coding方式
- Producer Server
- Consumer Client
代码请见子模块rpc-producer/rpc-consumer

--关于公用public部分代码，真正实践中可以通过新建一个共享模块，然后依赖同一个模块来存放，将其install后就可调用，此处没有做处理

--关于代码，也是拼拼凑凑借鉴的互联网上的前辈们的资源，自己亲手书写调试的

--在书写的过程中，将很多零碎学习到的知识点整合在了一起，主要用到：
1. 并发ConcurrentHashMap，基于分段锁机制，读并发，写在一定程度上并发
2. 类加载器的类型，rpc示例中使用了ClassResolvers.cacheDisabled
3. 显示建立线程池，了解每一个参数的含义，并设定参数值：new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
4. 了解反射的定义，并通过类名能够利用反射生成对象，使用对应的方法
5. jdk动态代理的实现
6. synchronized同步方法的使用
7. CountDownLatch的使用场景及使用
8. 程序计时器的使用
9. 断线重连的思想，及其实现

