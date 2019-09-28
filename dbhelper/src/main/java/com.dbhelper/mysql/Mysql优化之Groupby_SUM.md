> 最近是在看mysql, 利用到了有个group by和sum的一个简单查询，但是没想到，效率差出了天际，难以想象，因而在这里边优化边记录

### group by 为什么会慢？

1） 分组字段不在同一张表中

2） 分组字段没有建索引

3）分组字段加DESC后索引没有起作用（如何让索引起作用才是关键、且听下文分解）

4）分组字段中加函数导致索引不起作用（这种一定要避免、本文不对这种情况展开说明）

5）分组字段中含有TEXT或CLOB字段（改成VARCHAR字段）

### 怎么避免？

1. 把order by 设置为 order by null 禁止fileSort排序，explain可以看到

2. 多列索引和组合索引，同时奏效

#### 应当将查询与统计分离，优化查询语句 group by 并不会根据where的子集去分组，而是用全表

- 无法使用索引的情况下，可以在查询如果使用排序比较多的情况下，可以调整sort_buffer_size来调整排序可用的大小
- show global status like '%sort%'
- show variables like '%sort_buffer_size%';
- set global sort_buffer_size=1024*1024


#### 联合索引
- 对需要使用到的 select sum group by 的字段组建联合索引
- select c1,sum(c2) from a group by c1; 联合索引：c1,c2



#### group by 使用索引原理
- 1、group by 
    - 使用排序来读取数据，所以只能用btree索引，不能使用在hash索引的算法中
    - 因为hash索引是一种类似键值对的快速访问方式，这个对于指定某个值查询很好，但
    - 没有排序的方法，其使用的hash函数 + 碰撞冲突解决方案
- 2、当使用索引排序来查找数据时，不会在explain中extra列看到有using filesort
- 3、在group by操作完成后，还会对group出来的结果进行排序，因此如果对排序的结果,没有排序的需求，可以考虑在其后面加上order by null

#### group by 访问索引的方法
```text
group by 访问数据有两种方法：

1、边扫描边执行group操作,叫做松散索引扫描(Loose index scan)

2、先执行一个范围(range)扫描，然后在执行group 操作,叫做紧索引扫描(Tight index scan)
```

#### 松散索引扫描(Loose index scan)
- 最高效的 处理group by的方法是，直接访问相应的索引，所以不用排序就能根据索引来读取需要的数据
- 而对于如聚簇索引(cluster index),我们可以读取前面的一部分的字段索引来获取数据，而不用满足所有的列，这就叫做松散索引扫描
- 使用松散索引扫描的条件：
    - 1、查询只能针对一个单表进行操作，这个可是个致命的缺点啊，但如果where条件比较多，选出来的数据少的话，还是不用担忧的
    - 2、group by使用索引为：对聚簇索引使用前缀索引
    - 3、使用类似group by 的操作的函数有distinct函数，使用此函数时，要么在一个索引上使用，要么在group by时，其group by的字句是索引扫描，否则会引起全表扫描。
    - 4、在使用group by语句中，如果使用聚合函数max(), min()等，如果列不在groupby的列中，或不在group by 列的聚簇索引的一部分，这将会用到排序操作
    - 5、只能对整个列的值排序时使用到索引，而只有前面一部分索引不能用到排序， 如： 列 c1 char(20), index(c1(10))、这个只用了一半索引，将无法使用来对整个数据排序
- 假设我们在表t1(c1, c2, c3, c4)有聚簇索引index(c1, c2, c3)，能使用Loose index scan例子：
```text
1、SELECT c1, c2 FROM t1 GROUP BY c1, c2;
2、SELECT DISTINCT c1, c2 FROM t1;
3、SELECT c1, MIN(c2) FROM t1 GROUP BY c1;
4、SELECT c1, c2 FROM t1 WHERE c1 < const GROUP BY c1, c2;
5、SELECT MAX(c3), MIN(c3), c1, c2 FROM t1 WHERE c2 > const GROUP BY c1, c2;
6、SELECT c2 FROM t1 WHERE c1 < const GROUP BY c1, c2;
7、SELECT c1, c2 FROM t1 WHERE c3 = const GROUP BY c1, c2;
```


