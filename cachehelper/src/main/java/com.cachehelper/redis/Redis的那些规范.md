- 跟随阿里云规范，看看redis里的那些注意点

### 一、键值设计
1.key的设计
#### 需要具有可读性和可管理性
- 以业务名(或数据库名)为前缀(防止key冲突)，用冒号分隔，比如业务名:表名:id
- 这边需要注意redis的冒号代表的就是namespace的划分，不能在业务字段当中存在冒号的字段
#### 需要具有简洁性
- 控制key的长度
#### 不要包含特殊字符
- 不要包含空格、换行、单双引号以及其他转义字符

2.value设计
#### 拒绝bigkey
- 防止网卡流量、慢查询，string类型控制在10KB以内，hash、list、set、zset元素个数不要超过5000
- 对于list数据类型不要按照java arraylist用，它的应用场景是queue
- 非字符串的bigkey，不要使用del删除，使用hscan、sscan、zscan方式渐进式删除
- 注意防止bigkey过期时间自动删除问题造成阻塞、查找方法和删除方法

#### 选择适合的数据类型
#### 控制key的生命周期
- 使用expire设置过期时间，为防止集中过期可以设置一定区域内随意过期时间，不过期的数据重点关注idletime


### 二、命令使用
1.O(N)命令关注N的数量
- 对于一些O（n）复杂度的命令，需要关注n的数量，例如hgetall、lrange、smembers、zrange、sinter等并非不能使用，有遍历的需求可以使用hscan、sscan、zscan代替

2.禁用命令
- 对于一些经过使用会造成很大隐患的命令，禁止使用，禁止线上使用keys、flushall、flushdb
- 通过redis的rename机制禁掉命令，或者使用scan的方式渐进式处理

3.合理使用select

4.使用批量操作提高效率
- 使用mget\mset\pipelining等操作
- 原生是原子操作，pipeline是非原子操作
- pipeline可以打包不同的命令，原生做不到
- pipeline需要客户端和服务端同时支持

5.不建议过多使用Redis事务功能
- redis事务在新版本中渐渐都不推荐了，建议使用scripting lua来执行一系列操作，而不是事务

6.Redis集群版本在使用Lua上有特殊要求

7.monitor命令不要长期使用

### 三、客户端使用
1.避免多个应用使用一个Redis实例，需要做好业务的拆分，公共数据服务化

2.使用连接池，可以有效控制连接，提高效率
```text
Jedis jedis =jedisPool.getResource();
//具体命令
jedis.executeCommand()
```
3.高并发下建议客户端添加熔断功能(例如netflix hystrix)

4.设置合理的密码，如有必要可以使用SSL加密访问

5.根据自身业务类型，选好maxmemory-policy(最大内存淘汰策略)，设置好过期时间
默认策略是volatile-lru，即超过最大内存后，在过期键中使用lru算法进行key的剔除，保证不过期数据不被删除，但是可能会出现OOM问题。

### 四、相关工具
1.redis间数据同步可以使用：redis-port

2.redis大key搜索工具

3.热点key寻找：建议使用facebook的redis-faina

4.删除大key
- Hash删除: hscan + hdel
- List删除: ltrim
- Set删除: sscan + srem
- SortedSet删除: zscan + zrem
