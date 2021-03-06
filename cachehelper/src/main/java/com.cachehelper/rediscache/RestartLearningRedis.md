> [一个很完整的中文学习网站](http://redisdoc.com/topic/index.html)

### 一、redis是什么
- redis是一个开源，基于内存结构化数据存储媒介，可以作为数据库、缓存服务、消息服务
- 支持多种数据结构包括，字符串，哈希表，链表，集合，有序集合，位图等
- 具有LRU淘汰，事务实现，不同级别的持久化能力，并且支持副本集和通过哨兵模式实现高可用，集群模式实现数据自动分片的能力
- 基于单线程，使用一个线程处理所有客户端的请求，采用非阻塞IO-NIO，精细优化各种命令的算法时间复杂度
- redis是线程安全的，操作原子化，不会因为并发的原因产生数据异常
- redis速度很快，使用非阻塞IO ，并且大部分算法的时间复杂度都是O（1）
- keys命令，获取一个缓存对象中的所有key,这个时间复杂度是0（n）,相对是高耗能的操作，慎用

### 二、redis为什么这么快
- 底层基于netty，使用nio多路复用技术，实现了单线程能够最大化地充分利用管理多路请求的功能,依靠零拷贝实现快速读写
- 单线程操作避免上下文切换，c语言操作更贴近操作系统
- 一个是性能，一个是并发
- 性能主要是由于服务端拼接的sql，通过数据库执行，会带来数据库连接池，网络传输能消耗
- 并发主要是当服务端请求量巨大的时候，如果不经内存的缓冲，将大量并发作用在数据库，可能会给数据库带来灾难性的问题


### 三、redis的特点
- 单线程，读写快

### 四、redis的数据结构
- 数据结构有哪些，哪些是热门的，可以用在哪些场景中，以下一一解读：

#### 1.key
- key-value是redis最基本的数据结构，任何二进制序列都可以作为redis的key使用
- 不要使用过长的key,会消耗很多内存，查找效率也会降低
- key短但是不要缺失可读性，虽然节省了空间，但是会造成可读性和可维护性上的问题
- 最好使用统一的规范在定义设计key,带上namespace,注意redis中使用冒号来作为namespace的分割，存入的key字段中不允许带有冒号，这个是必须遵守的规定，没有办法通过什么捷径能够避免
- redis允许的最大key长度是512M，value也是512M


#### 2.String
- redis中没有int double等基本数据类型，存储到redis的数据都是String类型
```text
SET 为一个key设值
EX/PX 给字段设置有效期
NX/XX参数针对key是否存在的情况进行区别，时间复杂度0（1）
GET获取某一个key对应的value，时间复杂度0（1）
GETSET 为一个key设置value，并返回该key的原value，时间复杂度O（1）
MSET 为多个key设置value，时间复杂度O（n）
MSETNX 同MSET ，如果指定的key中有任意一个存在，则不进行任何操作，时间复杂度O（n）
MGET 获取多个key对应的value,时间复杂度O（n）
```

- 虽然redis中的数据类型只有String,但是Redis可以把String作为整型或者浮点型数字来使用，主要体现在自动递增，自动递减等操作上
```text
INCR：将key对应的value递增1，并返回递增之后的值，这其实利用了String到整型的转换，如果String表达的数是超过整型的范围的，是不可以递增的，时间复杂度O（1）
INCRBY：可以将key按照制定步长进行递增，并返回递增后的值，同样这个转换不能超过整型的表示范围，时间复杂度是O（1）
DECR/DECRBY: 同INCR/INCRBY，自增的情况改为自减
```
- INCR/DRCR 要求对应的String value值可以转换为64位的带符号的整型数字，否则报错

- 以上命令的操作场景可以有

1.库存控制：并发操作，对数据的操作往往容易出错，一般情况下会进行加锁操作，执行事务，这样的操作是消耗性能的，但是redis的单线程处理这种库存的操作不会出现并发资源竞争的问题
```text
库存扣减+余量控制
SET inv:remain "100"
DECR inv:remain
```

2.自增序列生成
```text
类似生成RDBMS 序列的功能，能够从redis拿到一段连续的ID，供本地使用，可以批量的获取，能够减轻请求的压力
SET sequence "10000"
INCR sequence
INCRBY sequence 100
能够保证全局是唯一的
```


#### 3.List
- redis的list是链表型的数据结构，可以说使用LPUSH RPUSH LPOP RPOP等命令在list的两端进行操作，是一个双端队列，支持快速在双端进行操作，对于指定点获取数据的时间复杂度0（n）
```text
LPUSH  时间复杂度O（n） 左侧头部
RPUSH 右侧头部
LPOP  左侧头部删除
RPOP  右侧头部删除
LPUSHX/RPUSHX 用于事务，如果key不存在就不会操作
LLEN 长度
LRANGE 返回指定范围内的数据
```
- 因为双端队列对指定index的查询和获取会消耗时间
```text
LINDEX ，返回指定index的数据 O(n)
LSET，给指定index设置数据  O(n)
LINSERT  在指定范围插入数据 O(n)
```
- List的设计主要是为了实现队列的特性，而不是ArrayList特性
- 由于扩展队列的特性，还提供了阻塞的队列BLPOP BRPOP,提供了类似的BlockingQueue的能力，list为空就阻塞连接，直到有对象才返回


#### 4.Hash
- 类似就是HashMap
- field -Value类型
- 可以实现二元查找
- 比起将整个对象序列化后转为String存储，Hash能够很好地减少网络传输带来的消耗
- hash维护一个集合，比起list能够很好地支持随机访问
```text
命令：
HSET 为field设置value O(1)
HGET 获取制定field的值  O(1)
HMSET/HMGET 可以操作同一key 下的多个field O(N)
HSETNX  如果field已经存在则不做任何操作  O(1)
HEXSITS  判断hash中的field是否存在，存在返回1，不存在返回0，0(1)
HDEL  删除制定hash中的field O(N)
HINCRBY  对指定hash 的一个field进行INCRBY O(1)
```
- 以下命令存在风险,以下三个命令是进行完整遍历，会耗费大量性能 如果需要遍历可以使用HSCAN命令
```text
HGETALL，返回制定hash中的field value对 O（n）
HKEYS/HVALS 返回指定hash中的所有field /value O(n)
```

#### 5.Set
- redis set是无序的，不可重复的String集合
```text
SADD  向指定set中添加member，set不存在，会自动创建 O（n）,n为需要创建的member数
SREM  从指定set移除一个或者多个member 时间复杂度O(N),N为数据 
SRANDMEMBER 从制定set中随机返回一个或者多个member
SPOP 从set中随机移除N个文件，并返回
SCARD 返回制定set中的member
SISMEMBER 判断指定value是否存在与指定set,O(1)
SMOVE 将指定member从一个set移至另一个set
```
- set需要慎用的命令，以下命令是因为计算量较大，特别是set数据量大的情况下更应该避免
```text
SMENBERS  返回指定hash中的所有member O(n)
SUNION/SUNIONSTORE 计算多个set的并集并返回，或者存入另一个set中 O(N)
SINTER/SINTEERSTORE 计算多个set的交集并返回，或者存入另一个set中 O(N)
SDIFF/SDIFFSTORE 计算多个set的差集并返回，或者存入另一个set中 O(N)
```

#### 6.SortedSet
- redis SortedSet是有序的，不可重复的，每一个元素都需要指定一个分数，sortedSet需要更需根据这个分数进行升序排序，比如是排名的ID，就可以自然地从1到N进行排序
```text
ZADD 向指定sortedSet添加一个或多个member，O(N)
ZREM 向指定sortedSet删除一个或多个member
ZCOUNT 返回指定SortedSet中指定score范围内的member数量 O(LogN)
ZCARD 返回指定set中member数量 O(1)
ZSCORE 查找指定member的score O(1)
ZRANK/ZREVRANK 返回指定member在SortedSet中的排名，ZRANK返回升序排序的排名，ZREVRANK返回降序排序的排名 O(LOGN) 
ZINCRBY 同INCRBY 对指定SortedSet 指定score进行自增 O(LOGN)
```
sortedSet慎用命令：
```text
ZRANGE/AREVRANGE 返回指定sortedSet中指定排名范围的所有member O(LOGN+M)
ZRANGEBYSCORE/ZREVRANGEBYSCORE:根据score返回指定排名范围的所有member O(LOGN +M)
ZREMRANGEBYRANK/ZREMREANGESCORE 通过排名或者分数返回范围内的所有member
```
以上命令会由于set的长度而产生不可估计的问题，可以使用ZSCAN


#### 7.Bitmap HyberLogLog
- Bitmap的话，是redis将String作为Bitmap来使用，是将String转换为bit的数组，使用bitmap存储true false的数据，用来节省空间
- HyberLogLog是一种用于数据统计的数据结构，类似与set，维护一个不可重复的String集合，不维护具体内容，只维护member的数量，用于计算一个集合不重复的元素个数，比set更节省空间

#### 8.其余的命令
- EXSITS:判断key是否存在 O(1)
- DEL: 删除指定key O(N)
- EXPIRE/PEXPIRE :给一个key设置过期时间 0(1) O(1) 
- TTL/PTTL:返回一个key的生存时间，还剩余的时间 O(1)
- RENAME REMAMENEX,给指定key重命名，一个存在依旧会覆盖，一个存在就不操作  O(1)
- TYPE : 返回指定key的类型,O(1)
- CONFIG GET：获取redis某个配置项的当前值,O(1)
- CONFIG SET：设置redis某个配置项的当前值，O(1)
- CONFIG REWRITE： 重新设置redis某个配置项的当前值 


### 五、数据持久化
- redis的数据持久化主要是有两种方式，一个是RDB（通过定期快照存储的方式进行持久化和故障恢复），一个AOF（通过书写日志文件，通过文件的追加文件的重读，实现持久化和故障恢复），各有各得优缺点
- 一般数据持久化方案会是定期的RDB快照备份，和AOF指定日志节点日志重做的方式，来完成故障恢复
- 数据持久化的工作一般是在需要当作完整的内存数据库使用才需要开启的，一般把redis当作数据缓存和一些临时排序功能的时候就不需要开启持久化操作了
- RDB是建议打开的基本持久化方式，RDB的持久化基本不消耗redis的性能，它通过fork一个子进程，将持久化工作由子进程完成
- redis不论因为什么原因宕机，都能够恢复到上一个快照的时间节点，省去了手动DB数据同步的工作，恢复的时间较快


#### 1.RDB方式
- 会定期保存数据快照到一个rdb文件，并在启动的时候自动加载这个rdb文件
- 这个可以在配置文件中设置：
```text
save [seconds] [changes] ：在指定秒数时间内如果发生changes次数据修改，就进行一个RDB快照保存 ，例如 save 60 100,可以每60秒检查一次数据变更情况，如果60秒内发生了100次数据变更，进就行一个快照

```
- 如果默认开启RDB快照，默认参数是：save 900 1 save 300 10 save 60 10000 采用三级持久化策略
- 也可以手动出发快照 BGSAVE

- RDB的优点：对性能影响小（fork子进程），每次快照能生成一个完整的数据快照，是非常可靠的灾难恢复手段，RDB会比AOF恢复快很多
- RDB的缺点：快照是定期的，如果redis宕机会丢失部分数据
- 如果数据集很大，子进程的快照也会消耗将近1秒的时间


#### 2.AOF方式
- 如果采用AOF，redis会把每个写请求记录在日志文件中，redis重启就会重读AOF文件，将所有写操作顺序指定一遍，确保数据更新到最新
- appendonly yes
- AOF提供三种fsysnc配置，always/everysec/no. 通过appendsync配置
```text
appendsync no 将flush文件交给OS决定，速度最快
appendsync always 每写入一条日志就进行一次fsync操作
appendsync everysec，每秒进行fsync一次
```
- AOF文件会很大，并且无用的更新操作会很多，中间的结果对最终数据状态来说是无用的，使数据恢复的时间过长
- redis提供了AOF rewrite功能，重写AOF文件，只保留数据最新状态的最小写操作集
```text
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
```
以上配置表示：如果日志文件增加了100%，并且超过64mb就会进行rewrite，不然不会

- AOF的优点：
    - 很安全，会实施记录全部的数据操作，appendsync always会保证数据不丢失
    - AOF文件在断电时也不会损坏
    - AOF文件易读、可修改，只要AOF文件没有在进行rewite ,就能够将将文件备份出来，进行编辑和替换

- AOF缺点：
    - AOF文件很大
    - 恢复时间很长，比RDB慢
    - 性能消耗比RDB大

- 以上就是RDB和AOF的两种数据持久化策略，如果对缓存数据的重要性要求不高的，或者可以方便地从数据库同步出来的，可以使用简单的额RDB进行持久化
- 如果缓存数据极其重要，不可丢失，那就需要AOF或者RDB和AOF组合的方式进行持久化，保证数据安全


### 六、内存管理和数据淘汰机制
- 在缓存中，由于内存的限制，一定需要将冷数据进行清理，redis提供了几种机制
- redis的默认内存使用：32位最大3G，64位没限制，会无限制的占用内存空间
- 那一般redis运行都需要设置最大可用内存，maxmemory 100MB
- 在内存占用达到最大的时候，再向redis写入数据的时候，redis就需要使用内存淘汰机制进行数据的释放，如果这个时候没有配置内存淘汰策略的话，写入程序就会报错，读程序不影响
- 这个最大内存的设置不能接近于系统内存，因为还需要系统内存做数据同步或者其他

- 数据淘汰机制
```text
volatile-lru:只对设置了有效期的key ,进行最近最少使用的淘汰策略

allkeys-lru ：对所有key进行最近最少使用的key的淘汰策略

vilatile-random：随机淘汰数据，只针对设置了有效期的key

allkeys-random:随机淘汰数据：针对所有Key

volatile-ttl:淘汰有效期最短的key
```
- 一般是最大使用内存的配置配合淘汰机制的配置一起，推荐使用volatile-lru
- maxmemory-policy volatile-lru 


- Pipelining:将连续执行的操作进行绑定，批量提交
- 对于一些简单的重复的 类似于批量读，批量写是可以通过mget mset，使用批量操作可以减少单次操作的性能消耗，
- 可是几个不相同的命令就不可以了
```text
例如：
SET A "ABC"
INCR B
HSET C NAME "HI"
```
- 将多条命令以rn分割执行，redis会按照顺序操作
- 客户端大部分都支持pipeline命令的组装
- pipelining只能用于执行不相关的命令，一个命令以来另一个命令的结果就不能依靠pipeline执行



### 七、事务
- redis中的事务要求，指定命令之间的顺序是固定的并且连续执行，不允许命令的插入
- 通过MULTI EXEC来执行事务
```text
MULTI
GET COUNT
SET COUNT 0
EXEC
```
- 通过MULTI就开启一个事务，将所有的读写命令写入队列，直到EXEC才将队列中的命令拿出来顺序执行
- 可以通过DISCARD取消事务，并清空命令队列
- redis的事务只能检测语法上面的错误，对于其他的异常都无法检测，并且并不能够实现错误的回滚，一个事务中可能出现部分指令成功，部分指令失败
- redis对于事务可以通过WATCH指令实现乐观锁的效果
- WATCH机制：在事务EXEC执行时，redis会检查所有被WATCH的key,只有被WATCH的key从WATCH从始至今没有发生变更的 EXEC才会被执行，不然EXEC命令会返回失败


### 八、Scripting
- 通过EVAL EVALSHA命令，可以让redis执行lua脚本，这就类似于存储过程，直接将读写放在服务端执行，不在客户端进行调用，提升性能
- 作为事务的替代者，事务的功能Scripting都能实现，官方推荐使用LUA脚本的书写替代事务，执行效率能获得很大的提升

### 九、redis性能调优
- redis是单线程，所有命令串行执行，某一个耗时过长，就会阻塞拖慢后续的命令执行

1.枪毙耗时较长的操作

2.将连续执行不相关的命令通过pipelining组合提交

3.关闭操作系统Transparent huge pages的属性
echo never > /sys/kernal/mm/transparent_hugepage/enabled

4.降低固有延迟，查看固有延迟： 。/redis-cli -intrisic-latency 100

5.检查持久化策略的耗时

6.考虑引入读写分离

7.长耗时命令 redis的命令的复杂度一般在O(1)-O(N)之间，对于N值以及并集、交集、差集的操作需要重视，会随着N的增加而增加

8.不要将list当作列表使用，注意redis的list的队列的意思

9.严格控制HASH SET SORTEDSET的大小

10.将排序，并集，交集的操作放在客户端执行

11.禁止keys命令

12.避免一次读取所有值，而是通过scan进行分批读取，进行游标遍历  SCAN HSCAN SSCAN ZSAN

13.show log查看耗时操作
```text
showlog-log-slower-than xxxms,执行时长大于xxx的 
showlog-max-len xx，最多log记录数
slowlog get[number] 输出指定数量日志
```
14.网络引发的延迟 长连接，链接的频繁创建和销毁，批量操作推荐使用pipelining提交

15.数据持久化引发的延迟，对于RDB来说，延迟的可能性较小，AOF由于写入文件数据量变大， 配置的不合理，rewrite等过程，会引起持久化延迟
- 几种持久化的合理策略：
```text
AOF + FSYNC always  保证数据安全，性能消耗较大，每一次写操作都需要写日志

AOF + FSYNC every second ：比较好的折中

AOF + FSYNC fsync never ： AOF最优性能方案
```
- 每一次RDB快照结合AOF rewrite 可以根据集体的需求配置快照次数和rewrite的时机，因为fork子进程做快照和rewrite都有性能损耗，但是这个RDB的操作延时一般肯定在1秒以内

16.Swap引发的延迟 
- linux 进程将redis内存分页拷贝到swap空间，会阻塞redis进程，会出现在物理内存不足或者大量IO操作的时候
- /proc/<pid>/smaps,这个文件中会记录swap的情况，包括size，如果redis延迟的时间点有swap的记录，则可能是swap的原因

17.数据淘汰
- 同一时间内的大量key过期之类的，出现缓存雪崩
- 应该进行读写分离，分散redis每个节点的压力，并且key的设计和过期的设计相对分散，对于缓存的过期时间可以不设置固定值，而是通过设置一个区间范围内的随机值，使过期时间分布均匀，避免这种状况的出现



### 十、主从复制
- redis的搭建模式有两种：
    - 哨兵高可用版本，通过哨兵对所有集群节点的监控，可以动态进行master节点的选举和同步位点消息的通知
    - 集群模式，通过增加副本和节点数，将读写请求分散，提高集群的吞吐量，并且能够提高数据安全程度，数据更分散

- 开启读写分离，只需要多个节点组成哨兵模式或者集群模式，就可以实现读写分离，master节点提供读写，slave节点提供实时性要求不高的读（因为数据同步难免会出现数据的不一致）
- 配置以上一个模式里面，通过
```text
slaveof 192.168.1.1：6379 进行副本的配置
```
slave加入master,都会由master进行一次冷启动数据同步，由master出发BGSAVE生成RDB文件，将其发给slave节点进行导入，导入完成后master再将增量的数据通过redis-protpcol同步给slave
 
- sentinel做自动failover,会根据分布式一致性算法，保证数据主节点选举和数据消息同步
- sentinel节点可以监控集群中的节点，通过master节点自动发现slave
 
- 集群为什么要分节点，为什么需要以分片方式存储
    - 主要是因为物理内存的有限和并发读写需求的巨大，需要分布式节点来分摊存储的压力和请求的压力
    - 推荐的就是Redis cluster官方的分片方式


- redis cluster是通过不同节点上的文件互为备份，数据能够自动复制和分发到不同节点，数据的访问能自动路由到制定的分片，分片之间的副本是依靠主从复制的能力实现
- cluster模式是依靠redis内部一个分片内部的节点监控和自动failover

- 分片原理
    - redis有16384个hash slot，每次计算一个key的CRC16 ,通过值对16384进行取模能够发现在哪一个分片上，如果该分片上不存在，就路由到分片的副本上进行数据的读取  


- redis 读写分离
- redis scripting lua
- redis pipelining spring

### 十一、redis的那些客户端
- Redis客户端选择 官方推荐jedis redisson lettcure 

#### 1.Jedis 
- 功能相对少一些
- 简便，便于集成
- 支持连接池
- 支持pipelining 
- 支持事务 lua脚本，哨兵模式，集群模式
- 不支持读写分离
- 文档很少


#### 2.Redisson 
- 相对维护会比jedis好，功能也多一些
- 支持异步请求
- 底层基于netty，实现非阻塞io，性能较好
- 支持pipelining 集群模式 哨兵模式 lua脚本
- 不支持事务，官方已经建议用lua脚本替代事务，事务比较难维护，而且执行效率不高，作用在客户端消耗大，可以使用作用在服务端的lua脚本
- 支持集群模式下的pipelining 
- 支持读写分离，支持负载均衡，在主从模式和集群模式都能使用
- 内部有tomcat session manager，可以给tomcat 6,7,8 提供支持
- 与spring session 等框架一起使用，可以实现redis 下的session 共享
- 文档丰富，中文支持较好


### 十二、过期机制
- 关于数据淘汰机制也介绍过了，有很多具体的配置，是对于内存沾满后如何进行内存淘汰，但是内存占满是一个非常危险的操作，一般肯定是没有占满之前能够自动对废数据进行清除
- 正解：Redis 采用的是定期删除+惰性删除策略
- 那么这个过期的方式有三种，定时，定期，惰性
- 那为什么正解不用定时呢，定时会对每一个key设置这个定时器，每一个都添加定时器会非常消耗cpu，这不是合理的损耗
- 那么定期加惰性的过期机制是怎么实现的呢？
- 定期过期，是随机抽取key进行检测和清除
- 惰性过期，是当这个key再一次被访问并发现是过期的之后，被清除。这个key是随机的，而不是所有的，那么就是可能出现定期过期和惰性过期并没有将应该删除的key删除，而导致内存逐渐增加
- 这个时候就需要内存淘汰机制了，这个机制能够彻底的按照配置将内存占满情况下过期的key进行清除



### 十三、缓存会出现的问题以及解决方案
- 缓存会出现缓存雪崩，缓存穿透，缓存和数据库写的一致性，缓存并发竞争的问题
- 一致性问题还可以再分为最终一致性和强一致性
    - 数据库和缓存双写，就必然会存在不一致的问题
    - 前提是如果对数据有强一致性要求，不能放缓存，只能保证最终一致
    - 对于写入，先写数据库在删缓存，下一次访问，缓存会去读取最新的数据。写入如果失败，缓存也不会错误，读到的数据也是和数据库一致的。但是删除缓存失败的话，缓存中的数据可能会有问题，因而提供一个补偿措施即可，例如利用消息队列
    


- 缓存雪崩和缓存穿透发生的场景很特殊，但是这也是黑客攻击的地方
- 雪崩是指一批key的同时过期，请求全部压倒数据库端，导致数据库不可用
- 穿透是指程序的处理可能存在不当或者考虑欠缺的地方，比如空key，带有一个破坏性的查询，这个key缓存中没有，因而会直接查询数据库，返回对空key没有做处理，那么这样的sql注入就可能击垮数据库

- 怎么预防这些问题呢？
    - 对于缓存穿透
        - 采用互斥锁，缓存过期去操作数据库的时候都加锁，获得锁才进行操作，不然休眠一段时间
        - 采取缓存预热措施，对于缓存过期和程序初始化阶段，使用异步起线程主动更新缓存方式
        - 对于穿透的问题，使用一个高效有用的拦截机制，比如布隆过滤器，存储一些列有效key，自动实现对无线key的过滤
    - 对于缓存雪崩
        - 可以给缓存设置的过期值，在一个过期时间段内随机取值，而不是统一的值
        - 互斥锁，这个方案相对没有这么好
        - 双缓存方案，相对重一点，可是很稳当。有缓存a b，在a中读取数据，有则返回，没有就从b读，当发现ab内缓存情况不一致时，另起异步线程实现缓存更新


- 并发竞争key的问题，那对于并发操作，一般大家会想到加锁，直接就通过添加一个redis 的分布式锁或者事务实现，那么有考虑需要执行的顺序吗
- 一般情况下，我们的产线环境都是分布式分片存储，那么这种情况下虽然事务能保证执行顺序，但是事务就不能支持啦，但是还可以用分布式锁，可是分布式锁只是获取资源的一种方式，并不能保证执行的顺序
- 总结，不考虑执行顺序可以使用分布式锁进行，如果考虑执行顺序，可以参考多版本控制，这个版本的参考字段是时间戳，如果第二阶段的操作优先于第一阶段，那第一阶段后来去修改的时候，发现时间比现有时间之前，则不做修改了




### 十三、那些数据结构作用
- String  一个key 一个String 或者数字
- Hash   一个key 一个对象，做单点登录的时候，可以用这种数据结构存储用户信息，以 CookieId 作为 Key，设置 30 分钟为缓存过期时间，能很好的模拟出类似 Session 的效果
- list
    - Redis 中的list是用来实现双端队列功能的，而不是普通意义上的数组。可以利用 lrange 命令，做基于 Redis 的分页功能，性能极佳，用户体验好。
    - 关于分页检索统计数据，普通常规使用的方法是每晚将所有的统计值，按照跑数据的方式进行计算并存储，放在非日常业务数据库，实时数据可以实时计算，然后将离线计算数据与实时数据聚合，对于分页前十页的数据进行统一的缓存，用于分页

- set
    - 是无序不重复的集合，所以可以做全局去重的功能。系统一般都是集群部署，使用 JVM 自带的 Set 比较麻烦
    - 可以利用交集、并集、差集等操作，可以计算共同喜好，全部的喜好，自己独有的喜好等功能

- SortedSet 
    - Sorted Set 多了一个权重参数 Score，集合中的元素能够按 Score 进行排列
    - 可以做排行榜应用，取 TOP N 操作
    - Sorted Set 可以用来做延时任务。如果是使用数据结构来实现topN的操作的话，可以使用数组排序或者大顶堆方式获取

### 十四、redis的那些个应用场景
- Redis 和memcache都能够作为内存数据库，它们有什么区别
    - 当redis 只是作为一个键值的内存数据库，确实他们两个没什么太大区别
    - memcache 仅是一个纯缓存的键值数据库，而redis是一个非关系型数据库，它能根据它拥有的数据类型实现很多功能

- redis有哪些应用场景呢
    - 最常见的就是缓存，利用redis 的几种数据结构做数据的缓存： String hash set list sortedset 

    - 计数器，通过对一些简单的点击时间，阅读时间进行递增统计，通过自身的incr 属性，就可以简单绑定对象和计数，效率很高，能大大减轻数据库的压力，一般这种频繁的递增量会很大

    - 排名，可以使用sortedset 通过对阅读点击数逆序，获取对应的排名，如果使用Redis的zadd来实现这个功能，使用zrevrange 来按照分值获取前10名或者50名的歌曲，或者获取歌曲排名，都是操作比较容易的，试想一下这种范围取值，如果使用Mysql来操作，一般会全表扫描，对I/O、数据库都是压力，所以选Redis

    - geo数据的存储：从3.2版本开始，还支持对geo数据的存储和基本的检索，包括可以应用在用户和地理位置的绑定，这样可以获取实时的用户位置，并判断用户之间的距离
        - Redis的 GEO特性，我们可以通过提前输入具体的地理位置信息，经纬度一些主要内容，在使用时，可以快读定位到APP使用者的位置，以及位置之间的距离等

    - 队列功能也很常用，就是list，这个属性可以实现阻塞或者非阻塞队列的功能，能够让程序在执行时被另一个程序添加到队列

    - 还有一种场景就是交友，将好友的兴趣和关注等数据放入缓存，通过set的交差补集，通过使用Set集合的差查找爱好不同，或者爱好相同的点，增加交友匹配度等

- 以上是很典型的应用场景，但是redis 相对于数据库不能做的有，集群情况下的事务，虽然推荐可以用lua脚本实现，不能处理复杂sql，不能做冷数据存储

### 十五、redis的主从、哨兵和集群
- 开始踏进了redis的坑，就哪儿哪儿都不知道，都不了解，最先认识单点，然后就是哨兵，还有什么主从和集群的说法天花乱坠的，今天来从概念和架构上了解它们分别是怎么样的

#### 【redis主从】：
- 是备份关系，我们操作主库，数据也会同步到从库。 如果主库机器坏了，从库可以上。就好比你 D盘的片丢了，但是你移动硬盘里边备份有

#### 【redis哨兵】：
- 哨兵保证的是HA，保证特殊情况故障自动切换，哨兵盯着你的“redis主从集群”，如果主库死了，它会帮助我们选举出新的leader并通知我们同步

#### 【redis集群】：
- 集群保证的是高并发，因为多了节点，一起分担存储和读写。同时集群会导致数据的分散，整个redis集群会分成一堆分片数据

https://www.cnblogs.com/demingblog/p/10295236.html

###架构图
(1)standalone类型架构
- 用于可穿透业务场景，如后端有DB存储，脱机影响不大的应用
- 一台机器宕机，能够立马替换
```text
           USER                                   USER
             |                                      |
             |                                      |
       |-------------------------------------------------|
       |         可穿透Cache  Standalone                  |
       |-------------------------------------------------|
             |                                       |
           Memcache                                Redis            
```

(2) sentinel类型架构
- 用于高可用需求场景,可用于高可用Cache,存储等场景
- 内存/QPS受限于单机
- 哨兵会帮你监控集群节点状态，以及做master节点的选举
```text
            USER                                  
             |                                      
             |                                      
       |-------------------------------------------------|
       |                     Sentinel                    |
       |            高科哦那单实例Storage                 |
       |-------------------------------------------------|
             |-------------------------------------
             |                   |                |  （User端订阅 switch-master频道 实现master在线迁移）
       |--------------------------------------------------|
       |                   Sentinel集群                   |
       |  Sentinel1          Sentinel2       Sentinel3    |
       |                   三个节点互为备份                |
       |--------------------------------------------------|
              |                   |                |   
              |-------------------------------------
              |
            Master  -----------------> Slave (slave全量复制Master的数据)
```

(3)cluster类型架构
- 用于高可用、大数据量需求场景,可用于大数据量高可用Cache/存储等场景
- 内存/QPS不受限于单机，可受益于分布式集群高扩展性
- 数据分片存储，给程序增加访问难度，要考虑未来数据量是否会达到这样的程度，再考虑搭建集群模式
```text
          |---Client----|                         |--Client---|
          |             |                         |           |
|---------|   Cluster   |-------------------------|  Cluster  |-----------|
|         |-------------|                         |-----------|           |
|                                Cluster                                  |
|                            高可用集群Storage                             |
|-------------------------------------------------------------------------|
                 |   使用CRC16&13684算法计算虚拟slot       |
                 |   数据路由key --> slot --> node        |
                 |                                       |
  |---------------------------------------------------------------------|
  |                            Cluster集群                              |
  |    Slave<-------Master-------------------Master--------->Slave      |
  |                    |                        |                       |
  |                    |                        |                       |
  |    Slave<-------Master-------------------Master---------->Slave     |
  |                     |                       |                       |
  |                     |--------Master---------|                       |
  |                                |                                    |
  |                              Slave                                  |
  |---------------------------------------------------------------------|    
```









