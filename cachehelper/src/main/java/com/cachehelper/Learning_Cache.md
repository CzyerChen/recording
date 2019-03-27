- 分布式缓存：Memcache Redis
- 本地缓存： Ehcache Guava_cache  Caffine
- 以上会一一实践


### 本地缓存的区别：Ehcache Guava_cache Caffine
1. Ehcache支持持久化到本地磁盘，Guava不可以
2. Ehcache有现成的集群解决方案，Guava没有
3. Ehcache jar包庞大，Guava Cache只是Guava jar包中的工具之一;
4. 两种缓存当缓存过期或者没有命中的时候都可以通过load接口重载数据，调用方式略有不同;
两者的主要区别是Ehcache的缓存load的时候，允许用户返回null Guava Cache则不允许返回为null;
Guava Cache是根据value的值是否为null来判断是否需要load，所以不允许返回为null，但是使用的时候可以使用空对象替换
5. Ehcache有内存占用大小统计，Guava Cache没有
6. Ehcache在put缓存的时候，对K、V都做了包装，对GC有一定影响
7. Caffine 就是基于Guava_cache开发的，Springboot2.0中将Caffine取代了Guava_cache

