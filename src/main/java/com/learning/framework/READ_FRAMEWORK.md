## 一、手写RPC框架
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


