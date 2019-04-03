- Redis的客户端有很多，jedis,redisson,lettcure,这些都是官方推荐的，各自有各自的特点
- Jedis 是一个原声的redisTemplate,可以执行很多原生命令，Jedis中的Java方法基本和Redis的API保持着一致
- Jedis仅支持基本的数据类型如：String、Hash、List、Set、Sorted Set
- Redisson是一个和spring关系密切，发展迅速，封装度高，让应用者可以忽略redis最底层的命令，而实现控制缓存，相对热门，但是功能较为简单，不支持字符串操作，不支持排序、事务、管道、分区等Redis特性
- Redisson 完整的实现了Spring框架里的缓存机制,在Redis的基础上实现了Java缓存标准规范（JCache）,为Apache Tomcat集群提供了基于Redis的非黏性会话管理功能,提供了Spring Session会话管理器的实现
- Redisson是架设在Redis基础上的一个Java驻内存数据网格（In-Memory Data Grid）。充分的利用了Redis键值数据库提供的一系列优势，基于Java实用工具包中常用接口，为使用者提供了一系列具有分布式特性的常用工具类
- Lettcure 没有使用过，以后可以试试
- Jedis是非常典型针对redis的客户端方案，redisson是针对缓存，一个整合面向用户使用的缓存框架，与spring的整合密切，这也是它热门的原因

### Redisson分布式缓存
- 缓存是让分布式应用程序加速的重要技术之一。存储的信息越接近 CPU，访问速度就越快。从 CPU 缓存中加载数据比从 RAM 中加载要快得多，比从硬盘或网络上加载要快得多
- Redis 是一种流行的开源内存数据存储，可用作数据库、缓存或消息代理。由于是从内存而非磁盘加载数据，Redis 比许多传统的数据库解决方案更快
- Redisson 中分布式缓存的三个重要实现：Maps、Spring Cache 和 JCache
- Redisson 是一个基于 Redis 的框架，用 Java 实现了一个 Redis 包装器（wrapper）和接口。Redisson 包含许多常见的 Java 类，例如分布式对象、分布式服务、分布式锁和同步器，以及分布式集合


### Redisson Map
- 分布式缓存使用RMapCache
```text
RMapCache<String, SomeObject> map = redisson.getMapCache("anyMap");
map.put("key1", new SomeObject(), 10, TimeUnit.MINUTES, 10, TimeUnit.SECONDS);

map.destroy()

```

### Redisson Spring Cache
- Spring 是一个用于构建企业级 Web 应用程序的 Java 框架，也提供了缓存支持
- Redisson 包含了 Spring 缓存功能，提供两个对象： RedissonSpringCacheManager 和 RedissonSpringLocalCachedCacheManager。 RedissonSpringLocalCachedCacheManager 支持本地缓存

```text
@Configuration
@ComponentScan
@EnableCaching
public static class Application {
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        return Redisson.create(config);
    }
    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<String, CacheConfig>();
        // 新建 "testMap" 缓存：ttl=24分钟，maxIdleTime=12分钟
        config.put("testMap", new CacheConfig(24*60*1000, 12*60*1000));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
```

### Redisson JCache
- JCache 是一个 Java 缓存 API，允许开发人员从缓存临时存储、检索、更新和删除对象
- Redisson 提供了 Redis 的 JCache API 实现
```text
MutableConfiguration<String, String> config = new MutableConfiguration<>();
CacheManager manager = Caching.getCachingProvider().getCacheManager();
Cache<String, String> cache = manager.createCache("namedCache", config);


MutableConfiguration<String, String> jcacheConfig = new MutableConfiguration<>();
Config redissonCfg = ...
Configuration<String, String> config = RedissonConfiguration.fromConfig(redissonCfg, jcacheConfig);
CacheManager manager = Caching.getCachingProvider().getCacheManager();
Cache<String, String> cache = manager.createCache("namedCache", config);

```
### Redisson 官方文档
```text
Redisson采用了基于NIO的Netty框架，不仅能作为Redis底层驱动客户端，具备提供对Redis各种组态形式的连接功能，对Redis命令能以同步发送、异步形式发送、异步流形式发送或管道形式发送的功能，LUA脚本执行处理，以及处理返回结果的功
```
- 数据结构
```text
在此基础上融入了更高级的应用方案，不但将原生的Redis Hash，List，Set，String，Geo，HyperLogLog等数据结构
封装为Java里大家最熟悉的映射（Map），列表（List），集（Set），通用对象桶（Object Bucket），地理空间对象桶（Geospatial Bucket）
，基数估计算法（HyperLogLog）等结构，在这基础上还提供了分布式的多值映射（Multimap），本地缓存映射（LocalCachedMap），
有序集（SortedSet），计分排序集（ScoredSortedSet），字典排序集（LexSortedSet），列队（Queue），阻塞队列（Blocking Queue），
有界阻塞列队（Bounded Blocking Queue），双端队列（Deque），阻塞双端列队（Blocking Deque），阻塞公平列队（Blocking Fair Queue），
延迟列队（Delayed Queue），布隆过滤器（Bloom Filter），原子整长形（AtomicLong），原子双精度浮点数（AtomicDouble），BitSet等Redis原本没有的分布式数据结构
```
- redisson对锁的追求
```text
Redisson还实现了Redis文档中提到像分布式锁Lock这样的更高阶应用场景。
事实上Redisson并没有不止步于此，在分布式锁的基础上还提供了联锁（MultiLock），
读写锁（ReadWriteLock），公平锁（Fair Lock），红锁（RedLock），信号量（Semaphore），
可过期性信号量（PermitExpirableSemaphore）和闭锁（CountDownLatch）
这些实际当中对多线程高并发应用至关重要的基本部件。
正是通过实现基于Redis的高阶应用方案，使Redisson成为构建分布式系统的重要工具
```

- 分布式服务的桥梁
```text
Redisson广泛的使用了承载于Redis订阅发布功能之上的分布式话题（Topic）功能。
使得即便是在复杂的分布式环境下，Redisson的各个实例仍然具有能够保持相互沟通的能力。
在以这为前提下，结合了自身独有的功能完善的分布式工具，
Redisson进而提供了像分布式远程服务（Remote Service），分布式执行服务（Executor Service）
和分布式调度任务服务（Scheduler Service）这样适用于不同场景的分布式服务
```
- Redisson Node
```text
Redisson Node的出现作为驻内存数据网格的重要特性之一，
使Redisson能够独立作为一个任务处理节点，以系统服务的方式运行并自动加入Redisson集群，
具备集群节点弹性增减的能力。然而在真正意义上让Redisson发展成为一个完整的驻内存数据网格的，
还是具有将基本上任何复杂、多维结构的对象都能变为分布式对象的分布式实时对象服务（Live Object Service），
以及与之相结合的，在分布式环境中支持跨节点对象引用（Distributed Object Reference）的功能
```

- Redisson的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上

- 如果目前Redis的应用场景还仅限于作为缓存使用，您也可以将Redisson轻松的整合到像Spring和Hibernate这样的常用框架里。除此外您也可以间接的通过Java缓存标准规范JCache API (JSR-107)接口来使用Redisson

- Redisson生而具有的高性能，分布式特性和丰富的结构等特点恰巧与Tomcat这类服务程序对会话管理器（Session Manager）的要求相吻合。利用这样的特点，Redisson专门为Tomcat提供了会话管理器（Tomcat Session Manager)

