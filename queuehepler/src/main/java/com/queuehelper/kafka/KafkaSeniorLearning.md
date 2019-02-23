## 一、Kafka + 分布式事务
- Kafka 在 0.11.0.0 之前的版本中只支持At Least Once和At Most Once语义，尚不支持Exactly Once语义
- 从0.11开始终于支持了，提供了分布式事务的能力
- 保证了数据的一致性，实现了exactly once语义，只传一次不丢不重，支持操作的原子性，支持有状态操作的可恢复性
- 在Kafka没有支持事务之前，我们可以使用at least once 加上consumer端的幂等性操作或者缓存机制来应对消息的重复问题


### 1.事务的场景
在消息中间件中实现事务是大家都盼望的事情，应用的分散，事务操作的交互就需要依赖中间件来支持事务操作。
- producer向topic和partition发送消息的动作，有时需要存放在事务中，需要这些消息的投递都成功或者都失败
- producer发送的多个消息需要构成事务，对consumer都可见或者不可见
- consumer-transform-produce场景，涉及消息位点的提交
- 一个producer的宕机或不可用，能够是替代的producer能够还原上一个producer的事务消息状态


### 2.关键概念
- 为了支持事务，引入以下概念：

1.事务协调者：

    类似于消费组负载均衡的协调者，每一个实现事务的生产端都被分配到一个事务协调者(Transaction Coordinator)。

2.引入一个内部Kafka Topic作为事务Log：

    类似于消费管理Offset的Topic，事务Topic本身也是持久化的，日志信息记录事务状态信息，由事务协调者写入。

3.引入控制消息(Control Messages)：
- 这些消息是客户端产生的并写入到主题的特殊消息，但对于使用者来说不可见。
- 它们是用来让broker告知消费者之前拉取的消息是否被原子性提交。

4.引入TransactionId：

    不同生产实例使用同一个TransactionId表示是同一个事务，可以跨Session的数据幂等发送。
    当具有相同Transaction ID的新的Producer实例被创建且工作时，旧的且拥有相同Transaction ID的Producer将不再工作，避免事务僵死。

5.Producer ID：

    每个新的Producer在初始化的时候会被分配一个唯一的PID，这个PID对用户是不可见的。
    主要是为提供幂等性时引入的。

6.Sequence Numbler

    对于每个PID，该Producer发送数据的每个<Topic, Partition>都对应一个从0开始单调递增的Sequence Number

7.每个生产者增加一个epoch：

    用于标识同一个事务Id在一次事务中的epoch，每次初始化事务时会递增，从而让服务端可以知道生产者请求是否旧的请求

8.幂等性：

    保证发送单个分区的消息只会发送一次，不会出现重复消息
    增加一个幂等性的开关enable.idempotence，可以独立与事务使用，即可以只开启幂等但不开启事务
    
### 3.操作的原子性
    指操作要么成功要么失败，有助于提升数据的一致性，便于故障恢复

### 4.幂等性发送
- Exactly Once是一种让下游系统具有幂等处理特性
- 为了实现Producer 的幂等语义，Kafka 引入了Producer ID（即PID）和Sequence Number。每个新的 Producer 在初始化的时候会被分配一个唯一的 PID，该 PID 对用户完全透明而不会暴露给用户
- 对于每个 PID，该 Producer 发送数据的每个<Topic, Partition>都对应一个从 0 开始单调递增的Sequence Number
- broker维护了一个序号，记录<PID,Topic, Partition>,每次有commit就会将这个序号递增
- 在接收消息的过程中，会对消息PID与自身记录序号中的PID进行对比：
    - 如果消息PID比记录PID大一，接收
    - 如果消息PID比记录PID大几，证明当前broker的消息有丢失，则拒绝，Producer 抛出InvalidSequenceNumber
    - 如果消息PID比记录PID小，证明当前消息是滞后的或者重复的，则拒绝，Producer 抛出DuplicateSequenceNumber
- 以上消息接收的流程能够解决之前有提到的两种问题：
    - Broker 保存消息后，发送 ACK commit 之前宕机，Producer 认为消息发送失败并重试，造成数据重复
    - 前一条消息发送失败，后一条消息发送成功，前一条消息重试后成功，造成数据乱序
    
### 5.事务性保证
当前问题：
- 上述幂等设计只能保证单个 Producer 对于同一个<Topic, Partition>的Exactly Once语义
- 它并不能保证读操作和写操作的原子性
- 事务恢复需要有状态的应用也可以保证重启后从断点处继续处理

应对方案：
- 添加新字段
    - 应用程序必须提供一个稳定的（重启后不变）唯一的 ID，也即Transaction ID
    - Transaction ID 与PID一一对应
    - Producer 通过Transaction ID拿到 PID 的同时，还会获取一个单调递增的 epoch
- 拥有Transaction ID之后
    - 保证跨 Session 的数据幂等发送，原producer宕机，添加新producer的场景：即使有相同的Transaction ID，由于epoch的不同，新旧Producer不会重复处理数据
    - 保证跨 Session 的事务恢复，某个应用宕机：保证要么提交的所有事务都commit 或者这都abort，从新启动的事务开始重新工作
- consumer端由于消费的特殊性，不能保证事务性（https://www.infoq.cn/article/kafka-analysis-part-8）
    

### 6.事务性消息的传递
- Kafka 0.11.0.0 引入了一个服务器端的模块，名为Transaction Coordinator 事务协调者，用于管理 Producer 发送消息的事务性
- Transaction Coordinator维护Transaction Log，log 存于一个内部的 Topic 内，由于 Topic 数据具有持久性，因此事务状态也具有持久性
- Transaction Log的设计与offset log的设计类似
- Producer 并不直接读写Transaction Log，它与Transaction Coordinator通信，然后由Transaction Coordinator将该事务的状态插入相应的Transaction Log

### 7.事务中 Offset 的提交
- 为了实现该场景下的事务的原子性，Kafka 需要保证对 Consumer Offset 的 Commit 与 Producer 对发送消息的 Commit 包含在同一个事务中
- 否则，如果在二者 Commit 中间发生异常，根据二者 Commit 的顺序可能会造成数据丢失和数据重复
    - 如果先 Commit Producer 发送数据的事务再 Commit Consumer 的 Offset，即At Least Once语义，可能造成数据重复。
    - 如果先 Commit Consumer 的 Offset，再 Commit Producer 数据发送事务，即At Most Once语义，可能造成数据丢失。
    
### 8.用于事务特性的控制型消息
- 为了区分事务是commit还是abort ，提出了Control Message的概念
- 只用于 Broker 与 Client 间的内部通信，应用程序是不知道的
- 对于 Producer 端事务，Kafka 以 Control Message 的形式引入一系列的Transaction Marker
- 通过对消息的判断，并结合隔离级别决定是否将消息返回

### 9.完整事务过程
1.producer首先向任意一个brokers里面找到事务协调者（Transaction Coordinator），因为事务协调者是分配PID和管理事务的核心
- 这个动作的前提条件是需要应用程序开启事务特性、幂等特性，配置的时候需要带上唯一的Transaction ID，enable.idempotence设置为 true
 
2.producer向已经找到的事务协调者发送获取PID的请求
- 这个动作需要producer发送InitPidRequest
- InitPidRequest,只要开启了幂等特性即必须执行
- InitPidRequest具体流程
    - InitPidRequest发送到事务协调者
    - 如果是第一次请求，事务协调者会在Transaction log中记录<TransactionID,PID>
    - 经过持久化之后，PID就和TID进行了绑定
    - 在这个阶段，PID对应的epoch也会增加，这个epoch用于判定这个producer是否是最新的，旧的producer请求会被拒绝
    - 当处于恢复阶段，发送这个请求的同时，还会做与这个TID PID相关的事务状态的恢复
    - 这个init操作时同步阻塞的
    - 因为这个init操作只和幂等的设置有关，以上是开启事务的流程，如果不开启事务，这个请求将发给任意一个broker来处理，并且返回一个新的PID，只能处理单一会话当中的幂等和事务特性了

3.开启事务
- beginTransaction标识事务的开始，但是事务协调者只有当producer开始发消息，才认为事务的开始

4.Consume-Transform-Produce 几个重要的方法：
   - AddPartitionsToTxnRequest
        - 一个producer给多个topic发送数据，如果topic是新的，就先向事务协调者发送AddPartitionsToTxnRequest
        - 事务协调者会将<Transaction,Topic ,Partition>记录下来，存在放日志里面。并将事务的状态改为BEGIN
        - 如果topic是新的，该消息是第一条消息，事务协调者就会给当前事务开始计时
      
   -  ProduceRequest：除了应用数据外，该请求还包含了 PID，epoch，和Sequence Number
   - AddOffsetsToTxnRequest
        - sendOffsetsToTransaction 会将当前消费到不同record的offset，topic ，partition放在Map中发送
        - 这个方法里面会对GroupID判断，如果是同一个组，可以直接发送，如果不是同一个组，则标识一个新事务的开启
   
   - TxnOffsetCommitRequest
        - 作为sendOffsetsToTransaction方法的一部分，在处理完AddOffsetsToTxnRequest后，Producer 也会发送TxnOffsetCommit请求给Consumer Coordinator
        - 事务协调者会将本事务包含的与读操作相关的各<Topic, Partition>的 Offset 持久化到内部的__consumer_offsets
        - Consumer Coordinator 会校验PID 和epoch ，始终保证消息来自最新producer
        - 写入__consumer_offsets的 Offset 信息在当前事务 Commit 前对外是不可见的
        - Consumer Coordinator并不会立即更新缓存中相应<Topic, Partition>的 Offset

   - Commit 或 Abort 事务
     - commitTransaction 提交事务
     - abortTransaction 遇到异常回滚事务

   - EndTxnRequest
     - 无论是提交还是回滚，producer都会发送EndTxnRequest给事务协调者，需要标志事务的状态，是提交或是回滚
     - 事务协调者收到请求后，会将PREPARE_COMMIT或PREPARE_ABORT消息写入Transaction Log
     - 通过WriteTxnMarker请求以Transaction Marker的形式将COMMIT或ABORT信息写入用户数据日志以及Offset Log中
     - 最后将COMPLETE_COMMIT或COMPLETE_ABORT信息写入Transaction Log中
     - 对于commitTransaction方法，它会在发送EndTxnRequest之前先调用 flush 方法以确保所有发送出去的数据都得到相应的 ACK。对于abortTransaction方法，在发送EndTxnRequest之前直接将当前 Buffer 中的事务性消息（如果有）全部丢弃，但必须等待所有被发送但尚未收到 ACK 的消息发送完成
     - 只有当事务为commit状态，下游消费者才可见，如果是abort，直接将写入数据的状态改为abort，如果下游接收的规则是read-commit则无法获取到这些消息，也不可见

   -  WriteTxnMarkerRequest：收到Transaction Marker该请求后，对应的 Leader 会将对应的COMMIT(PID)或者ABORT(PID)控制信息写入日志

   -  最终的COMPLETE_COMMIT或COMPLETE_ABORT消息
      - 在Transaction Marker写完了所有用户日志后，事务协调者会在自己的transaction log中写入日志，标志事务的结束
      - 事务结束，在事务协调者对这部分数据的引用就移除，不会再被加入判断
      - COMPLETE_COMMIT或COMPLETE_ABORT的写入并不需要所有副本的确认，副本的维护靠自身的复制同步算法保证  

### 10.事务处理样例代码
    见TestKafkaTransaction

### 11.事务相关配置
#### 1.Broker configs
1. transactional.id.timeout.ms：事务协调器在生产者TransactionalId提前过期之前的最长等待时间，并且没有生产者关于TransactionalId的任何事务状态更新

    默认是604800000(7天)

2. max.transaction.timeout.ms：事务最大超时时间

    如果客户端请求的事务时间超过此时间，broke将在InitPidRequest中返回InvalidTransactionTimeout错误

    默认值为900000(15分钟)

    这可以防止客户端请求超时，从而导致用户无法从相关主题读取数据

3. transaction.state.log.replication.factor：事务状态topic的副本数量，默认值:3

4. transaction.state.log.num.partitions：事务状态主题的分区数，默认值:50

5. transaction.state.log.min.isr：事务状态主题的每个分区ISR最小数量，默认值:2

6. transaction.state.log.segment.bytes：事务状态主题的segment大小，默认值:104857600字节

#### 2.Producer configs
1. enable.idempotence：开启幂等

2. transaction.timeout.ms：事务协调器在主动中止正在进行的事务之前等待生产者更新事务状态的最长时间。

    这个配置值将与InitPidRequest一起发送到事务协调器。

    如果该值大于max.transaction.timeout，在broke中设置ms时，请求将失败，并出现InvalidTransactionTimeout错误

    默认是60000，一分钟

3. transactional.id：唯一标识TransactionalId

    它允许客户端确保使用相同TransactionalId的事务在启动任何新事务之前已经完成

    如果没有提供TransactionalId，则生产者仅限于幂等交付

#### 3.Consumer configs
1. isolation.level

    read_uncommitted:以偏移顺序使用已提交和未提交的消息，可能出现脏读、虚读等问题

    read_committed:仅以偏移量顺序使用非事务性消息或已提交事务性消息。能够看到确定的数据，避免脏读、虚读等问题
    

### 12.事务性能与优化
- producer端主要减少写入的延迟，每一次写入都会有大量的producer、事务协调者、broker多方的消息传送，为了减少这些开销，关键是每个事务包含更多的消息

- consumer端过滤掉重值的事务，在consumer这边配置read-commit，一般consumer不会有性能太大影响


### 13.Exception 处理
具体的Exception可以在编码的过程中自行体会，这边罗列几个：
- InvalidProducerEpoch ： Fatal Error，它说明当前 Producer 是一个过期的实例

- InvalidPidMapping :Transaction Coordinator没有与该Transaction ID对应的 PID。此时 Producer 会通过包含有Transaction ID的InitPidRequest请求创建一个新的 PID

- NotCorrdinatorForGTransactionalId：该Transaction Coordinator不负责该当前事务。Producer 会通过FindCoordinatorRequest请求重新寻找对应的Transaction Coordinator

- InvalidTxnRequest

- CoordinatorNotAvailable ： 协调者还未准备就绪，可以重试

- DuplicateSequenceNumber ：该异常说明该消息已经被成功处理过，可以忽略该消息

- InvalidSequenceNumber：说明发送的消息中的序号大于 Broker 预期，可能是数据乱序，或者服务器由于日志被 Truncate 而造成数据丢失

- InvalidTransactionTimeout：Producer 传入的 timeout 时间不在可接受范围内，已超时



### 14.处理Transaction Coordinator失败
事务协调者的异常，需要重新选举新事务协调者
  - 发生在PREPARE_COMMIT/PREPARE_ABORT之前
      - 此时transaction log 记录完整的事务过程和状态，切换新的事务协调者可以继续处理事务
      
  - 发生在PREPARE_COMMIT/PREPARE_ABORT之后
      - 此时transaction marker已经发出日志信息，broker节点分别在写用户日志，这个状态不可控
      - 如果新的事务协调者出现，会把调用transaction marker写用户日志的操作重做，如果在此期间没有新的事务提交/取消，用户日志侧只会有结果信息的重复提交，不影响事务的执行，不然就会造成日志信息混乱
      
  - 发生在COMPLETE_COMMIT/COMPLETE_ABORT之后
       - 在结束事务之后，transaction log 已经完整记录了事务的提交状态，新的事务协调者可以还原事务状态继续处理，不影响
       - 新事务协调者会给producer返回成功
        
### 15.事务过期机制
- 事务过期机制主要依靠参数控制：transaction.timeout.ms
- 终止事务：在客户端失败的情况下，通过超时机制，事务协调器能够主动终止一些事务
    - 能够避免过多死事务状态的管理
    - 能够避免过多死事务日志数据的存储
    - 尽早将死事务的数据清除，能够让consumer的缓存不至于OOM
    - 如果多个Transaction ID不同的 Producer 交叉写同一个 Partition，当一个 Producer 的事务状态不更新时，READ_COMMITTED的 Consumer 为了保证顺序消费而被阻塞
- 主动通过超时将事务终止
    - 如果状态为begin，就通过PID的epoch ,并且写入事务日志，，用新的epoch回滚事务，那死的客户端的请求就会被拒绝
    - 如果状态为precommit ，就执行之后的操作，交给transaction marker 执行用户日志，并commit写入transaction log
    - 如果是preabort，就执行后续abort操作
- 终止Transaction ID
    - 某Transaction ID的 Producer 可能很长时间不再发送数据，Transaction Coordinator没必要再保存该Transaction ID与PID等的映射，造成资源浪费
    - transactional.id.expiration.ms（默认值是 7 天），事务协调者周期性遍历内存中的Transaction ID与PID映射，将无响应的事务从内存中删除，并在Transaction Log中将其对应的日志的值设置为 null，使 Log Compact 可将其记录删除
  

### 16.PostgreSQL MVCC
对于事务的操作，一方面是加锁，阻塞请求，消耗较大，一方面就是通过多版本控制进行灵活的请求，没有锁的消耗，但是需要维护多版本的记号
PostgreSQL中就是使用多版本控制来解决事务的问题：http://www.jasongj.com/sql/mvcc/

### 17.两阶段提交
http://www.jasongj.com/big_data/two_phase_commit/#%E4%B8%A4%E9%98%B6%E6%AE%B5%E6%8F%90%E4%BA%A4%E5%8E%9F%E7%90%86
- 两阶段提交是应对分布式事务的解决方案，能够保证数据的一致性，分为请求阶段、提交阶段
- 除了两阶段提交还是有三阶段提交，增加了预提交阶段
- 除此以外还有，补偿事务，消息事务等

Kafka的事务机制与传统两阶段提交机制有很大不同：
- 在Kafka中二阶段提交中，请求阶段是指PREPARE_COMMIT/PREPARE_ABORT，并且只须在Transaction Log中标记即可
- Kafka 事务中，发起PREPARE_COMMIT或PREPARE_ABORT后，则确定该事务最终的结果应该是被COMMIT或ABORT。而分布式事务中，请求阶段之后由各参与者返回状态，只有所有参与者均返回Prepared状态才会真正执行 COMMIT，否则执行 ROLLBACK；
- Kafka中，某几个partition在commit/abort阶段不可用，不会影响其他partition，这个和传统两阶段提交，需要所有的参与者全部回应，任何一个参与者宕机都会影响最终执行不同
- Kafka的事务超时机制，能够避免挂起的事务对其他事务的影响
- Kafka中可以存在多个事务协调者，二两阶段提交当中提到只能有一个事务管理器


### 18.Zookeeper
Zookeeper 的原子广播协议与两阶段提交与Kafka 事务机制相似
- Kafka事务由commit/abort两种情况，zookeeper原子广播协议只有commit
- Kafka存在多个事务协调者，而zookeeper的写入只能在leader上
- Kafka事务的commit/abort来自客户端，而zookeeper当中的提议结果来自半数以上节点的ack




