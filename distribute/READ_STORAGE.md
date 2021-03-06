> 读《分布式存储》总结

### 一、分布式存储现状
1. 特点：可扩展 低成本 高性能 易用；
2. 挑战： 数据和状态信息的自动迁移，自动容错，并发读写中保持数据一致性；
3. 技术点：数据分布、一致性、容错、负载均衡、事务与并发控制、易用性、压缩/解压缩；

------------------------------------------------------
### 二、分布式存储分类
1. 非结构化数据（文件图片）
2. 结构化数据
3. 半结构化数据（html等）

------------------------------------------------------
### 三、分布式系统分类
1.**分布式文件系统**

分布式文件系统（非结构化数据）：Blob -- binary large Object，二进制大文件 TFS GFS EBS 
一般存储大文件、定长块、Blob对象;

2.**分布式键值系统**

分布式键值系统（存储关系简单的半结构化数据） --- 一致性哈希，是分布式表格的一种简化，一般用于缓存;

3.**分布式表格系统**

分布式表格系统（存储关系相对复杂的半结构化数据）---- 只支持单表操作;

4.**分布式数据库**

分布式数据库系统（由传统单机发展而来）--- Mysql数据库分片，Amazon RDS ,Microsoft Azure;

------------------------------------------------------
### 四、单机存储引擎
1. **单机存储引擎**----是哈希表、B树在机械磁盘、SSD等持久化介质上的实现。

2. **单机存储引擎**：哈希存储引擎、B树存储引擎、LSM（log Structure Merge Tree）存储引擎

------------------------------------------------------
### 五、B树存储引擎
1. **Mysql InnoDB引擎**:B+树（B树存储引擎的一种）：非叶子节点存储索引数据，叶子节点存储完整的数据。

2. **Mysql 缓冲区管理** ---- 缓冲区管理器将可用内存划分成缓冲区，磁盘块内容可以将内容传送到缓冲区，缓冲区是与页同样大小的区域。
  - 一般缓冲区采用LRU策略，最近最少使用，符合条件的被置换，一般效果都比较好
  - 但出现全表扫描的时候，会出现原缓冲区大量数据被置换，从而污染缓冲区
  - 因而现在一般使用LIRS,将数据库分为两级，进入一级满足一定条件才（短时间被访问两次）可以进入二级
  - 每一级内部还是采用LRU算法进行置换，Mysql InnoDB 内部就采用了新子链表（5/8）和老子链表（3/8），先进入老子链表，需要停留超过1S或者一定时间，才能进入新子链表，采用了这种策略防止缓冲区被污染。

------------------------------------------------------

### 六、LSM存储引擎
1. **LSM存储引擎**：例子是Google LevelDB；

2. 包含可变和不可变的MemTable，达到一定条件后，不可变的MemTable转为外部存储的SSTable文件；

3. 写操作先写入可变MenTable, 而后将可变MenTable冻结成不可变的MenTable，然后再生成一个可变的MenTable,LevelDB后台线程将不可变的MenTable存储到磁盘，然后写入到一个SSTable文件，按照主键排序；

4. 合并的操作有两种：
  - 一种Minner合并，是内存中MenTable达到一定值时，将内存数据转储到SSTable中；
  - 一种是Major合并，是一个层级下有多个SSTable, 当数量超过一定值时，会将SSTable向上合并，相当于多路归并；

------------------------------------------------------  
### 七、数据事务
同数据库事务类似、面临的事务问题、事务的隔离级别

------------------------------------------------------
### 八、数据层面的并发控制
数据层面的并发控制主要有：

1.**数据库锁：读锁，写锁，读写锁**
- 读锁可以同时加，写锁只能加一个，读写锁也是在写的时候只能加一个；
- 加锁会造成两个问题，一个消耗性能，一个可能造成死锁；

那么**死锁的解决办法**：

1）设置超时机制，每个事务设置一个超时时间。一个事务超时解锁后就不会影响别的事务继续执行了；

2）设置死锁检测机制，死锁检测主要检查事务之间有没有形成依赖的环路，来避免或者破环环路避免死锁；


2.**写时复制**,对于加锁这种耗时耗性能的操作，可以通过其他灵活的操作实现。

一般读事务使用的数量会大大超过写事务，因而写时复制能够大大减少所带来的消耗，大大提高性能。步骤如下：

1）拷贝：将叶子界面到根节点的路径拷贝出来；

2）修改：对拷贝节点进行修改；

3）提交：原子地切换根结点的指针，使之指向新的根节点；


写时复制涉及引用计数，主要是计算节点被引用的次数，当次数为0 的时候，垃圾数据可以被回收，对于读操作，可以提高性能，但是写操作的成本较高，并且写操作是互斥的。

3.**多版本并发控制MVCC**
- MVCC是对每一行数据维护多个版本号，事务的执行关乎对应的版本号，因而不用加锁就能够读取正确的数据。
- Mysql InnodeDB引擎每一行数据就维护了数据修改版本号和删除版本号，这个版本号是相对于数据库而言的一个递增整数，与时间戳之类的无关，每次查询都会将事务号与版本号对比；
    - 对于SELECT就比较事务号需要大于等于修改版本号，并且删除版本号没有定义；
    - 对于INSERT就更新修改版本号为事务号；
    - 对于DELETE就更新删除版本号；
    - 对于UPDATE就更新修改版本号；

------------------------------------------------------
### 九、故障恢复
1. 对于分布式存储机制一般采用操作日志方式进行故障恢复；
2. 操作日志分为回滚日志（UNDO log）、重做日志(REDO log)、UNDO/REDO日志；
3. 先将日志随机保存在内存数据块中，在通过定期顺序刷到磁盘上进行持久化；
4. 一般先写操作日志，才操作内存中数据，因为日志是分布式存储最为关键的，绝对要保证正确性和一致性；

故障恢复的策略有多种，关于优化:
1. **成组提交**:REDO日志首先写入到日志缓冲区，当缓冲区大小到达一定值或者刷写时间间隔达到一定值，将日志缓冲区一下子刷写到磁盘，再一次性修改内存中数据，牺牲了写事务的延时却提高了吞吐量;

2. **设置检查点**:在日志过大内存不够的情况下，故障恢复后的日志重做会耗费大量时间，检查点这种定期转储易于回放重做的方式，只需要执行最新一个检查点之后的REDO日志，就可以保证数据的一致性了，无需全部重做;

步骤如下:

1）日志中记录START CKPT;

2）形成易于加载的checkpoint文件;

3）记录END CKPT;
  
  (1)恢复主要是将checkpoint索引数据加载到内存，然后将START - END CKPT之间的操作重做;
  
  (2)对于非幂等性操作，需要在记录点前对数据进行快照存储为checkpoint文件，不包含非幂等性操作，记录此时REDO日志回放点，就不通过命令START- END CKPT方式来记录检查点了。


------------------------------------------------------
### 十、数据压缩算法
1. 哈夫曼编码----前缀编码;

2. LZ系列压缩算法----基于字典的压缩算法，（LZW,GZIP）找出单词对应字典中的页码和位置即可 ，压缩率比较高;

3. BMDiff 和Zippy----基于Google的Bigtable,属于LZ系列，压缩率不大但是效率很高;

4. 列式存储----能够很好的压缩数据，因为一般列里面的值重复度很高，比如性别、籍贯等，OLAP大数据查询效率高，也支持列组(行列混合存储模式)，这种模式能够同时满足OLAP,OLTP;


------------------------------------------------------

### 十一、分布式可能遇到的问题
分布式系统很大情况下就是为了避免因为数据的存储不当，而出现一致性、持久性等问题。

**分布式遇到可能的一些问题：**

1.**异常**：因为一些外部不可抗因素，使得分布式自动容错的机制出现问题：
比如：服务器宕机，网络异常，磁盘故障；

2.**超时**
- 在分布式存储中，数据的读写需要一个一致性协调同步工作；
- 这些工作可能依靠RPC来完成，那因为网络原因、节点自身原因，可能出现：
1）数据同步或者选举操作超时；
2）可能操作成功，最终客户端并没有收到确认信息，这种状态不能简单的认为任务操作失败，所以需要维护一个幂等的操作，例如循环检验上一步操作的最终执行状态记录来判断，一直执行这个幂等操作来查看客户端的返回和操作的状态情况。
- RPC执行的三种状态有：成功、失败、超时；

3.**一致性问题**：虽然分布式得到了水平扩展提高性能的能力，但是一份数据的丢失会影响整个数据存储，因而分布式存储提出了副本的概念很好的解决了容错的问题，副本既可以提供读的能力，也可以通过一致性协调算法同步写的数据，但是这个多副本的一致性又成了大难题；


------------------------------------------------------
### 十二、一致性问题
**一致性的分类**：分为弱一致性、强一致性和最终一致性，用的最多的就是最终一致性这个概念；
1. **强一致性**：每一个写操作必须立即生效，保证最新的读取操作读取到最新的数据；
2. **弱一致性**：一个写操作的执行，并不能保证并发相关的读操作可以读取到最新的数据；
3. **最终一致性**：是弱一致性的一种特例，就是当写操作执行同时的读操作并不一定可以读取到最新的值，存在一个不一致性窗口，在这个窗口外能够保证读取到最新值，这个窗口的大小与交互延迟、系统负载、复制协议要求同步的副本数等相关

最终一致性还有以下几个方面：
- **读写一致性**:某一个客户端做出的写操作下能保证其读操作的一致性，其他客户端会有窗口延迟；
- **会话一致性**：要求客户端和存储系统交互的整个会话期间保证读写一致性，其他不保证；
- **单调读一致性**：某一客户端如果已经读取到最新值，就不能读到旧值；
- **单调写一致性**：某一客户端的写操作是顺序的，那么存储系统多个副本的同步操作也是按照同样顺序进行的；

一致性顺序的说明：
- **副本一致性**：多副本之间数据应当是一致的，或者有一个不一致的窗口;
- **更新顺序一致性**：存储副本的同步操作的顺序是一致的


------------------------------------------------------
### 十三、分布式系统的衡量标准
1. **性能**：
    系统的吞吐能力，系统的响应时间（某一时间段请求总数，每秒读操作数，每秒写操作数，响应延迟）;
    
2. **可用性**：
    面对各种异常的快速恢复并对外提供服务的能力，衡量的标准是暂停服务到恢复的时间;

3. **一致性**：
    在强一致性的前提之下，对性能和可用性造成的影响;
    
4. **可扩展性**：
    系统通过扩大集群规模能够提升性能、可用性的能力;
    
------------------------------------------------------

### 十四、分布式系统的数据分布
1.数据分布：分布式系统需要依靠分布式的数据存储来实现可扩展性

2.数据分布有两种模式：一个是随机分布（哈希分布），可采取一致性哈希算法；一种是顺序分布，表格内数据按照主键整体有序;

3.**哈希分布**:依赖哈希算法散列的性质，能够将数据进行均匀地分布而充分发挥分布式的特性，哈希分布在主键递增的情况下就会发生数据倾斜的问题，就需要手动拆分或者自动拆分的模式，将大任务进行拆分。

4.传统哈希分布还存在一个问题，当服务器上线下线，N值发生变化时（意思是增加或删除节点），就会出现全部数据迁移，会有很大性能的损耗，因而采用一致性哈希，即每一个节点随机分配一个token,并不再变化，与对应节点绑定，确定一个哈希环，当新节点加入只需要移动相邻或者上一个环中的节点进行数据迁移即可，无需改变所有节点的数据分布。虽然避免了数据迁移，但是久而久之数据的分布就会不均匀，后面会提到虚拟节点技术来避免这种不均匀。
  - 每个服务器维护哈希环中的位置的算法有几种：
     - 维护向前一个和向后一个的节点位置信息，空间复杂度O(1)，时间复杂度O（N）；
     - 每个节点维护一个大小为N的路由表，时间复杂度和空间复杂度均为O（lgN）；
     - 维护所有节点的位置信息，空间换时间，空间复杂度O（N），时间复杂度O（1）；
     
5.哈希分布是比较普遍，但是不能支持顺序扫描是一个很困扰的事情，因而只能在应用端进行操作，按照用户做数据拆分，对单一节点支持数据的顺序读取。

6.**顺序读取**:最原始的就是顺序分布，比如垂直分表，将关系型表按照主键进行垂直分表，需要meta表维护数据区间和数据位置对应的关系，对于数据的增加还需要考虑数据的分裂与合并的问题，比较复杂。

7.负载均衡：分布式架构非常讲求节点的分布式协调合作，需要依赖负载均衡，这个负载均衡是依靠节点之间发送心跳包（包含CPU,性能，内存，磁盘等系统信息）到主控节点，主控节点来控制数据的迁移，主要通过主副本提供服务，备副本复制信息并传送替换的方式来实现。


------------------------------------------------------
### 十五、分布式系统的复制算法
1. 复制算法的分类：**强同步复制和异步复制**
2. 分布式存储依靠副本进行数据迁移中很重要的环节是复制，分为强同步复制和异步复制，区别就是复制操作完成是否需要返回成功；
3. 这两种复制方式一个侧重一致性一个侧重可用性，强同步复制考虑一致性，各个节点之间数据能保持强一致性，但是当同步过程出现问题，可用性就受到影响。异步复制的可用性有较好保障，可是一致性就受到威胁；

- 强同步复制流程：
1. 写请求到主副本；
2. 主副本将操作日志同步给备副本；
3. 备副本同步完毕；
4. 主副本告知客户端写成功；
5. 客户端读请求；
6. 客户端读返回；

这个操作具有强一致性，但是牺牲了可用性。强同步复制和异步复制都是基于主副本的复制协议，对主节点的依赖较大，主节点一旦宕机，就要依靠一致性选举算法（paxos等）实现选举。也有基于多存储节点的复制协议，但是不常用。

------------------------------------------------------
### 十六、分布式事务
参看READ_TRANSACTION.md


------------------------------------------------------
### 十七、分布式协调一致性算法
分布式协议主要有：
1. 租约协议
2. paxos(协议用于多节点选举或某个投票达成一致性)
3. 一致性协议
4. 两阶段提交协议（用于节点间的操作能够原子性执行）
5. 复制协议

这边主要介绍Paxos---分布式一致性算法的鼻祖，解决分布式节点选举或提议的一致性问题。
- Paxos协议主要分为两个阶段：
1. 批准阶段：
提议者向其他节点发送accept信息，其他节点可以选择同意或不同意；

2. 确认阶段：
超过半数以上的节点赞同提议，提议就将被执行，提议者就发送acknowledge确认信息给所有节点；

- 完整请求流程：
1. 准备阶段：
提议者选择某一个提议号向其他acceptor节点发送准备消息，accpetor接收到提议号后，如果提议号大于他所有回复的准备消息，accpetor就将上次接受的提议回复给提议者，并承诺不再介绍小于当前提议号的提议；

2. 批准阶段：
提议者在接收到acceptor中多数派对准备的回应之后，就进入批准阶段。如果accpetor在准备阶段回复了上一次提议的回复，那提议者就将最大序号的提议值发放下去批准，否则发放一个新的提议值给acceptor批准。Acceptor在不违背准备阶段承诺的前提下，接受这个请求；

3. 确认阶段：
超过半数以上的acceptor同意提议，提议者就发送acknowledge确认信息给所有acceptor，通知他们提议值生效；

- Paxos协议考虑的两个问题，一个是正确性，只有一个提议值最终会生效，一个是可终止性，即最终肯定有一个提议会生效；

- Paxos协议有两种使用方式，一种是实现全局的锁、命名和各种配置，类似于zookeeper，google chubby；一种是将用户数据复制到多个数据中心，例如Google Megastore /Google Spanner；


------------------------------------------------------
### 十八、数据集群集中部署方案
数据集群跨机房部署的集中方案：
1. 集群整体切换：
两个物理环境下有两个一模一样的集群，通过复制协议（强同步复制/异步复制），进行数据同步，当切换的时候可以自动/手动进行数据切换；

2. 单个集群跨机房：
这个主要就是主副本A1,B1在机房1，主副本C1在机房2，让数据备用副本和主副本能够均匀地分布在不同机房，当切换的时候能够灵活的使用，这边需要一个能够与两个机房都通信的主控节点；

3. Paxos选主副本：以上方法总控节点与工作节点必须实现租约，就是一旦有故障，会自动切换到另一套。
- 对于以上方法，基本主副本和备副本
    - 都存在，主副本宕机，备副本自然替代；
    - 一种就是A1,A2,,A3,A4谁是主副本依靠Paxos协议产生，B1,B2,B3,B4也依靠选举产生主副本，因而最初是状态你不能知道主副本的分布状态，数据的复制同步也依靠Paxos协议；
    

------------------------------------------------------
### 十九、分布式系统和集中式系统的对比
分布式系统的优缺点：
- 分布式的优点：
    - 通过主备的副本，大大提高了服务的可用性，大大减少了单点故障可能带来的问题；
    - 通过负载均衡，也能够发挥副本并发读的特点，大大提高了性能；
    
- 分布式的缺点：
    - 由于主备副本的原因，出现了数据多节点一致性的问题；
    - 由于复制协议的强同步复制和异步复制，也带来了强一致性、弱一致性和最终一致性的选择问题；

分布式系统相对于集中式系统，有几个特点：
1. 分布性：数据和服务节点的分布；
2. 对等性：没有全局的主机和从机，只有主副本和备副本的概念，分布在不同的主机上，统一对外提供服务；
3. 并发性：因为副本的加入，分布式系统相对于集中式，更好地支持并发操作；
4. 缺乏全局时钟：因为分布式系统依靠消息系统在多服务节点之间交互，所以没有一个统一的时钟来维护事件发生的时钟序列；
5. 故障总会发生：会出现节点故障（服务器宕机或僵死状态），网络分区（俗称脑裂，部分节点无法加入集群进行通信），通信异常（超时或硬件相关）、三态（成功、失败、超时）；
