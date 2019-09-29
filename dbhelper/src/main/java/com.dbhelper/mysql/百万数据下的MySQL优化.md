#### 优化硬件
#### 优化磁盘，检索的时候IO是很大的瓶颈
#### 优化操作系统
#### 优化应用编程接口
#### 优化MySQL，主要是修改一些默认配置
```text
back_log 如果需要大量新连接修改

thread_cache_size 如果需要大量新连接修改

key_buffer_size 索引页池，可以设成很大

bdb_cache_size BDB表使用的记录和键吗高速缓存

table_cache 如果有很多的表和并发连接修改

delay_key_write 如果需要缓存所有键码写入 设置

log_slow_queries 找出需花大量时间的查询

max_heap_table_size 用于GROUP BY

sort_buffer_size 用于ORDER BY和GROUP BY

myisam_sort_buffer_size 用于REPAIR TABLE

join_buffer_size 在进行无键吗的联结时使用

===================================
MySQL高速缓存（所有线程共享，一次性分配）

#键码缓存：key_buffer_size，默认8M。

#表缓存：table_cache，默认64。

#线程缓存：thread_cache_size，默认0。

#主机名缓存：可在编译时修改，默认128。

#内存映射表：目前仅用于压缩表

===================================
MySQL缓存区变量（非共享，按需分配）

#sort_buffer_size：ORDER BY/GROUP BY 排序会出现file sort

#record_buffer：扫描表

#join_buffer_size：无键联结

#myisam_sort_buffer_size：REPAIR TABLE

#net_buffer_length:对于读SQL语句并缓存结果。

#tmp_table_size：临时结果的HEAP表大小
```
#### 对比服务器
#### 优化表：主要是索引
- 了解索引的最左缀原则，并且将辨识度最高的字段放在最左边
- 什么时候使用索引
```text
对一个键码使用>, >=, =, <, <=, IF NULL和BETWEEN

当使用不以通配符开始的LIKE

在进行联结时从另一个表中提取行时

找出指定索引的MAX()或MIN()值

一个键码的前缀使用ORDER BY或GROUP BY

```
- 什么时候不使用索引
```text
如果使用以一个通配符开始的LIKE

搜索一个索引而在另一个索引上做ORDER BY

```

#### 优化SQL！！！
- explain
#### show processlist

#### group by 优化
#### 分页优化
- 将id先过滤出来，再在子集中查询
```text
select * from news
where cate = 1 and id > (select id from news2 where cate = 1 order by id desc limit 500000,1 ) 
order by id desc 
limit 0,10
```