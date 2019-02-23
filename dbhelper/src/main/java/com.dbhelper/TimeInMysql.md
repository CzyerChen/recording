很多数据库中时间字段需要一些初始值，以下将设置的学习总结

### 一、MySQL中的当前时间
```text
CURRENT_TIMESTAMP

CURRENT_TIMESTAMP()

NOW()

LOCALTIME

LOCALTIME()

LOCALTIMESTAMP

LOCALTIMESTAMP()

```

### 二、DATE & TIME
- 日期格式如下：YYYY-MM-DD HH:MM:SS[.fraction]，它可分为两部分：date部分和time部分
- date部分对应格式中的“YYYY-MM-DD”，time部分对应格式中的“HH:MM:SS[.fraction]”
- 对于Date类型的数据，如果插入时带上了time的部分，会默认被舍去
- 两者都可用来表示YYYY-MM-DD HH:MM:SS[.fraction]类型的日期
- 存储时，timestamp会转化成世界时间UTC存储，但是读取时会根据时区转换。DateTime插入后不会改变，因而timestamp处理跨时区数据相对合适
- timestamp所能存储的时间范围为：'1970-01-01 00:00:01.000000' 到 '2038-01-19 03:14:07.999999'，
  datetime所能存储的时间范围为：'1000-01-01 00:00:00.000000' 到 '9999-12-31 23:59:59.999999'
- 显示设置时间戳默认值,将explicit_defaults_for_timestamp值设置为ON即可
```text
mysql> show variables like '%explicit_defaults_for_timestamp%';
+---------------------------------+-------+
| Variable_name                   | Value |
+---------------------------------+-------+
| explicit_defaults_for_timestamp | OFF   |
+---------------------------------+-------+
row in set (0.00 sec)
```
就可以实现以下字段初始化,初始化当前时间
```text
 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```
- 如果已经打开默认值初始化，有的字段又不想要默认值，则可以显示设置为null
```text

mysql> create table test1(id int, create_time timestamp null);

对应

`create_time` timestamp NULL DEFAULT NULL

mysql> create table test2(id int,create_time timestamp default 0);

  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
```

### 其他
对于以上进行时间默认值设置的写法，注意时5.6之后版本才能支持，版本过低会报错
```text
ERROR 1067 (42000): Invalid default value for 'create_time'
```
- 一个考察自己的MySQL版本
- 一个考察explicit_defaults_for_timestamp是否打开

