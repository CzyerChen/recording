> MySQL 8 是2018年上半年的一个强音，Mysql从5.7.x版本一跃至Mysql8，发生了很多变化，下面让我们一起学习和了解以下

> 注意：从 MySQL 5.7 升级到 MySQL 8.0 仅支持通过使用 in-place 方式进行升级，并且不支持从 MySQL 8.0 降级到 MySQL 5.7（或从某个 MySQL 8.0 版本降级到任意一个更早的 MySQL 8.0 版本）。唯一受支持的替代方案是在升级之前对数据进行备份。

> 8.0内有很多C++11特性，需要gcc4.8版本以上，Rhel6系列默认gcc是4.7，如需安装需要自行检查版本,如果是centos7 安装mysql8是没有问题的

### 一、安全及账户管理方面
- MySQL8.0中默认新建用户使用caching_sha2_password，而不是原生的加密策略，可以使用缓存解决连接时的延时问题
- 不过就是因为加密方式的升级，会导致客户端由于不一致的加密方式而无法连接，可以执行服务器降级，或者客户端升级
- 可以创建的时候指定加密方式
```text
create mysql.user 'xxx'#'127.0.0.1' identified with mysql_native_password by 'xxx';
```
修改原有用户的加密方式
```text
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
```

### 二、新增角色
- 通过角色管理权限，用户可以直接通过角色的授予，来实现权限的控制，类似于oracle

### 三、用户密码
- 除了原有的密码强度，过期时间，还新增密码历史记录，不允许重复使用原有密码（社会工程学）
- 数据存储在mysql.password_history表
- mysql.user 表中多出了关于密码重用的列

### 四、InnoDB引擎的加强
- 关于自增列，计数器会将值修改计入redo log，在checkpoint的时候写入系统表，消除了以往重启实例自增列不连续的问题

### 五、B树索引
- B树索引损坏会在redo log中记录，在checkpoint的时候也会计入系统表，促进了数据恢复工作，提升了一致性

### 六、死锁检测
- 新的动态配置选项innodb_deadlock_detect可用于禁用死锁检测，默认打开
- 因而在高并发情况下，关闭性能较低的死锁检测，通过事务过期时间配置更好提升吞吐量

### 七、加密特性
支持REDO UNDO表空间加密

### 八、共享锁
- 以往的共享锁手动添加，需要手动释放
- 在新版本中，通过``select for share /select for update`，可以支持不等待和跳过锁的选项

### 九、数据字典
- InnoDB使用自己的MySQL服务层的数据字典，不再保留自己的数据字典。有利于事务的原子性。
- MySQL系统表和数据字典表现在创建在MySQL数据目录中名为mysql.ibd的单个InnoDB表空间文件中。
- 以往，这些表是在mysql数据库目录中的各个InnoDB表空间文件中创建的
- mysql schema新增表
```text
component

default_roles

global_grants

password_history

role_edges
```
- mysql schema移除表
```text
Proc

ndb_binlog_index

event
```
- 由于存储引擎层不再保留自己的数据字典。所以表的.frm文件也不存在了

### 十、DDL操作的原子性
### 十一、UNDO表空间改变
- 在线修改UNDO表空间数量
- innodb_undo_log_truncate默认被打开
- innodb_undo_tablespaces 表空间数量默认由0变为2.并且被设置为0已经不被允许
- UNDO默认名有UNDONNN变成UNDO_NNN
- innodb_rollback_segments 配置选项定义每个UNDO表空间的回滚段数量
- innodb_undo_logs 被移除了
- Innodb_available_undo_logs状态变量被移除

### 十二、BUFFER POOL的改变
-  innodb_max_dirty_pages_pct_lwm 从0变为10
-  innodb_max_dirty_pages_pct 从75到90，刷盘动作参数的改变
- INNODB自增长锁模式变成2
- 支持使用 ALTER TABLESPACE ... RENAME TO 语句为通用表空间改名
- 新配置选项innodb_dedicated_server

### 十三、REDO优化
- 日志支持并行写入，用户写入日志缓冲区
- 用户线程现在可以按照relaxed的顺序将脏页添加到flush列表中
- 系统变量增加，控制刷新REDO的CPU等待方式
- 动态修改log buffer配置项

### 十四、其他
- 提出2资源组的概念
- 工作线程可以分配给指定工作组
- 字符集的变化建议用utf8mb4代替utf8(utf8现在是utf8mb3的别名)。默认也是utf8mb4。关于这个字符集，在存二进制表情文件的时候，经常需要手动去调整这个字符集配置，现在可以默认支持了
- 优化器的增强：
    - 支持不可见索引，优化器会自动忽略不可见索引，去除索引的时候就不需要手动删除索引了
    - 支持降序索引，就是真正按照DESC的方式降序存储，而不是反向扫描
    - 支持行转列，以及可以支持多个表的不同列同时读取并作关联等
    - 支持正则表达式
    - 增加一个可加载的JSON日志记录器
    - 备份锁
    - SET命令增强
    - 命令行部分补全功能
    - 支持大量窗口函数
     - 聚合函数的窗口函数
```text
AVG()

BIT_AND()

BIT_OR()

BIT_XOR()

COUNT()

MAX()

MIN()

STDDEV_POP(), STDDEV(), STD()

STDDEV_SAMP()

SUM()

VAR_POP(), VARIANCE()

VAR_SAMP()
```
   - 非聚合函数的窗口函数
```text
CUME_DIST()

DENSE_RANK()

FIRST_VALUE()

LAG()

LAST_VALUE()

LEAD()

NTH_VALUE()

NTILE()

PERCENT_RANK()

RANK()

ROW_NUMBER()
```

### 十五、弃用的部分
- ALTER TABLESPACE 和 DROP TABLESPACE ENGINE 子句被弃用
- JSON_MERGE函数在8.0.3中被弃用,使用 JSON_MERGE_PRESERVE()代替
- SQL_CACHE和SQL_NO_CACHE子句被弃用

### 十六、移除的部分
- information Schema中的视图被改名，共涉及十张系统表的表名被修改
- 因为没有.frm文件了，sync_frm系统变量移除
- ignore_db_dirs被移除
- log_warnings被log_error_verbosity代替
- 加密相关的函数ENCODE() 和 DECODE()以及ENCRYPT()被移除
- 存储过程的ANALYSE() 语法被移除
- INFORMATION_SCHEMA.INNODB_LOCKS和INNODB_LOCK_WAITS表被移除。使用 Performance Schema.data_locks和data_lock_waits 的表代替
- innodb_support_xa系统变量被移除


学习内容来自：https://www.cnblogs.com/xiangerfer/p/8920463.html