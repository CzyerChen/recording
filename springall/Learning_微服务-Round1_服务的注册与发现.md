> 服务的注册与发现，除了我们Spring cloud全家桶的eureka consul,还有参与分布式协调服务的zookeeper，能够用client进行信息的注册

### 客户端注册
#### part1 简单逻辑 -- 直接client去维护
```text
   service instance 
         |
         |register heartbeat unregister
        \|/
    service registry

```
- 客户端注册是服务自身要负责注册与注销的工作。当服务启动后向注册中心注册自身，当服务下线时注销自己。
- 期间还需要和注册中心保持心跳。心跳不一定要客户端来做，也可以由注册中心负责（这个过程叫探活）。
- 缺点：注册工作与服务耦合在一起，不同语言都要实现一套注册逻辑。


#### part2 第三方注册中心独立的服务Registrar
```text
                     health check
 Service instance  <--------------  registrar
        |                              |
        |                              |register heartbeat unregister
        |                              |
        |------->service registry<-----|
                    高可用
```
- 第三方注册由一个独立的服务Registrar负责注册与注销。当服务启动后以某种方式通知Registrar，然后Registrar负责向注册中心发起注册工作
- 同时注册中心要维护与服务之间的心跳，当服务不可用时，向注册中心注销服务
- 缺点是Registrar必须是一个高可用的系统

### 客户端发现
- 指客户端负责查询可用服务地址，以及负载均衡的工作
- 缺点也在于多语言时的重复工作，每个语言实现相同的逻辑
```text
     service instance                               
 registry aware HTTP client  -----------------------------------|                         
            |                                        			|
            |                             						|
            |												    |
            |												    |
            |													|
            |													|
           \|/													|
        service registry										|
           /|\													|
            |													|
       |--------------------------------------------|			|
       |						|					|			|
       |						|				 	|			|	
  service instance        service instance   service instance   |
      rest api               rest api           rest api		|
     registry client         registry client					|
     						   /|\							    |
     						    |-------------------------------|
```

### 服务端发现
- 服务端发现需要额外的Router服务，请求先打到Router，然后Router负责查询服务与负载均衡
- 缺点是保证Router的高可用
```text
    service instance                                service instance -------|
           |               高可用  load balance                          |
           |------------->Rounter ----------------> service instance -------|
                            |   											|
                            |                       service instance -------|
                            |query										    |
                            |												|register
                            |												|
                           \|/												|
                    service registry <--------------------------------------|
```
