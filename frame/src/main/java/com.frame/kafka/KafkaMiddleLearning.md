kafka中级教程将从以下几个方面展开：
- Kafka架构和原理
- Kafka消息存储方式
- Kafka集群Leader节点选举算法
- Partition Replication的分配
- Replication数据同步
- 如何应对所有的Replica宕机
- Broker fail over流程
- Controller节点
- Producer消息路由
- Consumer 和Consumer Group
- Offset的保存
- Kafka分区机制、Partition分配算法
- 分配和重新分配的协调者
- 消息消费的定义
- Kafka自带管理工具
- Kafka监控工具
- Kafka的使用DEMO

### 一、Kafka架构和原理
```text
( Producer )    ( Producer )  ....   ( Producer )    ( Producer )
     |                |                    |               |
     |                |                    |               |
    \|/              \|/                  \|/             \|/
    |--------------------------------------------------------|           
    |   [ Kafka ]          [ Kafka ]            [ Kafka ]    | Kafka-Cluster --->ZookeeperCluster
    |   [ Broker]          [ Broker]            [ Broker]    | 
    |--------------------------------------------------------|
    /|\              /|\                  /|\             /|\
     |                |                    |               |
     |                |                    |               |  
( Consumer )     ( Consumer )  ....   ( Consumer )    ( Consumer )
```
组成：
1. Producer:可以是业务数据、日志数据、页面缓存等，Kafka发送端采用push模式将消息发送到broker
2. Consumer(ConsumerGroup):kafka消费端采用pull模式订阅并消费消息
3. broker : kafka服务节点
4. zookeeper: kafka通过zookeeper管理集群配置，选举leader,在consumer group变化时进行rebalance
5. Partition: 
    - 是topic真实存储数据的部分，一个topic被分成多个partition，每个partition在存储层面是append log文件
    - 任何消息都会被顺序追加到append log的尾部,然后通过offset来维护消息所在的具体位置。因为append log的顺序写，所以效率很高，吞吐量也很大
    - 每条消息被发送到broker中，会根据partition规则选择被存储到哪一个分区，这个规则可以在分发的时候指定

### 二、Kafka消息存储方式
- Kafka中消息按照topic的逻辑概念存储
- 在物理存储上，topic的数据会分到不同的partition上面存储，每个partiton是按照0-（N-1）编号
- partition的文件夹数，依靠你server.properties当中的配置，或者在生成topic的时候手动配置的分区数
- partition只是一个文件夹，实际存储数据的还是在segment上，segment里面存储分为大小固定的index文件和log文件，index文件存储消息索引位置，log文件存储实际数据
- partition 目录文件结构,partition全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值，数值大小为64位，20位数字字符长度，没有数字用0填充
```text
00000000000000000000.index 
00000000000000000000.log 
00000000000000170410.index 
00000000000000170410.log 
00000000000000239430.index 
00000000000000239430.log 
```
“.index”索引文件存储大量的元数据，“.log”数据文件存储大量的消息，索引文件中的元数据指向对应数据文件中message的物理偏移地址


### 三、Replication数据同步
- 数据同步基本概念
    - kafka为了保证消息的可靠性，Kafka每个topic有N个副本-replica，N是复制因子-replication-factor的数量
    - Kafka依靠多副本的机制，实现故障broker的自动转移，当Kafka集群中一个broker失效，仍能保证高可用
    - Kafka在实现复制的过程中，有两种角色，一种是leader ，负责处理partition的读写请求，一种是follower，负责定期从leader上进行数据复制

- 复制原理和同步方式
1. 首先介绍两个名词概念
    - HW（highwatermark）：高水位

- 数据同步流程
描述数据状态：现在一个Leader(1,2,3),follower(1,2,3),follower(1,2,3),HW/LEO都位于3

- 副本同步队列ISR

- 整体信息的维护

- 数据可靠性和持久性的保证

- 关于HW的进一步探讨

### 四、Partition Replication的分配
Kafka为了更好地实现负载均衡，会通过算法，将partition均匀分配到整个集群上，同时也通过副本地均匀分配，提供容错能力
以下介绍Kafka副本replica算法：



### 五、Kafka集群Leader节点选举算法


### 六、如何应对所有的Replica宕机

### 七、Broker fail over流程

### 八、Controller节点

### 九、Producer消息路由

### 十、Consumer 和Consumer Group

### 十一、Offset的保存

### 十二、Kafka分区机制、Partition分配算法

### 十三、分配和重新分配的协调者

### 十四、消息消费的定义

### 十五、Kafka自带管理工具

### 十六、Kafka监控工具

### 十七、Kafka的使用DEMO



