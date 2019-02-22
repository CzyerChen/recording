kafka中级教程将从以下几个方面展开：
- Kafka架构和原理
- Kafka消息存储方式
- Replication数据同步
- Partition Replication的分配
- Kafka集群Leader节点选举算法
- 消息传输保障
- 消息去重
- Controller节点
- fail over流程
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
- 假设某个partition中的副本数为3，replica-0, replica-1, replica-2分别存放在broker0, broker1和broker2中。AR=(0,1,2)，ISR=(0,1)

- 当ISR中的replica-0出现crash的情况时，broker1选举为新的leader[ISR=(1)]，因为受min.insync.replicas=2影响，write不能服务，但是read能继续正常服务。
    - 此种情况恢复方案：尝试恢复(重启)replica-0，如果能起来，系统正常;
    - 如果replica-0不能恢复，需要将min.insync.replicas设置为1，恢复write功能。

- 当ISR中的replica-0出现crash，紧接着replica-1也出现了crash, 此时[ISR=(1),leader=-1],不能对外提供服务，
    - 此种情况恢复方案：尝试恢复replica-0和replica-1，如果都能起来，则系统恢复正常;
    - 如果replica-0起来，而replica-1不能起来，这时候仍然不能选出leader，因为当设置unclean.leader.election.enable=false时，leader只能从ISR中选举，当ISR中所有副本都失效之后，需要ISR中最后失效的那个副本能恢复之后才能选举leader, 即replica-0先失效，replica-1后失效，需要replica-1恢复后才能选举leader。保守的方案建议把unclean.leader.election.enable设置为true,但是这样会有丢失数据的情况发生，这样可以恢复read服务。同样需要将min.insync.replicas设置为1，恢复write功能;replica-1恢复，replica-0不能恢复，这个情况上面遇到过，read服务可用，需要将min.insync.replicas设置为1，恢复write功能;
    - replica-0和replica-1都不能恢复:当ISR中的replica-0, replica-1同时宕机,此时[ISR=(0,1)],不能对外提供服务，此种情况恢复方案：尝试恢复replica-0和replica-1，当其中任意一个副本恢复正常时，对外可以提供read服务。直到2个副本恢复正常，write功能才能恢复，或者将将min.insync.replicas设置为1;
    
5. kafka producer发送模式
- Kafka的发送模式由producer端的配置参数producer.type来设置
- 这个参数指定了在后台线程中消息的发送方式是同步的还是异步的，默认是同步的方式，即producer.type=sync
- 如果设置成异步的模式，即producer.type=async，可以是producer以batch的形式push数据，这样会极大的提高broker的性能，但是这样会增加丢失数据的风险
- 如果需要确保消息的可靠性，必须要将producer.type设置为sync
- batch模式可以通过参数配置，来设置批量的数量，batch.num.messages/batch.size

### 六、消息投递语义---消息传输保障
- At most once: 消息可能会丢，但绝不会重复传输
    - 先接收数据，然后commit offset，最后进行数据业务处理
    - 由于Kafka获取了commit，就会将offset再zookeeper里面更新，即使consumer在后去数据业务处理的时候宕机，下次读取也只能从下一条消息读取数据
- At least once：消息绝不会丢，但可能会重复传输
    - 先接收数据，然后进行业务处理，最后commit offset
    - 在进行业务数据处理完之后，如果consumer宕机，并没有发出commit命令，那么Kafka中offset并不会更新，下次数据请求依旧会从上一个读取过的offset开始向后消费
- Exactly once：每条消息肯定会被传输一次且仅传输一次，消息不会丢失（0.11中的实现，仅限于下游也是Kafka）
- 对于消息中间件的设计，一般都是实现了 at least once的模式，可能出现数据重复投递的问题，但是能够保证数据不丢失，对于去重就需要下游系统做幂等处理

### 七、消息去重
- 因为再Kafka消息传送的过程当中，由于网络波动、节点的性能等问题，可能出现消息的丢失和重传，在重传过程中必然就出现Kafka侧消息重复或者consumer侧消息重复的问题
- 从producer的角度，一旦Kafka节点crash, 客户端没有收到Kafka的commit消息，就会在节点恢复服务后重试，这个地方客户端可以采取通过GUID生成唯一id，再Kafka broker上进行去重，即可实现这种重试的消息去重问题
- 从consumer的角度，一旦Kafka消息发送过来，consumer出现故障，Kafka没有收到消息的commit，会在consumer恢复后进行重发，对于consumer接收的部分需要实现幂等，或者根据缓存方式自行去重消息；


### 八、Controller节点
- Controller节点主要用于集群leader节点出现异常宕机（broker fail over）后，进行新partition leader节点的选举工作
- 集群初始化阶段，每个节点都可以申请担任controller，这个选举依靠zookeeper ZAB 算法保证最终仅有一个broker可以担任controller
- 每个broker都在zookeeper上注册一个watch，一旦controller宕机，原controller在zookeeper上的ephemeral node 会删除，所有注册watch都会继续参与controller选举


### 九、fail over问题
#### 1.producer角度Kafka fail over
- 如果打开 replica 机制，还取决于 produce 的 request.required.acks 的设置，为了保证数据的接收，会分析参数值为-1的情况，即需要接收到commit才认为消息投递成功
- 如果producer发送完消息，Kafka接收到了，但是还没有来的及发送commit就宕机了，这种时候，producer会认为消息发送失败，开启重发的措施，保证数据不丢失

#### 2.consumer fail over
- consumer和Kafka交互很重要的就是offset的维护，一般这个接收都会采用at least once的语义投递
- 如果Kafka给consumer传递消息，consumer订阅到消息之后，处理了消息，可是还没有来的及commit就宕机了，那么Kafka这边的offset没有更新
- Kafka没有收到consumer的commit，offset没有更新，那等到consumer恢复后，Kafka依旧会把之前位点的数据再传送给consumer

#### 3.zookeeper fail over
- zookeeper负责Kafka集群管理信息的存储，Kafka对zookeeper有强依赖
- 一旦zookeeper因为宕机或者负载过大二无法对外提供服务的时候，Kafka也会跟着无法提供服务
- 如果zookeeper宕机后的session timeout 主要会带来以下问题：
    - Controller fail ,controler会重新进行选举和切换
    - broker fail，导致partition leader的切换或者offline
    - broker 挂起，不对外提供服务
    
#### 4.broker fail over
- broker fail over 分为两个阶段，一个是failure，一个是startup
- 当新加一个broker节点的时候，对原有的partition和replica均不会有影响，它默默的把数据复制过来，然后等着下一次新建topic 开始分partition的时候才可能轮到它
- 当一个节点发生failure，可能是宕机，或者是网络原因，导致不能对外提供服务了，那么在zk地址对应的ephemeral node ，比如/brokers/ids/1，发生 session timeout；
  - 这种情况下，虽然这个节点不响应了，但是处于副本机制，数据不会丢失，并且能够快速切换，不影响用户使用
  - 但是会触发一些问题，比如leader选举，如果是follower宕机，那ISR会变化，如果是普通节点，就不影响，如果是leader就会触发新选举
- 当一个节点startup，原有节点上有数据的partition，可能会加入ISR，进行数据同步复制，如果是没有数据的partition，就等待下一轮数据的分配，将他们offline的状态都改为online
 
 
#### 5.controller fail over
controller的fail over流程：
- 试图去在“/controller” 目录抢占创建 ephemeral node；
- 如果已经有其他的 broker 先创建成功，那么说明新的 controller 已经诞生，更新当前的元数据即可；
- 如果自己创建成功，说明我已经成为新的 controller，下面就要开始做初始化工作，
- 初始化主要就是创建和初始化 partition 和 replicas 的状态机，并对 partitions 和 brokers 的目录的变化设置 watcher。

- 通常情况下，controller不可用会导致leader选举不能正常进行，如果在这个时候leader也宕机需要重新选举，partition会暂时不可用
- 通常，controller dead 会造成broker dead ，导致数据暂时不可用


### 十、Producer消息路由
- producer会根据key和partition的方式进行消息的分发
- 如果该key不存在，会使用round-robin方式获取partition并投递
- 如果key存在，默认会使用key取模的方式分发到指定partition上面，达到消息的均匀投递

### 十一、Consumer 和Consumer Group
- 一个消费组里面可以有多个消费者
- topic可以被多个消费组订阅
- 不同消费组可以消费同一个消息
- 同一个消费组里面的两个消费者，不能同时消费一个partition，只能被消费组里的一个消费者消费
- 消费组内的消费者，如果比partition多的话，定然会有消费者是空余的

### 十二、Offset的保存
- Kafka每次消费需要记录消费者的消费位移，以便消费者下次消费的时候可以从上一个位点向下
- Kafka会根据offset情况定期清理消息，而不是消费完就根据消费情况删除或保留
- 以前Kafka会将offset的信息保存在zookeeper中，但是处于zookeeper数据写入的性能不佳，在0.10之后的版本中，Kafka将自己的offset放在专门的_consumeroffsets topic中，来维护不同consumer的消费位点

### 十三、内部网络框架
内部网络架构处理流水线话，提高吞吐量和性能：
```text
                                                              |---------------------|                                  |---------------------------|
    |-（接收请求，建立连接）--->(Accept Thread)--(Socket转交)--->|  (Process Thread)   |          |---------------|       |                           |
    |                                                         |                     |--------->|(request queue)|-----> |(Work Thread) (Work Thread)|
    |                                                         | Network Thread Pool |          |---------------|       |                           |
( Socket )<-------------------------------------------------->|  (Process Thread)   |          |---------------|       |        Work Thread Pool   |
    |                                                         |        .......      |<-------- |(reponse queue)|<----- |(Work Thread) (Work Thread)|
    | <------------------------------------------------------>|  (Process Thread)   |          |---------------|       |                           |
                                                              |---------------------|                                  |---------------------------|            
                                                              
```
- Accept Thread负责与客户端建立连接链路，然后把Socket轮转交给Process Thread；
- Process Thread负责接收请求和响应数据，Process Thread基于Selector事件循环，首先从Response Queue读取响应数据，向客户端回复响应，然后接收到客户端请求后，读取数据放入Request Queue；
- Work Thread负责业务逻辑、IO磁盘处理等，负责从Request Queue读取请求，并把处理结果放入Response Queue中，待Process Thread发送出去；

### 十四、Rebalance机制
- 在Kafka数据消费的过程中，由于consumer的数量不确定，因为consumer和Kafka partition的对应关系也就不确定
- 当consumer数量小于partition数量，会出现有consumer会消费多个partition
- 当consumer数量等于partition数量，正好一对一消费
- 当consumer数量大于partition数量，就有consumer会空闲
- 增加partition，增加消费者，消费者主动关闭，消费者宕机，协调者宕机
- 出现以上情况的时候，就需要rebalance控制策略，算法如下：
     1. 将目标 topic 下的所有 partirtion 排序，存于PT
     2. 对某 consumer group 下所有 consumer 排序，存于 CG，第 i 个consumer 记为 Ci
     3. N=size(PT)/size(CG)，向上取整
     4. 解除 Ci 对原来分配的 partition 的消费权（i从0开始）
     5. 将第i*N到（i+1）*N-1个 partition 分配给 Ci　
- 目前consumer rebalance的控制策略是由每一个consumer通过Zookeeper完成的。具体的控制方式如下：
    1. 在/consumers/[consumer-group]/下注册id
    2. 设置对/consumers/[consumer-group] 的watcher
    3. 设置对/brokers/ids的watcher
    4. zk下设置watcher的路径节点更改，触发consumer rebalance

### 十五、Kafka自带管理工具
除了在基本篇介绍了关于查看topic\创建topic\删除topic，console consume这些操作，还有一些关于集群节点、分片管理的高级命令
- kafka-replica-verificatiion.sh
   - 验证一个或多个topic下的partition是否都同步
- kafka-reassign-partitions.sh
   - 手动迁移topic 上的partition：bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --topics-to-move-json-file topics-to-move.json --broker-list "5,6" --generate
   - 开始执行迁移：  bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file expand-cluster-reassignment.json --execute
   - 检查迁移状态：  bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file expand-cluster-reassignment.json --verify
   - 选择topic的某个partition的某些replica进行迁徙：
```text
> cat custom-reassignment.json
{"version":1,"partitions":[{"topic":"foo1","partition":0,"replicas":[5,6]},{"topic":"foo2","partition":1,"replicas":[2,3]}]}

> bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file custom-reassignment.json --execute
Current partition replica assignment
```
   - brokers下线
   - 增加replication factor:bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file increase-replication-factor.json --execute
- kafka-preferred-replica-election.sh
   - 手动帮助恢复leader分配的平衡

- Consumer Offset Checker ：显示出consumer group的offset情况， 必须参数为--group， 不指定--topic，默认为所有topic
    - bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --group TEST

- Export Zookeeper Offsets:将Zk中的offset信息以下面的形式打到file里面去
- Update Offsets In Zookeeper:bin/kafka-run-class.sh kafka.tools.UpdateOffsetsInZK earliest config/consumer.properties  page_visits
- 关于zookeeper上的数据也可以直接通过zkCli登陆zookeeper来查看对应目录下的数据


### 十六、Kafka监控工具
1. Kafka Web Conslole 
- 基本的zookeeper信息、broker信息、topic信息、settings配置
- 获取对应日志信息，读取连接不释放，容易出现问题

2. Kafka Manager
- 集群、broker、topic信息
- 操作preferred replica election、reassign partition，实现重分配

3. KafkaOffsetMonitor
- 实时监控集群状态
- Topic、Consumer Group列表
- 展示topic和consumer之间的关系
- 展示consumer的Offset、Lag等

### 十七、Kafka的使用DEMO
kafka 提供了两套 consumer API：
1. The high-level Consumer API
2. The SimpleConsumer API

