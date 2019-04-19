> 测试版本redis 2.6.10

### 尝试redis基础命令
redis的基本数据类型：
- 字符串
- 哈希表
- 集合
- 列表
- 有序集合
- 地理位置
- 位图
- HyberLogLog

```text
观察每一个操作的复杂度，基本平均为O(1),这也是为什么redis是单线程也能那么快的执行操作

这些操作都和操作系统底层指令紧密相关，做到更少的重排，更快的执行

关注复杂度较高的操作，建议不要在生产中随意执行

```


#### 字符串
1.set  0(1) -- 给键设置值，可以覆盖
```text
set key "value "

get key

set key "new" 原来的值会覆盖

2.6.12开始可以使用SET NX /SET XX/SET EX 过期时间秒/SET PX 过期时间毫秒
```

2.SETNX O(1) -- 键不存在进行设置
```text
redis 127.0.0.1:6379> exists job
(integer) 0
redis 127.0.0.1:6379> EXISTS test
(integer) 0
redis 127.0.0.1:6379> SETNX test "claire"
(integer) 1
redis 127.0.0.1:6379> SETNX test "new"  //已存在不会覆盖，返回值0
(integer) 0
redis 127.0.0.1:6379> GET test
"claire"
```

3.SETEX O(1) -- 过期时间 SETEX key 时间 值
```text
redis 127.0.0.1:6379> SETEX exkey 60  10086
OK
redis 127.0.0.1:6379> GET exkey
"10086"
redis 127.0.0.1:6379> TTL exkey
(integer) 17
redis 127.0.0.1:6379> TTL exkey
(integer) -1
redis 127.0.0.1:6379> GET exkey
(nil)
redis 127.0.0.1:6379> SET exkey "value1"
OK
redis 127.0.0.1:6379> GET exkey
"value1"
redis 127.0.0.1:6379> SETEX exkey 60  "value2"
OK
redis 127.0.0.1:6379> GET exkey
"value2"
redis 127.0.0.1:6379> TTL exkey
(integer) 55
```

4.PSETEX O(1) -- 和SETEX一致，将过期时间设置为毫秒级别

5.GET O(1) -- 获取键值


6.GETSET O(1) -- 返回旧值，保存新值

7.STRLEN O(1) -- 查看key对应值的长度
```text
redis 127.0.0.1:6379> SET mykey "hello"
OK
redis 127.0.0.1:6379> get mykey
"hello"
redis 127.0.0.1:6379> STRLEN mykey
(integer) 5
```
不存在则为0

8.APPEND 平均为O(1) -- 如果值是字符串，就将数据追加到后面
```text
redis 127.0.0.1:6379> EXISTS strings
(integer) 0
redis 127.0.0.1:6379> APPEND strings "str1"
(integer) 4
redis 127.0.0.1:6379> APPEND strings "str2"
(integer) 8
redis 127.0.0.1:6379> APPEND strings "str3"
(integer) 12
redis 127.0.0.1:6379> GET strings
"str1str2str3"
```

9.SETRANGE O(1)-O(M) -- SETRANGE KEY 位移量 值：将value设置到指定偏移量上
值会进行覆盖
```text
redis 127.0.0.1:6379> SET greet "hello lily"
OK
redis 127.0.0.1:6379> SETRANGE greet 6 "claire"
(integer) 12
redis 127.0.0.1:6379> GET greet
"hello claire"
```
10.GETRANGE O(N) -- GETRANGE KEY START END 取决于获取的长度
start和end都包括的内容
```text
redis 127.0.0.1:6379> GET greet
"hello claire"
redis 127.0.0.1:6379> GETRANGE greet 2 4
"llo"
```

11.INCR O(1) -- 数字进行递增，可以用于控制主键，避免冲突，不是数字就报错
```text
redis 127.0.0.1:6379> SET hits 1
OK
redis 127.0.0.1:6379> GET hits
"1"
redis 127.0.0.1:6379> INCR  hits
(integer) 2
redis 127.0.0.1:6379> GET hits
"2"
redis 127.0.0.1:6379>
redis 127.0.0.1:6379> SET hitstr "1ss"
OK
redis 127.0.0.1:6379> INCR hitstr
(error) ERR value is not an integer or out of range
```
12.INCRBY O(1) --INCRBY KEY 步长
```text
redis 127.0.0.1:6379> INCRBY hits 4
(integer) 6
```

13.INCRBYFLOAT  O(1) -- 加上浮点数增量
```text
redis 127.0.0.1:6379> INCRBYFLOAT hits 3.4
"9.4"
```

14.DECR key O(1) -- 减一
15.DECRBY key decrement O(1) --- 减N

16.MSET key value O(N) -- MSET 是一个原子性(atomic)操作， 所有给定键都会在同一时间内被设置
原子性操作很重要，一批数据需要同时写入缓存，可以使用这个命令
相同key值会被覆盖
```text
redis 127.0.0.1:6379> MSET date "2012.3.30" time "11:00 a.m." weather "sunny"
OK
redis 127.0.0.1:6379> MGET date time weather
1) "2012.3.30"
2) "11:00 a.m."
3) "sunny"
```


17.MSETNX key value O(N) -- 避免上面值会覆盖的情况而出现的
```text
redis 127.0.0.1:6379> MSETNX date "2012.3.30" time "11:00 a.m." weather "rain"
(integer) 0
redis 127.0.0.1:6379> MGET date time weather
1) "2012.3.30"
2) "11:00 a.m."
3) "sunny"
```
18.MGET O(N) -- 批量获取


#### 哈希表
1.HSET hash field value   O(1)
2.HSETNX O(1)
3.HGET O(1)
4.HEXISTS O(1)
5.HDEL O(1)
6.HLEN O(1)
7.HSTRLEN O(1)
8.HINCRBY O(1)
9.HINCRBYFLOAT O(1)
10.HMSET O(N)
11.HMSETNX O(N)
12.HKEYS O(N)
13.HVALS O(N)
14.HGETALL O(N)
15.HSCAN
```text
redis 127.0.0.1:6379> HSET lists a "value1"
(integer) 1
redis 127.0.0.1:6379> HSET lists b "value2"
(integer) 1
redis 127.0.0.1:6379> HGET lists a
"value1"
redis 127.0.0.1:6379> HGET lists b
"value2"
redis 127.0.0.1:6379> HSETNX lists a "valuenew"
(integer) 0
redis 127.0.0.1:6379> HGET lists a
"value1"
redis 127.0.0.1:6379> HSET lists c "value2"
(integer) 1
redis 127.0.0.1:6379> HGET lists c
"value2"
redis 127.0.0.1:6379> HEXISTS lists c
(integer) 1
redis 127.0.0.1:6379> HDEL lists c
(integer) 1
redis 127.0.0.1:6379> HEXISTS lists c
(integer) 0
redis 127.0.0.1:6379> HLEN lists
(integer) 2
redis 127.0.0.1:6379> HSTRLEN lists a
(error) ERR unknown command 'HSTRLEN'
redis 127.0.0.1:6379> HSETNX counts a 10
(integer) 1
redis 127.0.0.1:6379> HINCRBY counts a 10
(integer) 20
redis 127.0.0.1:6379> HINCRBYFLOAT counts a 1.1
"21.1"
redis 127.0.0.1:6379> HMSET setvalues a "valuea" b "valueb"
OK
redis 127.0.0.1:6379> HMGET setvalues
(error) ERR wrong number of arguments for 'hmget' command
redis 127.0.0.1:6379> HMGET setvalues a b
1) "valuea"
2) "valueb"
redis 127.0.0.1:6379> HKEYS  setvalues
1) "a"
2) "b"
redis 127.0.0.1:6379> HVALUES  setvalues
(error) ERR unknown command 'HVALUES'
redis 127.0.0.1:6379> HVALS  setvalues
1) "valuea"
2) "valueb"
redis 127.0.0.1:6379> HGETALL setvalues
1) "a"
2) "valuea"
3) "b"
4) "valueb"
```

#### 列表
1.LPUSH key value O(1)  注意： 插入到表头 ，key不存在会创建列表
2.LPUSHX key value O(1)  注意： 插入到表头 ，key不存在不会创建列表，什么也不做，这是差别
3.RPUSH key value  O(1) 注意：插入到表尾(最右边)，key不存在会创建列表
4.RPUSHX key value O(1) 注意：插入到表尾(最右边)，key不存在不会创建列表，什么也不做，这是差别
5.LPOP key 头弹出元素，左边
6.RPOP key 尾弹出元素， 右边
7.RPOPLPUSH source destination  O(1) -- 在一个原子时间内，将右边尾元素查看，并将值放入目标的头中，左边
- 注意：如果源和目标是同一个，那么这将是一个列表的旋转，像Collections.rotate()
8.LREM key count value  O(N)， N 为列表的长度 -- 移除从表头到表尾，最先发现的N个 value
9.LLEN key O(1) -- 列表长度
10.LINDEX key index O(N)  --- 遍历列表，返回指定位的值
11.LINSERT key BEFORE|AFTER pivot value O(N) -- 寻找需要插入的点，在对应的位上插入数据
12.LSET key index value 作用在头尾元素上O(1)/其他O(N)
13.LRANGE key start stop O(S+N) -- stop 为-1是代表全部
14.LTRIM key start stop O(N) --- trim是修建的意思-- 表示只留下开始到结束之间的数据
15.BLOP O(1) ---阻塞列表的弹出
16.BROP O(1) ---阻塞列表的弹出
17.BLOP O(1) ---阻塞列表的循环
```text
redis 127.0.0.1:6379> LPUST country "china"
(error) ERR unknown command 'LPUST'
redis 127.0.0.1:6379> LPUSH country "china"
(integer) 1
redis 127.0.0.1:6379> LRANGE country 0 1
1) "china"
redis 127.0.0.1:6379> LPUSH country "usa"
(integer) 2
redis 127.0.0.1:6379> LRANGE country 0 2
1) "usa"
2) "china"
redis 127.0.0.1:6379> LPUSH country "us" "russia"
(integer) 4
redis 127.0.0.1:6379> LRANGE country 0 4
1) "russia"
2) "us"
3) "usa"
4) "china"
redis 127.0.0.1:6379> LRANGE country 0 5
1) "russia"
2) "us"
3) "usa"
4) "china"
redis 127.0.0.1:6379> LRANGE country 0 3
1) "russia"
2) "us"
3) "usa"
4) "china"
redis 127.0.0.1:6379> LRANGE country 0 2
1) "russia"
2) "us"
3) "usa"
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "russia"
2) "us"
3) "usa"
4) "china"
redis 127.0.0.1:6379> RPUSH country "claire"
(integer) 5
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "russia"
2) "us"
3) "usa"
4) "china"
5) "claire"
redis 127.0.0.1:6379> LPOP country
"russia"
redis 127.0.0.1:6379> RPOP country
"claire"
redis 127.0.0.1:6379> RPOPLPUSH country dest
"china"
redis 127.0.0.1:6379> LRANGE dest 0 -1
1) "china"
redis 127.0.0.1:6379> LRANGE country 0 -1
1) "us"
2) "usa"
redis 127.0.0.1:6379> RPUSH country "claire"
(integer) 3
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "us"
2) "usa"
3) "claire"
redis 127.0.0.1:6379> RPOPLPUSH  country country
"claire"
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "claire"
2) "us"
3) "usa"
redis 127.0.0.1:6379> RPOPLPUSH  country country
"usa"
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "usa"
2) "claire"
3) "us"
redis 127.0.0.1:6379> RPUSH country "claire"
(integer) 4
redis 127.0.0.1:6379> LPUSH country "claire"
(integer) 5
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "claire"
2) "usa"
3) "claire"
4) "us"
5) "claire"
redis 127.0.0.1:6379> LREM country 2 claire
(integer) 2
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "usa"
2) "us"
3) "claire"
redis 127.0.0.1:6379> LLEN country
(integer) 3
redis 127.0.0.1:6379> LINDEX country 1
"us"
redis 127.0.0.1:6379> LINSERT country  BEFORE "us" "newinsert"
(integer) 4
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "usa"
2) "newinsert"
3) "us"
4) "claire"
redis 127.0.0.1:6379> LSET country 1 "newset"
OK
redis 127.0.0.1:6379> LRANGE country 0  -1
1) "usa"
2) "newset"
3) "us"
4) "claire"
```


#### 集合
1.SADD key member O(N) -- 无序
2.SISMEMBER key member O(1) -- 判断是否包含成员
3.SMEMBER key O(N)  -- 查看集合所有成员
4.SPOP O(1) -- 随机移除一个集合值 
5.SRANDMEMBER key [count]  O(N)--- 随机获取一位集合值
6.SREM key member [member …] O(N) -- 移除一个或多个元素
7.SMOVE source destination member O(N) -- 移动数据元素，源和目标这边不能够相同，相同就报错，和列表不同
8.SCARD key O(1) -- 返回集合中的数量，做一些统计的时候，会需要使用
9.SSCAN key cursor [MATCH pattern] [COUNT count]
10.SINTER key [key …] O(N*M) -- 两个集合的交集 ,场景就是一些共同的轨迹，共同好友，大家都爱去的地方等等，但是他的复杂度消耗还是比较大的
11.SINTERSTORE destination key [key …] O(N * M)  -- 将上面得到的交集存到另一个集合中，这也是使用场景的一部分
12.SUNION key [key …]  O(N) -- 两个集合的并集
13.SUNIONSTORE destination key [key …] O(N) -- 保存两个集合的并集
14.SDIFF key [key …] O(N) -- 差集就是寻求两个人的不同之处
15.SDIFFSTORE destination key [key …] O(N)  -- 保存差集
```text
redis 127.0.0.1:6379> SADD collection "a" "b" "c"
(integer) 3
redis 127.0.0.1:6379> SMENBERS collection
(error) ERR unknown command 'SMENBERS'
redis 127.0.0.1:6379> SMEmBERS collection
1) "b"
2) "c"
3) "a"
redis 127.0.0.1:6379> SPOP collection
"b"
redis 127.0.0.1:6379> SMEMBERS collection
1) "c"
2) "a"
redis 127.0.0.1:6379> SRANDMEMBER collection 2
1) "a"
2) "c"
redis 127.0.0.1:6379> SMEMBERS collection
1) "c"
2) "a"
redis 127.0.0.1:6379> SREM collection a
(integer) 1
redis 127.0.0.1:6379> SMEMBERS collection
1) "c"
redis 127.0.0.1:6379> SADD collection "aaa"
(integer) 1
redis 127.0.0.1:6379> SADD collection "aa"
(integer) 1
redis 127.0.0.1:6379> SADD collection "bb"
(integer) 1
redis 127.0.0.1:6379> SADD collection "bbb"
(integer) 1
redis 127.0.0.1:6379> SMEMBERS collection
1) "bb"
2) "c"
3) "aaa"
4) "aa"
5) "bbb"
redis 127.0.0.1:6379> SMOVE collection col "aaa"
(integer) 1
redis 127.0.0.1:6379> SMEMBERS col
1) "aaa"
redis 127.0.0.1:6379> SCARD collection
(integer) 4
redis 127.0.0.1:6379> SADD col "b" "bb" "c"
(integer) 3
redis 127.0.0.1:6379> SMEMBERS col
1) "b"
2) "c"
3) "bb"
4) "aaa"
redis 127.0.0.1:6379> SMEMBERS collextion
(empty list or set)
redis 127.0.0.1:6379> SMEMBERS collection
1) "aa"
2) "bb"
3) "bbb"
4) "c"
redis 127.0.0.1:6379> SINTER collection col
1) "c"
2) "bb"
redis 127.0.0.1:6379> SUNION collection col
1) "b"
2) "bb"
3) "aaa"
4) "bbb"
5) "c"
6) "aa"
redis 127.0.0.1:6379> SDIFF collection col
1) "bbb"
2) "aa"
```

#### 有序集合
- 有序集合的使用场景大约是一些临时数据的排行，微信计步排行榜，点赞人员列表（一般最好根据点赞时间是有序的），评论点赞列表（点赞多的评论需要放在上面显示）等等

1.ZADD key score member [[score member] [score member] …] O(M*log(N)) -- 将一个或多个 member 元素及其 score 值加入到有序集 key 当中
- score 值可以是整数值或双精度浮点数
- 有序的插入序列
2.ZSCORE key member O(1) -- 查看某一项的分值
3.ZINCRBY key increment member O(log(N)) -- 给某一项的分值增加固定的量，比如点赞数加一，那这条评论的分值可能不是加一，让它加10，增加它的权重
4.ZCARD key O(1) -- 返回数量
5.ZCOUNT key min max  O(log(N)) -- 计算在指定大小中间的命中数 前后都包含
6.ZRANGE key start stop [WITHSCORES]  O(log(N)+M) --  递增排序
7.ZREVRANGE key start stop [WITHSCORES] O(log(N)+M) -- 递减排序
8.ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]  O(log(N)+M)  -- 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列
9.ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count] O(log(N)+M)  -- 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递减(从小到大)次序排列
10.ZRANK key member O(log(N)) -- 直接获取排名的位数 ，增序排
11.ZREVRANK key member O(log(N)) -- 直接获取排名的位数 ，倒序排
12.ZREM key member [member …] O(M*log(N)) -- 删除成员
13.ZREMRANGEBYRANK key start stop  O(log(N)+M) -- 移除有序集 key 中，指定排名(rank)区间内的所有成员
14.ZREMRANGEBYSCORE key min max   O(log(N)+M)  -- 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
15.ZRANGEBYLEX key min max [LIMIT offset count]  O(log(N)+M) -- 值相同情况：当有序集合的所有成员都具有相同的分值时， 有序集合的元素会根据成员的字典序（lexicographical ordering）来进行排序， 而这个命令则可以返回给定的有序集合键 key 中， 值介于 min 和 max 之间的成员
```text
合法的 min 和 max 参数必须包含 ( 或者 [ ， 其中 ( 表示开区间（指定的值不会被包含在范围之内）， 而 [ 则表示闭区间（指定的值会被包含在范围之内）。

特殊值 + 和 - 在 min 参数以及 max 参数中具有特殊的意义， 其中 + 表示正无限， 而 - 表示负无限
```
16.ZLEXCOUNT key min max  O(log(N))  -- 值相同情况：对于一个所有成员的分值都相同的有序集合键 key 来说， 这个命令会返回该集合中， 成员介于 min 和 max 范围内的元素数量
17.ZREMRANGEBYLEX key min max O(log(N)+M) --- 对于一个所有成员的分值都相同的有序集合键 key 来说， 这个命令会移除该集合中， 成员介于 min 和 max 范围内的所有元素
18.ZSCAN key cursor [MATCH pattern] [COUNT count] 
19.ZUNIONSTORE destination numkeys key [key …] [WEIGHTS weight [weight …]] [AGGREGATE SUM|MIN|MAX]  O(N)+O(M log(M)) -- 这个操作就是两个集合一起操作，最后存进一个集合里
20.ZINTERSTORE destination numkeys key [key …] [WEIGHTS weight [weight …]] [AGGREGATE SUM|MIN|MAX]  O(N*K)+O(M*log(M)) --- 交集存起来，相同的key权重会相加，这就不是简单的交集，能够保有原来的属性，也是比较重要的 
```text
redis 127.0.0.1:6379> ZADD ranks 10 "www.baidu.com" 9 "www.bing.com" 8 "www.qq.com" 1 "www.google.com"
(integer) 4
redis 127.0.0.1:6379> SMEMBERS ranks
(error) ERR Operation against a key holding the wrong kind of value
redis 127.0.0.1:6379> ZRANGE  ranks 0 -1 WITHSCORES
1) "www.google.com"
2) "1"
3) "www.qq.com"
4) "8"
5) "www.bing.com"
6) "9"
7) "www.baidu.com"
8) "10"
redis 127.0.0.1:6379> ZSCORE ranks "www.baidu.com"
"10"
redis 127.0.0.1:6379> ZINCRBY ranks 20 "www.baidu.com"
"30"
redis 127.0.0.1:6379> ZCARD ranks
(integer) 4
redis 127.0.0.1:6379> ZCOUNT ranks 9 10
(integer) 1
redis 127.0.0.1:6379> ZCOUNT ranks 9 20
(integer) 1
redis 127.0.0.1:6379> ZRANGE  ranks 0 -1 WITHSCORES
1) "www.google.com"
2) "1"
3) "www.qq.com"
4) "8"
5) "www.bing.com"
6) "9"
7) "www.baidu.com"
8) "30"
redis 127.0.0.1:6379> ZCOUNT ranks 9 30
(integer) 2
redis 127.0.0.1:6379> ZREVRANGE  ranks 0 -1 WITHSCORES
1) "www.baidu.com"
2) "30"
3) "www.bing.com"
4) "9"
5) "www.qq.com"
6) "8"
7) "www.google.com"
8) "1"
redis 127.0.0.1:6379> ZRANGEBYSCORE  ranks - +\
(error) ERR min or max is not a float
redis 127.0.0.1:6379> ZRANGEBYSCORE  ranks - +
(error) ERR min or max is not a float
redis 127.0.0.1:6379> ZRANGEBYSCORE  ranks -inf  +inf
1) "www.google.com"
2) "www.qq.com"
3) "www.bing.com"
4) "www.baidu.com"
redis 127.0.0.1:6379> ZRANGEBYSCORE  ranks -inf  1-
(error) ERR min or max is not a float
redis 127.0.0.1:6379> ZRANGEBYSCORE  ranks -inf  10
1) "www.google.com"
2) "www.qq.com"
3) "www.bing.com"
redis 127.0.0.1:6379> ZREVRANGEBYSCORE  ranks -inf  +inf
(empty list or set)
redis 127.0.0.1:6379> ZREVRANGEBYSCORE  ranks +inf  -inf
1) "www.baidu.com"
2) "www.bing.com"
3) "www.qq.com"
4) "www.google.com"
redis 127.0.0.1:6379> ZRANK ranks "www.baidu.com"
(integer) 3
redis 127.0.0.1:6379> ZREVRANK ranks "www.baidu.com"
(integer) 0
redis 127.0.0.1:6379> ZREM ranks "www.qq.com"
(integer) 1
redis 127.0.0.1:6379> ZREMRANGEBYRANK  ranks 0 1
(integer) 2
redis 127.0.0.1:6379> ZRANGE  ranks 0 -1 WITHSCORES
1) "www.baidu.com"
2) "30"
redis 127.0.0.1:6379> ZADD ranks 2 "www.test.com"
(integer) 1
redis 127.0.0.1:6379> ZADD ranks 3 "www.domain.com"
(integer) 1
redis 127.0.0.1:6379> ZRANGE  ranks 0 -1 WITHSCORES
1) "www.test.com"
2) "2"
3) "www.domain.com"
4) "3"
5) "www.baidu.com"
6) "30"
redis 127.0.0.1:6379> ZREMREANGEBSCORE  ranks 3 10
(error) ERR unknown command 'ZREMREANGEBSCORE'
redis 127.0.0.1:6379> ZREMRANGEBYSCORE  ranks 3 10
(integer) 1
redis 127.0.0.1:6379> ZRANGE  ranks 0 -1 WITHSCORES
1) "www.test.com"
2) "2"
3) "www.baidu.com"
4) "30"
redis 127.0.0.1:6379> ZZRANGEBYLEX - +
(error) ERR unknown command 'ZZRANGEBYLEX'
redis 127.0.0.1:6379> ZRANGEBYLEX - +
(error) ERR unknown command 'ZRANGEBYLEX'
redis 127.0.0.1:6379> ZADD cola 1 "a" 2 "b" 3 "c" 3 "d"
(integer) 4
redis 127.0.0.1:6379> ZRANGE  cola 0 -1 WITHSCORES
1) "a"
2) "1"
3) "b"
4) "2"
5) "c"
6) "3"
7) "d"
8) "3"
redis 127.0.0.1:6379> ZADD colb 1 "aa" 1 "bb" 1 "cccc" 3 "dddd"
(integer) 4
redis 127.0.0.1:6379> ZUNIONSTORE  cc 2 cola colb WEIGHTS 1 3
(integer) 8
redis 127.0.0.1:6379> ZRANGE  cc 0 -1 WITHSCORES
 1) "a"
 2) "1"
 3) "b"
 4) "2"
 5) "aa"
 6) "3"
 7) "bb"
 8) "3"
 9) "c"
10) "3"
11) "cccc"
12) "3"
13) "d"
14) "3"
15) "dddd"
16) "9"
redis 127.0.0.1:6379> ZINTERSTORE bb 2 cola cc
(integer) 4
redis 127.0.0.1:6379> ZRANGE  bb  0 -1 WITHSCORES
1) "a"
2) "2"
3) "b"
4) "4"
5) "c"
6) "6"
7) "d"
8) "6"
```

#### HyberLogLog  -- 用于对数值的估算 做最为简单的统计，但是并不会存储指定值
```text
HyperLogLog介绍

HyperLogLog 可以接受多个元素作为输入，并给出输入元素的基数估算值：
• 基数：集合中不同元素的数量。比如 {'apple', 'banana', 'cherry', 'banana', 'apple'} 的基数就是 3 。
• 估算值：算法给出的基数并不是精确的，可能会比实际稍微多一些或者稍微少一些，但会控制在合
理的范围之内。
HyperLogLog 的优点是，即使输入元素的数量或者体积非常非常大，计算基数所需的空间总是固定
的、并且是很小的。
在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基
数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。
但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以
HyperLogLog 不能像集合那样，返回输入的各个元素。
```

#### 地理位置  很实用 但是需要版本的支持 对地理实时性监测有要求的可以使用
- 从 >= 3.2.0版本开始，增加了对地理环境的支持，用于简单的地理搜索，对于公园几公里内有可用的出租车，这种临时数据，有不错的应用场景
1.GEOADD key longitude latitude member [longitude latitude member …] -- 添加地理位置
2.GEOPOS key member [member …] -- 获取地理位置
3.GEODIST key member1 member2 [unit] --  两个节点之间的距离
4.GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]  O(N+log(M)) -- 距离某一个坐标多少距离之内的点数据
5.GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]  O(log(N)+M) --
6.GEOHASH key member [member …]  O(log(N))  -- 返回对应的GEOHASH值，这个值在对接其他应用当中可以使用
```text
redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEODIST Sicily Palermo Catania
"166274.15156960039"

redis> GEORADIUS Sicily 15 37 100 km
1) "Catania"

redis> GEORADIUS Sicily 15 37 200 km
1) "Palermo"
2) "Catania"


redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEOPOS Sicily Palermo Catania NonExisting
1) 1) "13.361389338970184"
   2) "38.115556395496299"
2) 1) "15.087267458438873"
   2) "37.50266842333162"
3) (nil)


redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEODIST Sicily Palermo Catania
"166274.15156960039"

redis> GEODIST Sicily Palermo Catania km
"166.27415156960038"

redis> GEODIST Sicily Palermo Catania mi
"103.31822459492736"

redis> GEODIST Sicily Foo Bar
(nil)


redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEORADIUS Sicily 15 37 200 km WITHDIST
1) 1) "Palermo"
   2) "190.4424"
2) 1) "Catania"
   2) "56.4413"

redis> GEORADIUS Sicily 15 37 200 km WITHCOORD
1) 1) "Palermo"
   2) 1) "13.361389338970184"
      2) "38.115556395496299"
2) 1) "Catania"
   2) 1) "15.087267458438873"
      2) "37.50266842333162"

redis> GEORADIUS Sicily 15 37 200 km WITHDIST WITHCOORD
1) 1) "Palermo"
   2) "190.4424"
   3) 1) "13.361389338970184"
      2) "38.115556395496299"
2) 1) "Catania"
   2) "56.4413"
   3) 1) "15.087267458438873"
      2) "37.50266842333162"
      
redis> GEOADD Sicily 13.583333 37.316667 "Agrigento"
(integer) 1

redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEORADIUSBYMEMBER Sicily Agrigento 100 km
1) "Agrigento"
2) "Palermo"

redis> GEOADD Sicily 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
(integer) 2

redis> GEOHASH Sicily Palermo Catania
1) "sqc8b49rny0"
2) "sqdtr74hyu0"

```
#### 位图
1.SETBIT key offset value O(1) --- 将某一位设置为0或者1 ，默认为0
2.GETBIT key offset O(1)  -- 获取对应位上是0或者1
3.BITCOUNT key [start] [end]  O(N) -- 查询总共1的数量
- 模式：使用 bitmap 实现用户上线次数统计
- 一年有365天，共365位，只要是某一位为1 ，就表示这一天用户登录使用过，可以看出用户的使用情况
- 可以使用 SETBIT key offset value 和 BITCOUNT key [start] [end] 来实现 ，开始和结束，就是时间范畴，就可以知道对应时间范围内，用户登陆过多少天
- 即使运行 10 年，占用的空间也只是每个用户 10*365 比特位(bit)，也即是每个用户 456 字节。对于这种大小的数据来说， BITCOUNT key [start] [end] 的处理速度就像 GET key 和 INCR key 这种 O(1) 复杂度的操作一样快
4.BITPOS key bit [start] [end]   O(N)，其中 N 为位图包含的二进制位数量 -- 返回位图中第一个值为 bit 的二进制位的位置
5.BITOP operation destkey key [key …] O(N) -- 对一个或多个保存二进制位的字符串 key 进行位元操作，并将结果保存到 destkey 上  可用版本： >= 2.6.0
```text
BITOP AND destkey key [key ...] ，对一个或多个 key 求逻辑并，并将结果保存到 destkey 。
BITOP OR destkey key [key ...] ，对一个或多个 key 求逻辑或，并将结果保存到 destkey 。
BITOP XOR destkey key [key ...] ，对一个或多个 key 求逻辑异或，并将结果保存到 destkey 。
BITOP NOT destkey key ，对给定 key 求逻辑非，并将结果保存到 destkey 
```
6.BITFIELD key [GET type offset] [SET type offset value] [INCRBY type offset increment] [OVERFLOW WRAP|SAT|FAIL]  每个子命令的复杂度为 O(1)  -- 可用版本： >= 3.2.0
```text
BITFIELD 命令可以将一个 Redis 字符串看作是一个由二进制位组成的数组， 并对这个数组中储存的长度不同的整数进行访问 

BITFIELD 命令的作用在于它能够将很多小的整数储存到一个长度较大的位图中， 又或者将一个非常庞大的键分割为多个较小的键来进行储存， 从而非常高效地使用内存， 使得 Redis 能够得到更多不同的应用 —— 特别是在实时分析领域： BITFIELD 能够以指定的方式对计算溢出进行控制的能力， 使得它可以被应用于这一领域

BITFIELD 在一般情况下都是一个快速的命令， 需要注意的是， 访问一个长度较短的字符串的远端二进制位将引发一次内存分配操作， 这一操作花费的时间可能会比命令访问已有的字符串花费的时间要长

```
```text
redis 127.0.0.1:6379> SETBIT 10086 1
(error) ERR wrong number of arguments for 'setbit' command
redis 127.0.0.1:6379> SETBIT bit 10086 1
(integer) 0
redis 127.0.0.1:6379> GETBIT bit 10086
(integer) 1
redis 127.0.0.1:6379> BITCOUNT bit
(integer) 1
```

#### 数据库  -- 比较日常的语法
1.EXISTS key  O(1) -- 检查给定 key 是否存在
2.TYPE key O(1) -- 返回 key 所储存的值的类型
3.RENAME key newkey O(1)  --- 将 key 改名为 newkey
- 一个问题：如果新值有数据存在会被覆盖，这个是不被允许的
4.RENAMENX key newkey O(1) -- 对比上面的情况，就是新值不存在才会重命名，不然无效
5.MOVE key db O(1) -- 将当前数据库的 key 移动到给定的数据库 db 当中 ,如果key不存在或者目标库不存在，都会没有任何效果
6.DEL key [key …] O(N) -- 删除多个key
7.RANDOMKEY O(1) -- 从当前数据库中随机返回(不删除)一个 key
8.DBSIZE O(1) -- 返回当前数据库的 key 的数量
9.KEYS pattern  O(N)， N 为数据库中 key 的数量  -- 查找所有符合给定模式 pattern 的 key
```text
KEYS * 匹配数据库中所有 key 。
KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
KEYS h*llo 匹配 hllo 和 heeeeello 等。
KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo
```
10.SCAN cursor [MATCH pattern] [COUNT count]  增量式迭代命令每次执行的复杂度为 O(1) ， 对数据集进行一次完整迭代的复杂度为 O(N)  >= 2.8.0 
```text
SCAN 命令用于迭代当前数据库中的数据库键。
SSCAN 命令用于迭代集合键中的元素。
HSCAN 命令用于迭代哈希键中的键值对。
ZSCAN 命令用于迭代有序集合中的元素（包括元素成员和元素分值）


SCAN 会从 scan 0 开始，不断迭代，每次返回的值的数量并不确定，每次会返回下一位点的起始位置和一个列表的结果，
当返回的下一位点的值为0 时，表示已经完整遍历一遍，每次返回的值时随机的没有规律
当在遍历的过程中，如果有数据的修改或者删除，只能保证已经读取过的值不会再被读取，已经修改获取的值也只能是修改前的状态

COUNT 选项的作用就是让用户告知迭代命令， 在每次迭代中应该从数据集里返回多少元素，并非每次迭代都要使用相同的 COUNT 值

可以通过在执行增量式迭代命令时， 通过给定 MATCH <pattern> 参数来实现
redis 127.0.0.1:6379> sscan myset 0 match f*
1) "0"
2) 1) "foo"
   2) "feelsgood"
   3) "foobar"


SCAN 命令返回的每个元素都是一个数据库键。

SSCAN 命令返回的每个元素都是一个集合成员。

HSCAN 命令返回的每个元素都是一个键值对，一个键值对由一个键和一个值组成。

ZSCAN 命令返回的每个元素都是一个有序集合元素，一个有序集合元素由一个成员（member）和一个分值（score）组成
```

#### 自动过期
1.EXPIRE key seconds O(1)  为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除
```text
应用场景：
假设你有一项 web 服务，打算根据用户最近访问的 N 个页面来进行物品推荐，并且假设用户停止阅览超过 60 秒，那么就清空阅览记录(为了减少物品推荐的计算量，并且保持推荐物品的新鲜度)。

最近访问的页面记录，我们称之为『导航会话』(Navigation session)，可以用 INCR 和 RPUSH 命令在 Redis 中实现它
当用户阅览一个网页的时候
MULTI
    RPUSH pagewviews.user:<userid> http://.....
    EXPIRE pagewviews.user:<userid> 60
EXEC
```
2.EXPIREAT key timestamp O(1) 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)
```text
redis> EXPIREAT cache 1355292000     # 这个 key 将在 2012.12.12 过期
(integer) 1
```
3.TTL key O(1) -- 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live) ,当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key 的剩余生存时间
4.PERSIST key 0(1) -- 直接使其不会过期，可以本来是会过期的key,然后将它持久化
5.PEXPIRE key milliseconds O（1） -- 单位毫秒，功能和EXPIRE一样
6.PEXPIREAT key milliseconds-timestamp O(1) -- 时间戳为毫秒，功能和PEXPIREAT一样
7.PTTL O(1) -- 单位毫秒，功能和TTL一样


#### 事务
- 事务主要由一个开始标记multi 一个提交标记exec 一个回滚标记discard 
1.MULTI O(1) - 标记一个事务块的开始
2.EXEC -- 执行所有事务块内的命令
```text
# 事务被成功执行

redis> MULTI
OK

redis> INCR user_id
QUEUED

redis> INCR user_id
QUEUED

redis> INCR user_id
QUEUED

redis> PING
QUEUED

redis> EXEC
1) (integer) 1
2) (integer) 2
3) (integer) 3
4) PONG


# 监视 key ，且事务成功执行

redis> WATCH lock lock_times
OK

redis> MULTI
OK

redis> SET lock "huangz"
QUEUED

redis> INCR lock_times
QUEUED

redis> EXEC
1) OK
2) (integer) 1


# 监视 key ，且事务被打断

redis> WATCH lock lock_times
OK

redis> MULTI
OK

redis> SET lock "joe"        # 就在这时，另一个客户端修改了 lock_times 的值
QUEUED

redis> INCR lock_times
QUEUED

redis> EXEC                  # 因为 lock_times 被修改， joe 的事务执行失败
(nil)
```
3.DISCARD O(1)--取消事务，放弃执行事务块内的所有命令
4.WATCH O(1) -- 监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断
5.UNWATCH  -- 取消 WATCH 命令对所有 key 的监视
```text
redis 127.0.0.1:6379> MULTI
OK
redis 127.0.0.1:6379> SET ss "hello"
QUEUED
redis 127.0.0.1:6379> set bb "world"
QUEUED
redis 127.0.0.1:6379> EXEC
1) OK
2) OK
redis 127.0.0.1:6379> MULTI
OK
redis 127.0.0.1:6379> INCR a
QUEUED
redis 127.0.0.1:6379> INCR A
QUEUED
redis 127.0.0.1:6379> INCR a
QUEUED
redis 127.0.0.1:6379> PING
QUEUED
redis 127.0.0.1:6379> EXEC
1) (integer) 1
2) (integer) 1
3) (integer) 2
4) PONG
redis 127.0.0.1:6379> WATCH A a
OK
redis 127.0.0.1:6379> MULTI
OK
redis 127.0.0.1:6379> INCR A
QUEUED
redis 127.0.0.1:6379> INCR a
QUEUED
redis 127.0.0.1:6379> INCR a
QUEUED
redis 127.0.0.1:6379> EXEC
1) (integer) 2
2) (integer) 3
3) (integer) 4
```

#### LUA脚本
1.EVAL script numkeys key [key …] arg [arg …]  -- 从 Redis 2.6.0 版本开始，通过内置的 Lua 解释器，可以使用 EVAL 命令对 Lua 脚本进行求值
eval "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}" 2 key1 key2 first second
```text
> eval "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}" 2 key1 key2 first second
1) "key1"
2) "key2"
3) "first"
4) "second"

其中 "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}" 是被求值的 Lua 脚本，数字 2 指定了键名参数的数量， key1 和 key2 是键名参数，分别使用 KEYS[1] 和 KEYS[2] 访问，而最后的 first 和 second 则是附加参数，可以通过 ARGV[1] 和 ARGV[2] 访问它们
```
```text
redis.call() 和 redis.pcall() 两个函数的参数可以是任何格式良好(well formed)的 Redis 命令
call()  --- 脚本会停止执行，并返回一个脚本错误，错误的输出信息会说明错误造成的原因
pcall() ---  出错时并不引发(raise)错误，而是返回一个带 err 域的 Lua 表(table)，用于表示错误

两种格式意味着是两种方式进行表达式的解析：所有的 Redis 命令，在执行之前都会被分析

```
```text
eval "return redis.call('set','foo','bar')" 0  -- 不规范，没有参数表示，它违反了 EVAL 命令的语义，因为脚本里使用的所有键都应该由 KEYS 数组来传递
eval "return redis.call('set',KEYS[1],'bar')" 1 foo -- 规范

对于 EVAL 命令来说，必须使用正确的形式来传递键，才能确保分析工作正确地执行。主要是为了保证EVAL脚本能够在集群中进行执行，
而非仅仅再单节点可用即可，渐渐的，redis越来越推荐使用脚本形式来执行复杂的命令，因而最好做好规范保证

```
```text
LUA脚本的运行方式：
当 Lua 通过 call() 或 pcall() 函数执行 Redis 命令的时候，
命令的返回值会被转换成 Lua 数据结构。同样地，当 Lua 脚本在 
Redis 内置的解释器里运行时，Lua 脚本的返回值也会被转换成 
Redis 协议(protocol)，然后由 EVAL 将值返回给客户端


从 Redis 转换到 Lua ：
Redis integer reply -> Lua number / Redis 整数转换成 Lua 数字
Redis bulk reply -> Lua string / Redis bulk 回复转换成 Lua 字符串
Redis multi bulk reply -> Lua table (may have other Redis data types nested) / Redis 多条 bulk 回复转换成 Lua 表，表内可能有其他别的 Redis 数据类型
Redis status reply -> Lua table with a single ok field containing the status / Redis 状态回复转换成 Lua 表，表内的 ok 域包含了状态信息
Redis error reply -> Lua table with a single err field containing the error / Redis 错误回复转换成 Lua 表，表内的 err 域包含了错误信息
Redis Nil bulk reply and Nil multi bulk reply -> Lua false boolean type / Redis 的 Nil 回复和 Nil 多条回复转换成 Lua 的布尔值 false

从 Lua 转换到 Redis：
Lua number -> Redis integer reply / Lua 数字转换成 Redis 整数
Lua string -> Redis bulk reply / Lua 字符串转换成 Redis bulk 回复
Lua table (array) -> Redis multi bulk reply / Lua 表(数组)转换成 Redis 多条 bulk 回复
Lua table with a single ok field -> Redis status reply / 一个带单个 ok 域的 Lua 表，转换成 Redis 状态回复
Lua table with a single err field -> Redis error reply / 一个带单个 err 域的 Lua 表，转换成 Redis 错误回复
Lua boolean false -> Redis Nil bulk reply / Lua 的布尔值 false 转换成 Redis 的 Nil bulk 回复


Lua boolean true -> Redis integer reply with value of 1 / Lua 布尔值 true 转换成 Redis 整数回复中的 1
```
- Redis 也保证脚本会以原子性(atomic)的方式执行，脚本的原子性执行意味着类似于事务的模式，要么不可见，要么全部完成
- 注意脚本传输所带来不必要的带宽消耗，脚本是会被缓存再redis的，多次执行相同的脚本，就不需要多次传输脚本了，只需要指定对应脚本的sha值即可。
- 为了减少带宽的消耗， Redis 实现了 EVALSHA 命令，它的作用和 EVAL 一样，都用于对脚本求值，但它接受的第一个参数不是脚本，而是脚本的 SHA1 校验和(sum)
- Redis 保证所有被运行过的脚本都会被永久保存在脚本缓存当中，刷新缓存需要使用SCRIPT FLUSH 命令，这个命令会清空运行过的所有脚本的缓存
- SCRIPT 命令，用于对已有脚本进行管理
```text
SCRIPT FLUSH ：清除所有脚本缓存
SCRIPT EXISTS sha1 [sha1 …] ：根据给定的脚本校验和，检查指定的脚本是否存在于脚本缓存
SCRIPT LOAD script ：将一个脚本装入脚本缓存，但并不立即运行它
SCRIPT KILL ：杀死当前正在运行的脚本

redis> SCRIPT LOAD "return 'hello moto'"
"232fd51614574cf0867b83d384a5e898cfd24e5a"

redis> EVALSHA 232fd51614574cf0867b83d384a5e898cfd24e5a 0
"hello moto"

redis> SCRIPT EXISTS 232fd51614574cf0867b83d384a5e898cfd24e5a
1) (integer) 1

redis> SCRIPT FLUSH     # 清空缓存
OK

redis> SCRIPT EXISTS 232fd51614574cf0867b83d384a5e898cfd24e5a
1) (integer) 0

# 没有脚本在执行时

redis> SCRIPT KILL
(error) ERR No scripts in execution right now.

# 成功杀死脚本时

redis> SCRIPT KILL
OK
(1.30s)

# 尝试杀死一个已经执行过写操作的脚本，失败

redis> SCRIPT KILL
(error) ERR Sorry the script already executed write commands against the dataset. You can either wait the script termination or kill the server in an hard way using the SHUTDOWN NOSAVE command.
(1.69s)

```
- 随机数脚本的书写 -- 动态传入seed作为随机的种子
```text
RUBY:
RandomPushScript = <<EOF
    local i = tonumber(ARGV[1])
    local res
    math.randomseed(tonumber(ARGV[2]))
    while (i > 0) do
        res = redis.call('lpush',KEYS[1],math.random())
        i = i-1
    end
    return res
EOF

r.del(:mylist)
puts r.eval(RandomPushScript,1,:mylist,10,rand(2**32))

```
- redis无法使用全局变量，eval 'a=10' 0 ，将脚本中用到的所有变量都使用 local 关键字定义为局部变量
- LUA解释器库：
```text
base
table
string
math
debug
cjson   --- 处理json数据
cmsgpack
```
- 脚本种书写日志：redis.log(loglevel, message)
```text
loglevel:redis.LOG_DEBUG
         redis.LOG_VERBOSE
         redis.LOG_NOTICE
         redis.LOG_WARNING

```
- 流水线任务中避免使用EVALSHA

### redis持久化
- redis持久化能够保证数据的基本不丢失，方式有RDB和AOF两种，RDB主要是通过镜像的方式来实现，镜像生成的间隔可以通过参数指定
- AOF方式是通过重做日志同步，日志数量较大，这个同步时完整并且缓慢的
- 常用方法是通过镜像复制和日志同步并存，并对每次同步后日志rewrite

- SAVE O(N) 执行一个同步保存操作，将当前 Redis 实例的所有数据快照(snapshot)以 RDB 文件的形式保存到硬盘
- BGSAVE O(n) -- 异步存储数据到磁盘，一般都会使用这个方法
- BGREWRITEAOF O(N)  -- 执行一个 AOF文件 重写操作。重写会创建一个当前 AOF 文件的体积优化版本，这也是每一次数据同步完都会做的步骤，但是日志较大的时候会导致日志有一定时间的时延
- 从 Redis 2.4 开始， AOF 重写由 Redis 自行触发， BGREWRITEAOF 仅仅用于手动触发重写操作
- LASTSAVE,返回最近一次 Redis 成功将数据保存到磁盘上的时间，以 UNIX 时间戳格式表示


### 发布和订阅
- 这个功能主要是用于使用redis队列功能的时候，有生产者消费者的概念，就会存在发布和订阅的功能
- PUBLISH channel message O(N+M)  -- 将信息 message 发送到指定的频道 channel
- SUBSCRIBE channel [channel …] O(N) --- 订阅给定的一个或多个频道的信息
- PSUBSCRIBE pattern [pattern …]  O(N) -- 订阅一个或多个符合给定模式的频道，和上面的订阅相差一个模式和频道，就是相对专门的多方订阅，这是一个很好地办法，而不用修改订阅的具体频道
 psubscribe news.* tweet.*
- UNSUBSCRIBE [channel [channel …]] 取消订阅 O(N)
- PUNSUBSCRIBE [pattern [pattern …]] 取消订阅0(N+M)
- PUBSUB <subcommand> [argument [argument …]] 可用版本：>= 2.8.0 PUBSUB 是一个查看订阅与发布系统状态的内省命令
可以做一些统计方面的功能
```text
PUBSUB CHANNELS [pattern] O(N):列出当前的活跃频道,活跃频道指的是那些至少有一个订阅者的频道， 订阅模式的客户端不计算在内。
PUBSUB NUMSUB [channel-1 … channel-N] O(N)：返回给定频道的订阅者数量， 订阅模式的客户端不计算在内
PUBSUB NUMPAT : O(1) :  返回订阅模式的数量
```
```text
redis 127.0.0.1:6379> publish channel "hello"
(integer) 0
redis 127.0.0.1:6379> publish channel "world"
(integer) 0
redis 127.0.0.1:6379> publish channel "i am lily"
(integer) 0
redis 127.0.0.1:6379> SUBSCRIBE channel
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "channel"
3) (integer) 1
1) "message"
2) "channel"
3) "new img"
1) "message"
2) "channel"
3) "hello"
1) "message"
2) "channel"
3) "world"
1) "message"
2) "channel"
3) "i am lily"
```
### redis复制
- redis的数据有主从的概念，新加入一个节点就需要新节点去同步主节点的数据，这个再动态的情况下无法自行发现
- SLAVEOF host port  O(N) --- SLAVEOF 命令用于在 Redis 运行时动态地修改复制(replication)功能的行为，可以将当前服务器转变为指定服务器的从属服务器(slave server)
- 关闭同步复制功能：SLAVEOF NO ONE
- ROLE O(1) --- 可以是 master 、 slave 或者 sentinel


### 客户端服务端相关命令
- AUTH O(1)
- QUIT O(1)
- CLIENT GETNAME O(1) -- 获取当前连接的名字
- CLIENT SETNAME connection-name O(1)  -- 设置连接的名字
- CLIENT KILL ip:port  O(N)  -- 关闭地址为 ip:port 的客户端
- CLIENT LIST
```text
参数的含义：
以下是域的含义：

addr ： 客户端的地址和端口
fd ： 套接字所使用的文件描述符
age ： 以秒计算的已连接时长
idle ： 以秒计算的空闲时长
flags ： 客户端 flag （见下文）
db ： 该客户端正在使用的数据库 ID
sub ： 已订阅频道的数量
psub ： 已订阅模式的数量
multi ： 在事务中被执行的命令数量
qbuf ： 查询缓冲区的长度（字节为单位， 0 表示没有分配查询缓冲区）
qbuf-free ： 查询缓冲区剩余空间的长度（字节为单位， 0 表示没有剩余空间）
obl ： 输出缓冲区的长度（字节为单位， 0 表示没有分配输出缓冲区）
oll ： 输出列表包含的对象数量（当输出缓冲区没有剩余空间时，命令回复会以字符串对象的形式被入队到这个队列里）
omem ： 输出缓冲区和输出列表占用的内存总量
events ： 文件描述符事件（见下文）
cmd ： 最近一次执行的命令
客户端 flag 可以由以下部分组成：

O ： 客户端是 MONITOR 模式下的附属节点（slave）
S ： 客户端是一般模式下（normal）的附属节点
M ： 客户端是主节点（master）
x ： 客户端正在执行事务
b ： 客户端正在等待阻塞事件
i ： 客户端正在等待 VM I/O 操作（已废弃）
d ： 一个受监视（watched）的键已被修改， EXEC 命令将失败
c : 在将回复完整地写出之后，关闭链接
u : 客户端未被阻塞（unblocked）
A : 尽可能快地关闭连接
N : 未设置任何 flag
文件描述符事件可以是：

r : 客户端套接字（在事件 loop 中）是可读的（readable）
w : 客户端套接字（在事件 loop 中）是可写的（writeable）

```
- SHUTDOWN O(1)
```text
关闭流程：
     停止所有客户端
     如果有至少一个保存点在等待，执行 SAVE 命令
     如果 AOF 选项被打开，更新 AOF 文件
     关闭 redis 服务器(server)
```
- INFO O(1)
- TIME O(1) 返回当前服务器时间
```text
# 设置密码

redis> CONFIG SET requirepass redis   # 将密码设置为 secret_password
OK

redis> QUIT                                     # 退出再连接，让新密码对客户端生效

[huangz@mypad]$ redis-cli.exe

redis> PING                                     # 未验证密码，操作被拒绝
(error) ERR operation not permitted

redis> AUTH wrong                               # 尝试输入错误的密码
(error) ERR invalid password

redis> AUTH redis                     # 输入正确的密码
OK

redis> PING                                     # 密码验证成功，可以正常操作命令了
PONG


# 清空密码

redis> CONFIG SET requirepass ""   # 通过将密码设为空字符来清空密码
OK

redis> QUIT

$ redis                            # 重新进入客户端

redis> PING                        # 执行命令不再需要密码，清空密码操作成功
PONG
```
- INFO的那些参数
```text
server 部分记录了 Redis 服务器的信息，它包含以下域：

redis_version : Redis 服务器版本
redis_git_sha1 : Git SHA1
redis_git_dirty : Git dirty flag
os : Redis 服务器的宿主操作系统
arch_bits : 架构（32 或 64 位）
multiplexing_api : Redis 所使用的事件处理机制
gcc_version : 编译 Redis 时所使用的 GCC 版本
process_id : 服务器进程的 PID
run_id : Redis 服务器的随机标识符（用于 Sentinel 和集群）
tcp_port : TCP/IP 监听端口
uptime_in_seconds : 自 Redis 服务器启动以来，经过的秒数
uptime_in_days : 自 Redis 服务器启动以来，经过的天数
lru_clock : 以分钟为单位进行自增的时钟，用于 LRU 管理
clients 部分记录了已连接客户端的信息，它包含以下域：

connected_clients : 已连接客户端的数量（不包括通过从属服务器连接的客户端）
client_longest_output_list : 当前连接的客户端当中，最长的输出列表
client_longest_input_buf : 当前连接的客户端当中，最大输入缓存
blocked_clients : 正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客户端的数量
memory 部分记录了服务器的内存信息，它包含以下域：

used_memory : 由 Redis 分配器分配的内存总量，以字节（byte）为单位
used_memory_human : 以人类可读的格式返回 Redis 分配的内存总量
used_memory_rss : 从操作系统的角度，返回 Redis 已分配的内存总量（俗称常驻集大小）。这个值和 top 、 ps 等命令的输出一致。
used_memory_peak : Redis 的内存消耗峰值（以字节为单位）
used_memory_peak_human : 以人类可读的格式返回 Redis 的内存消耗峰值
used_memory_lua : Lua 引擎所使用的内存大小（以字节为单位）
mem_fragmentation_ratio : used_memory_rss 和 used_memory 之间的比率
mem_allocator : 在编译时指定的， Redis 所使用的内存分配器。可以是 libc 、 jemalloc 或者 tcmalloc 。
在理想情况下， used_memory_rss 的值应该只比 used_memory 稍微高一点儿。
当 rss > used ，且两者的值相差较大时，表示存在（内部或外部的）内存碎片。
内存碎片的比率可以通过 mem_fragmentation_ratio 的值看出。
当 used > rss 时，表示 Redis 的部分内存被操作系统换出到交换空间了，在这种情况下，操作可能会产生明显的延迟。
Because Redis does not have control over how its allocations are mapped to memory pages, high used_memory_rss is often the result of a spike in memory usage.

当 Redis 释放内存时，分配器可能会，也可能不会，将内存返还给操作系统。
如果 Redis 释放了内存，却没有将内存返还给操作系统，那么 used_memory 的值可能和操作系统显示的 Redis 内存占用并不一致。
查看 used_memory_peak 的值可以验证这种情况是否发生。
persistence 部分记录了跟 RDB 持久化和 AOF 持久化有关的信息，它包含以下域：

loading : 一个标志值，记录了服务器是否正在载入持久化文件。
rdb_changes_since_last_save : 距离最近一次成功创建持久化文件之后，经过了多少秒。
rdb_bgsave_in_progress : 一个标志值，记录了服务器是否正在创建 RDB 文件。
rdb_last_save_time : 最近一次成功创建 RDB 文件的 UNIX 时间戳。
rdb_last_bgsave_status : 一个标志值，记录了最近一次创建 RDB 文件的结果是成功还是失败。
rdb_last_bgsave_time_sec : 记录了最近一次创建 RDB 文件耗费的秒数。
rdb_current_bgsave_time_sec : 如果服务器正在创建 RDB 文件，那么这个域记录的就是当前的创建操作已经耗费的秒数。
aof_enabled : 一个标志值，记录了 AOF 是否处于打开状态。
aof_rewrite_in_progress : 一个标志值，记录了服务器是否正在创建 AOF 文件。
aof_rewrite_scheduled : 一个标志值，记录了在 RDB 文件创建完毕之后，是否需要执行预约的 AOF 重写操作。
aof_last_rewrite_time_sec : 最近一次创建 AOF 文件耗费的时长。
aof_current_rewrite_time_sec : 如果服务器正在创建 AOF 文件，那么这个域记录的就是当前的创建操作已经耗费的秒数。
aof_last_bgrewrite_status : 一个标志值，记录了最近一次创建 AOF 文件的结果是成功还是失败。
如果 AOF 持久化功能处于开启状态，那么这个部分还会加上以下域：

aof_current_size : AOF 文件目前的大小。
aof_base_size : 服务器启动时或者 AOF 重写最近一次执行之后，AOF 文件的大小。
aof_pending_rewrite : 一个标志值，记录了是否有 AOF 重写操作在等待 RDB 文件创建完毕之后执行。
aof_buffer_length : AOF 缓冲区的大小。
aof_rewrite_buffer_length : AOF 重写缓冲区的大小。
aof_pending_bio_fsync : 后台 I/O 队列里面，等待执行的 fsync 调用数量。
aof_delayed_fsync : 被延迟的 fsync 调用数量。
stats 部分记录了一般统计信息，它包含以下域：

total_connections_received : 服务器已接受的连接请求数量。
total_commands_processed : 服务器已执行的命令数量。
instantaneous_ops_per_sec : 服务器每秒钟执行的命令数量。
rejected_connections : 因为最大客户端数量限制而被拒绝的连接请求数量。
expired_keys : 因为过期而被自动删除的数据库键数量。
evicted_keys : 因为最大内存容量限制而被驱逐（evict）的键数量。
keyspace_hits : 查找数据库键成功的次数。
keyspace_misses : 查找数据库键失败的次数。
pubsub_channels : 目前被订阅的频道数量。
pubsub_patterns : 目前被订阅的模式数量。
latest_fork_usec : 最近一次 fork() 操作耗费的毫秒数。
replication : 主/从复制信息

role : 如果当前服务器没有在复制任何其他服务器，那么这个域的值就是 master ；否则的话，这个域的值就是 slave 。注意，在创建复制链的时候，一个从服务器也可能是另一个服务器的主服务器。
如果当前服务器是一个从服务器的话，那么这个部分还会加上以下域：

master_host : 主服务器的 IP 地址。
master_port : 主服务器的 TCP 监听端口号。
master_link_status : 复制连接当前的状态， up 表示连接正常， down 表示连接断开。
master_last_io_seconds_ago : 距离最近一次与主服务器进行通信已经过去了多少秒钟。
master_sync_in_progress : 一个标志值，记录了主服务器是否正在与这个从服务器进行同步。
如果同步操作正在进行，那么这个部分还会加上以下域：

master_sync_left_bytes : 距离同步完成还缺少多少字节数据。
master_sync_last_io_seconds_ago : 距离最近一次因为 SYNC 操作而进行 I/O 已经过去了多少秒。
如果主从服务器之间的连接处于断线状态，那么这个部分还会加上以下域：

master_link_down_since_seconds : 主从服务器连接断开了多少秒。
以下是一些总会出现的域：

connected_slaves : 已连接的从服务器数量。
对于每个从服务器，都会添加以下一行信息：

slaveXXX : ID、IP 地址、端口号、连接状态
cpu 部分记录了 CPU 的计算量统计信息，它包含以下域：

used_cpu_sys : Redis 服务器耗费的系统 CPU 。
used_cpu_user : Redis 服务器耗费的用户 CPU 。
used_cpu_sys_children : 后台进程耗费的系统 CPU 。
used_cpu_user_children : 后台进程耗费的用户 CPU 。
commandstats 部分记录了各种不同类型的命令的执行统计信息，比如命令执行的次数、命令耗费的 CPU 时间、执行每个命令耗费的平均 CPU 时间等等。对于每种类型的命令，这个部分都会添加一行以下格式的信息：

cmdstat_XXX:calls=XXX,usec=XXX,usecpercall=XXX
cluster 部分记录了和集群有关的信息，它包含以下域：

cluster_enabled : 一个标志值，记录集群功能是否已经开启。
keyspace 部分记录了数据库相关的统计信息，比如数据库的键数量、数据库已经被删除的过期键数量等。对于每个数据库，这个部分都会添加一行以下格式的信息：

dbXXX:keys=XXX,expires=XXX
除上面给出的这些值以外， section 参数的值还可以是下面这两个：

all : 返回所有信息
default : 返回默认选择的信息
当不带参数直接调用 INFO 命令时，使用 default 作为默认参数
```

### 配置修改等
- CONFIG SET 动态地调整 Redis 服务器的配置(configuration)而无须重启
- CONFIG GET parameter O(N)  取得运行中的 Redis 服务器的配置参数(configuration parameters)
```text
redis> CONFIG GET slowlog-max-len
1) "slowlog-max-len"
2) "1024"

redis> CONFIG SET slowlog-max-len 10086
OK

redis> CONFIG GET slowlog-max-len
1) "slowlog-max-len"
2) "10086"
```
- CONFIG RESETSTAT O(1) -- 重置 INFO 命令中的某些统计数据 主要就是针对INFO当中的参数，要求重新获取
```text
INFO的内容：
        Keyspace hits (键空间命中次数)
        Keyspace misses (键空间不命中次数)
        Number of commands processed (执行命令的次数)
        Number of connections received (连接服务器的次数)
        Number of expired keys (过期key的数量)
        Number of rejected connections (被拒绝的连接数量)
        Latest fork(2) time(最后执行 fork(2) 的时间)
        The aof_delayed_fsync counter(aof_delayed_fsync 计数器的值)

```
- CONFIG REWRITE 可用版本： >= 2.8.0  O(N)  对启动 Redis 服务器时所指定的 redis.conf 文件进行改写 直接作用到redis的启动文件中，永久的修改
这个操作是原子性的重写


### 调试命令
- PING  :如果服务器运作正常的话，会返回一个 PONG 
- ECHO message : 打印message
- OBJECT subcommand [arguments [arguments]]  ：OBJECT 命令允许从内部察看给定 key 的 Redis 对象，查看不同数据结构的底层存储
```text
字符串可以被编码为 raw (一般字符串)或 int (为了节约内存，Redis 会将字符串表示的 64 位有符号整数编码为整数来进行储存）。
列表可以被编码为 ziplist 或 linkedlist 。 ziplist 是为节约大小较小的列表空间而作的特殊表示。
集合可以被编码为 intset 或者 hashtable 。 intset 是只储存数字的小集合的特殊表示。
哈希表可以编码为 zipmap 或者 hashtable 。 zipmap 是小哈希表的特殊表示。
有序集合可以被编码为 ziplist 或者 skiplist 格式。 ziplist 用于表示小的有序集合，而 skiplist 则用于表示任何大小的有序集合
```
```text
REFCOUNT 和 IDLETIME 返回数字。 ENCODING 返回相应的编码类型。

OBJECT 命令有多个子命令：    
    OBJECT REFCOUNT <key> 返回给定 key 引用所储存的值的次数。此命令主要用于除错。
    OBJECT ENCODING <key> 返回给定 key 锁储存的值所使用的内部表示(representation)。
    OBJECT IDLETIME <key> 返回给定 key 自储存以来的空闲时间(idle， 没有被读取也没有被写入)，以秒为单位。

redis 127.0.0.1:6379> object refcount country
(integer) 1
redis 127.0.0.1:6379> object idletime country
(integer) 3578950
redis 127.0.0.1:6379> object encoding country
"ziplist"
```
- SLOWLOG -- 这个命令用于排查一些耗时的操作，在运维的过程种比较重要，用来记录查询执行时间的日志系统
```text
这个参数也可以比较灵活的使用config set来动态修改

1.记录所有查询时间大于 1000 微秒的查询：CONFIG SET slowlog-log-slower-than 1000
2.slow log 最多保存 1000 条日志：CONFIG SET slowlog-max-len 1000
3.SLOWLOG GET 打印slowlog-max-len允许范围内的多有log 
4.SLOWLOG GET number 则只打印指定数量的日志
5.SLOWLOG LEN  长度
6.SLOWLOG RESET  清空
```
-MONITOR O(N) -- 实时打印出 Redis 服务器接收到的命令，调试用
```text
redis 127.0.0.1:6379> MONITOR
OK
1555471944.710668 [0 127.0.0.1:58511] "get" "cc"
1555471959.190701 [0 127.0.0.1:58511] "get" "col"
1555471975.182375 [0 127.0.0.1:58511] "set" "newkey" "1"
1555471980.726541 [0 127.0.0.1:58511] "incr" "newkey"
1555471982.159532 [0 127.0.0.1:58511] "incr" "newkey"
1555471982.799217 [0 127.0.0.1:58511] "incr" "newkey"
```
- DEBUG OBJECT key
- DEBUG SEGFAULT : 执行一个不合法的内存访问从而让 Redis 崩溃，仅在开发时用于 BUG 模拟


### redis 内部命令
- MIGRATE host port key destination-db timeout [COPY] [REPLACE]  -- 实现键的原子性迁移 ：将 key 原子性地从当前实例传送到目标实例的指定数据库上，一旦传送成功， key 保证会出现在目标实例上，而当前实例上的 key 会被删除
- DUMP key  --- 序列化给定 key ，并返回被序列化的值，使用 RESTORE 命令可以将这个值反序列化为 Redis 键
- RESTORE key ttl serialized-value [REPLACE]  反序列化key
- SUNC : 用于复制功能(replication)的内部命令
- PSYNC master_run_id offset 用于复制功能(replication)的内部命令


### redis缓存
- Redis 保证所有被运行过的脚本都会被永久保存在脚本缓存当中 


### [功能文档](http://redisdoc.com/topic/index.html)
- 可以学到

集群规范（键分布模型，节点握手，ASK转向，容错），

持久化运作（RDB和AOF怎么执行，性能如何,如何相互作用，如何合理备份redis，做容灾），

发布订阅（简单的发布订阅），

哨兵模式（高可用模式的搭建，组织选举，主客观下线，故障转移，哨兵客户端的实现），

集群教程(数据共享，主从复制，一致性保证，重新分片，故障转移)，

键空间通知，

通信协议，

复制的原理，

事务的用法（不支持回滚，放弃事务，watch，使用check-and-set实现乐观锁）





