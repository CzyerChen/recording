> mybatis有一级缓存和二级缓存之说，今天就具体了解一下

### 什么是一级缓存，什么是二级缓存
- 默认情况下一级缓存是开启的，而且是不能关闭的
- 一级缓存是指SqlSession级别的缓存，当在同一个SqlSession中进行相同的SQL语句查询时，第二次以后的查询不会从数据库查询，而是直接从缓存中获取，一级缓存最多缓存1024条SQL
- 二级缓存是指可以跨SqlSession的缓存。是mapper级别的缓存，对于mapper级别的缓存不同的sqlsession是可以共享的。

```text 
                     一次会话中的一级缓存，针对单个SQL ，如果SQL一致，就直接拿缓存当中的数据
Client进行查询 <---> 开启一次会话 SqlSession对象 <----------------> Executor  <------------> DB
                       CachingExecutor                    一级缓存（本地缓存Local Cache）
                           /|\
                            |
   -----------------------------------------------------------------------------------------------------------
                            |
                           \|/
        全局配置Configuration对象
                         Mapper namespace1       Mapper namespace2            Mapper namespace3
        二级缓存             Cache                     Cache                        Cache
        全局缓存
   Mybatis实现：LRU FIFO       |                         |                            |
      --------------------------------------------------------------------------------------------------------
                               |                         |                            |
      第三方缓存库        OSCache         EhCache             Redis           MemCache

一级缓存：
第一次发出一个查询sql，sql查询结果写入sqlsession的一级缓存中，缓存使用的数据结构是一个map。 key：MapperID+offset+limit+Sql+所有的入参
value: 用户信息
同一个sqlsession再次发出相同的sql，就从缓存中取出数据。如果两次中间出现commit操作（修改、添加、删除），本sqlsession中的一级缓存区域全部清空，下次再去缓存中查询不到所以要从数据库查询，从数据库查询到再写入缓存


二级缓存：
mapper级别（mapper同一个命名空间），mapper以命名空间为单位创建缓存数据结构，结构是map。mybatis的二级缓存是通过CacheExecutor实现的。CacheExecutor，其实是Executor的代理对象。所有的查询操作，在CacheExecutor中都会先匹配缓存中是否存在，不存在则查询数据库
key：MapperID+offset+limit+Sql+所有的入参
具体使用需要配置：
1. Mybatis全局配置中启用二级缓存配置
2. 在对应的Mapper.xml中配置cache节点
3. 在对应的select查询节点中添加useCache=true
```

