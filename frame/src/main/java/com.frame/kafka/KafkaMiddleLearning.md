kafka中级教程将从以下几个方面展开：
- Kafka架构和原理
- Kafka消息存储方式
- Kafka集群Leader节点选举算法
- Partition Replication的分配
- Replication数据同步
- Kafka集群Leader节点选举算法
- 消息传输保障
- 消息去重
- Controller节点
- Broker fail over流程
- Producer消息路由
- Consumer 和Consumer Group
- Offset的保存
- 内部网络框架
- Rebalance机制
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
    - 大体同步思想，数据复制算法能够保证，leader宕机，回统其他节点选举出leader，并接收客户端消息并写入。
      leader负责维护和跟踪ISR（副本同步队列）中所有滞后follower的状态，producer发送一条消息到broker后，leader写入消息复制到所有follower。
      消息提交之后才成功复制到所有同步副本。
      滞后太多的follower会被leader从ISR中剔除。

- 复制原理和同步方式
```text
 首条消息                               HW                              LEO
|-------|-------|-------|-------|-------|-------|-------|-------|-------|
|       |       |       |       |       |       |       |       |       |
|  1    |  2    |   3   |   4   |   5   |   6   |   7   |   8   |   9   |
|       |       |       |       |       |       |       |       |       |
|-------|-------|-------|-------|-------|-------|-------|-------|-------|
```
1.首先介绍两个名词概念
    - HW（highwatermark）：高水位，是指consumer能够看到的此partition的位置，取一个partition对应的ISR中最小的LEO作为HW，consumer最多只能消费到HW所在的位置
    - LEO（LogEndOffset）:日志偏移最终位置，是指日志同步最后一个位移点的位置

2.零拷贝
  数据需要持久化到磁盘（Producer-> Broker），网络传输（Broker -> consumer）需要经过很多次上下文切换，才能实现。
  - 传统模式交互---> 四次拷贝、四次上下文切换
   例如磁盘文件的发送;
     - 1.数据从持久化磁盘上读取到内核内存buffer中（data  ---DMA拷贝---> 内核态buffer）
     - 2.用户调用，内核内存buffer数据拷贝到用户内存buffer（内核态buffer  ---CPU拷贝---> 用户态buffer）
     - 3.用户程序接收到磁盘文件将它使用socket发送（用户态buffer ---CPU拷贝---> 内核态buffer）
     - 4.内核调用底层网络传输（内核态buffer ---DMA拷贝---> NIC buffer ）
     
  - Linux 2.4+内核通过sendfile系统调用，提供了零拷贝 
     - 以上由于操作系统提供的方法，能够通过JVM底层来调用操作系统指令来最终实现零拷贝
     - 整个流程直接就是，从磁盘读取文件 ，通过DMA拷贝，通过FileChannel的转换发送到NIC buffer，调用底层sendfile进行发送 
     - 借用操作系统的支持，充分利用零拷贝的优势，大大提升了持久化的吞吐量    
  
3.数据同步ISR以及HW和LEO流转流程
描述数据状态：现在一个Leader(1,2,3),follower(1,2,3),follower(1,2,3),HW/LEO都位于3
   - 1）follower都已经赶上leader的情况下，leader没有新数据加入，follower也阻塞
   - 2）Producer生产消息后，发送到leader，leader首先在它的partition，segment文件后面顺序追加新添加的数据
     - Leader(1,2,3，4，5),follower(1,2,3),follower(1,2,3),HW位于3，LEO位于5
   - 3）leader有新消息，leader有监控ISR队列，因而通知队列里的follower来获取新消息。
   - 4）follower启动复制同步，其中一个follower完全赶上、其他没有完全复制完成时，状态如下
     - Leader(1,2,3,4,5),follower(1,2,3,4,5),follower(1,2,3,4),HW更新为4，LEO位于5
     - 消费者只能获取HW之前的数据，在刚开始同步的时候，只能获取3及以前的数据，当HW更新为4的时候，消费者可以更新到4及以前的数据
   - 5）ISR所有节点都成功复制了消息5,leader HW更新为5，所有follower都赶上就继续被阻塞，等新消息，如果超时滞后，就被踢出ISR，放入OSR
     - Leader(1,2,3,4,5),follower(1,2,3,4,5),follower(1,2,3,4,5),HW/LEO都位于5   

4.副本同步队列ISR
  - ISR In-Sync Replicas --- 副本同步队列
  - 副本同步会对Kafka吞吐量带来一部分影响，但是副本的机制大大提高了高可用性和容错性
  - 副本数量默认为1，可以通过broker的参数offsets.topic.replication.factor指定
  - 所有副本Asigned Replicas (AR),所有的副本可以参与复制同步任务，副本节点大于等于副本数
  - 副本节点可能由于滞后同步而被剔除副本同步队列，存入OSR（Outof-Sync Replicas）-非副本同步队列
  - AR = ISR + OSR 
  - Kafka 0.10.x版本后移除了replica.lag.max.messages参数，只保留了replica.lag.time.max.ms作为ISR中副本管理的参数。由于Kafka的流量不确定，这个值是broker全局的，设置不当，会对ISR队列有影响，造成频繁的出对入队问题。
   

5.整体信息的维护
    - Kafka的ISR的管理最终都会反馈到Zookeeper节点上，目录/brokers/topics/[topic]/partitions/[partition]/state
    - 这个数据可以有两个部分对其维护：
        - Controller来维护：Kafka集群中的其中一个Broker会被选举为Controller，主要负责Partition管理和副本状态管理，也会执行类似于重分配partition之类的管理任务
        - leader来维护：leader有单独的线程定期检测ISR中follower是否脱离ISR, 如果发现ISR变化，则会将新的ISR的信息返回到Zookeeper的相关节点中
        
6.数据可靠性和持久性的保证
  - Kafka的可靠性可以配置，主要是通过ack机制来保证，当producer向leader发送数据时，可以通过request.required.acks参数来设置数据可靠性的级别
    - 1(默认)：producer在ISR中的leader已成功收到的数据并得到确认后发送下一条message，leader宕机则会丢失数据
    - 0：producer无需等待来自broker的确认而继续发送下一批消息，数据传输效率最高，数据可靠性确是最低
    - -1：producer需要等待ISR中的所有follower都确认接收到数据后才算一次发送完成，可靠性最高。但是当ISR中只有leader的时候，也无法保证数据，退化成acks=1的情况
  - 一般情况下，我们需要保证消息的可用性，那就需要进行request.required.acks=-1的配置，但是仅此一个并不能够完成，还需要min.insync.replicas这个参数配合，默认为1，这个参数能够对ISR中副本数进行判断，小于这个数，客户端请求会被阻塞
  - 以下对ack流程进行分析

#### request.required.acks=1
这种情况下，客户端发送消息，只要leader写入数据成功就返回成功，没有等leader复制同步。因而可能发生leader宕机，数据丢失的问题
1. producer发送消息到leader
- Leader(1,2,3),follower(1,2,3),follower(1,2,3),HW位于3，LEO位于3
2. leader本地写日志，返回客户端成功，leader准备数据同步
- Leader(1,2,3,4,5),follower(1,2,3),follower(1,2,3),HW位于3，LEO位于5
3. leader 宕机，follower获取数据失败，并重新进行leader选举
- follower(1,2,3),follower(1,2,3) => 选举一个leader Leader(1,2,3),follower(1,2,3)HW位于3，LEO位于3
4. 以上情况，数据4，5丢失，不可复得

#### request.required.acks=-1
这种情况下，客户端发送消息，需要至少N（N>=1）个follower完全复制完数据，才返回成功，保证数据的高可用。
配合配置：producer.type=sync，replication.factor>=2，min.insync.replicas>=2
- 情况一： leader在副本节点全部复制完毕的情况下宕机，那么剩下的节点数据都是完整的，通过选举算法选举出新leader即可
- 情况二：leader在副本节点复制阶段宕机，此机制下，针对没有完全同步完的消息认为没有commit，因而客户端会对受到返回失败的数据进行重发
  - producer发送消息到leader
  - leader本地写日志成功
    - Leader(1,2,3,4,5),follower(1,2,3),follower(1,2,3),HW位于3，LEO位于5
  - follower准备开始数据同步
  - follower1同步完毕，followe2还未同步完成，此时leader宕机，由于复制工作未完成，HW未更新，返回客户端错误，因而客户端将数据重发
    -  follower(1,2,3,4,5),follower(1,2,3,4),HW位于3，LEO位于5
  - follower1被选举为leader
   -  leader(1,2,3,4,5),follower(1,2,3,4),HW位于3，LEO位于5
  - follower向leader同步数据
  - producer进行数据重发，到选举出来的新leader上
- 第二种情况，如果同步正在进行中，leader宕机，那么另外选举出来的leader可能出现消息重复的问题，如果选举的leader未开始同步，那就不会有消息重复的问题

7.关于HW的思考
- 在leader宕机，其余follower选举后产生新的leader，并且开始数据重新同步复制的过程中，HW起了重要作用。
- 由于leader的选举是通过ISR队列中顺序选举出来的，和各节点自身的HW和LEO没有关系
- 因而新节点选举出来，各个其他节点的原复制同步进度都不一致，和新leader上的数据位点也都可能不一样
- 为了避免数据的不一致，统一规定从新leader的HW开始，各个节点从HW这个位点开始截断，重新向后复制同步
- 通过以上的规定，能够清晰、准确的进行故障恢复后的重同步工作

### 四、Partition Replication的分配
Kafka为了更好地实现负载均衡，会通过算法，将partition均匀分配到整个集群上，同时也通过副本地均匀分配，提供容错能力
以下介绍Kafka副本replica算法：
- 将所有broken和待分配的partition排序
- 将第i个partition分配到第i mod n 个上面
- 将第i个partition的第j个Replica分配到第（i+j）mod n 个broken上

### 五、Kafka集群Leader节点选举算法
- Kafka的集群leader选举与微软的PacificA算法类似，可以容忍一定副本的失败
- 一个基本的原则就是，如果leader不在了，新的leader必须拥有原来的leader commit的所有消息

1.普通宕机恢复策略
- 以上原则说明了，leader希望更多的follower能够快速完全的复制同步自己的消息，那自己宕机也不会有什么问题，可是多副本的同步会使吞吐量下降
- 当存在2n+1个总副本的时候，Kafka要求在commit消息之前，必须有n+1个副本已经同步完全，失败的副本不超过n个，这样才能保证宕机后的选举

2.整个partition及副本宕机恢复策略
- 整个partition及副本宕机意味着一部分数据的丢失，并且不可复得，那在这种情况下，应该怎样选择恢复策略
- Kafka在Zookeeper中为每一个partition动态的维护了一个ISR，这个ISR里的所有replication都跟上了leader，只有ISR里的成员才能有被选为leader的可能(unclean.leader.election.enable=false)
- 方法一：等待副本的自动恢复，并且成为leader，进行数据的修复
  - 这种方式能最大程度保全消息，但是这个恢复的过程可能很长，甚至无法恢复
- 方法二：选择一个ISR以外的节点，作为leader，作为consumer的数据源
  - 消息的丢失在所难免，但是能够及时调整，对外提供服务
  - 可以通过以下方法配置，实现这种恢复策略
     - unclean.leader.election.enable=true，也可以将此参数设置为false来启用第一种策略
     - unclean.leader.election.enable这个参数对于leader的选举、系统的可用性以及数据的可靠性都有至关重要的影响

3.其他选举算法：zookeeper ZAB/Raft /Viewstamped Replication 

4.场景分析


### 六、消息传输保障
- At most once: 消息可能会丢，但绝不会重复传输
- At least once：消息绝不会丢，但可能会重复传输
- Exactly once：每条消息肯定会被传输一次且仅传输一次


### 七、消息去重

### 八、Controller节点


### 九、Broker fail over流程

### 十、Producer消息路由

### 十一、Consumer 和Consumer Group

### 十二、Offset的保存

### 十三、内部网络框架

### 十四、Rebalance机制

### 十五、Kafka自带管理工具

### 十六、Kafka监控工具

### 十七、Kafka的使用DEMO



