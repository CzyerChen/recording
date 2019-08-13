> 今天突然想装一下datagrip，国外的网站不可访问，国内的网站又是陷阱很多

> 我有的一个全家桶licence只能适用于2018的版本，因而尝试下载了一下,顺便和一直在用的Navicat Premium对比一下

### 一、针对MySQL 说说Mysql Workbench  VS  Navicat Premuim
- 如果是针对于MYSQL ，Mysql Workbench  VS  Navicat Premuim都是很好用的工具，但也略有区别
- Mysql Workbench：
```text
1.创建数据库时,流程化操作，先建数据库,再给用户赋权限 

2.创建表时,内容详细，容易操作; 

3.方便的进行数据库导出导入操作; 

4.数据访问异常，能够捕捉到; 

5.是一款针对MySQL的客户端可视化工具，自然问题还是不多的

6.但是，在数据过多的时候，不能够很好的利用分页来查看数据，这个可能就麻烦一些，不过一般也不会一次性要看这么多数据

```
- Navicat Premium:
```text
使用上，我个人觉得都比较简单清晰，没有太多问题，反正主要用SQL，导入导出也能支持很多类型，并且能支持很多类型数据库连接
```

------------------------------------------------------
### 二、除了MySQL Navicat Premuim VS Datagrip
- DataGrip是Jetbrains的产品，IDEA是众所周知的一流Java编辑器，有很好的用户体验，那么同一个公司的产品怎么样呢？
- 两个产品各有偏向，可以说习惯SQL选择Datagrip, 习惯可视化操作的选择Navicat Premium，两个都很好用
- DataGrip（抽取差异的地方）:
```text
优：
1.丰富的SQL提示
2.清晰的工作区，数据库schema区，sql文件区
3.专业的explain命令展示（这是SQL优化很重要的命令）
4.包含SQL语法的检查
5.相对便宜。。。

缺：
1.缺乏界面化操作的视图创建的入口，只能通过SQL
2.缺乏模型和表结构的转化可视化
3.导入功能差，导出性能优，但是功能单一

```
- Navicat Premium（抽取差异的地方）：
```text
缺：
1.相对不足的SQL提示
2.sql外部存储，每次需要手动打开
3.简单的explain命令展示
4.不含有SQL语法检查
5.相对贵。。。

优：
1.可以简单通过点击创建视图，可以不用在意SQL书写
2.简单的模型和表结构的转化可视化，清晰看得到表之间的关联
3.能够支持多表、选字段导出，很好的支持导入
```

------------------------------------------------------
### 三、翻译一个[外文文件](https://tableplus.io/blog/2018/10/navicat-vs-datagrip-vs-tableplus.html)
- 加上了新认识的一个漂亮的客户端工具TablePlus，[使用说明](https://www.waerfa.com/tableplus-review),[使用说明](https://www.v2ex.com/t/449763)
- 这个外文博客比较客观和全面地对三种工具做出了比较
------------------------------------------------------
#### Navicat vs DataGrip vs TablePlus 简单比较
在这里，我们将在三个数据库可视化客户端之间做一个快速的比较：Navicat, DataGrip, 和 TablePlus, 包括每一个工具的优点和缺点。
总体概括
- Navicat 是一个为MYSQL/MariaDB/MongoDB/Oracle/SQLite/Sql Server的一个图形化数据库管理和设计系列工具
- Datagrip 是一个多引擎数据库综合开发工具，它是由JetBrains 公司设计的，用来帮助开发者智能的执行查询，并且能够展示高效的schema导览
- TablePlus是一个时尚的、MACOS本地化的可视化客户端，能够让开发者和DBA同时登录、查询、编辑、管理不同的数据库
------------------------------------------------------
##### 一、平台上
- Navicat 是跨平台的，能够在Mac, Windows, Linux上运行，也同样有IOS版本
- DataGrip 是跨平台的，能够在macOS, Linux, 和Windows上运行
- Tableplus 刚开始是只在Mac本地化环境中运行，最新发布了Windows平台的版本
------------------------------------------------------
##### 二、支持的驱动
- Navicat支持以下数据库：MySQL, MariaDB, MongoDB, Oracle, SQLite, PostgreSQL, 和 Microsoft SQL Server；
- Datagrip 支持基本所有的数据库，包括：Postgres, MySQL, Oracle, SQL Server, Azure, Redshift, SQLite, DB2, H2, Sybase, Exasol, Derby, MariaDB, HyperSQL, Clickhouse；
- TablePlus 支持几乎所有热门的关系型数据库：MySQL, PostgreSQL, SQLite, Microsoft SQL Server, Amazon Redshift, MariaDB, CockroachDB, Vertica, Oracle，同样也支持Nosql数据库：Cassandra 和 Redis
------------------------------------------------------
##### 三、价格
- Navicat 是一个商业化的应用，有14天的试用期，有很多不同等级的价格，你可以买一个专为MYSQL的版本，非商业版本价格是119美金，标准版本价格是199美金，企业版本价格是299美金，或者你也可以购买Navicat Premium，能够支持所有支持的驱动，企业版本的价格是1299美金，非商业化版本是599美金
- Datagrip 同样也是商业化的产品，有30天的试用期，订购服务如下：个人用户的价格是8.9美金/月，企业用户的价格是19.9美金/月/人
- TablePlus 有免费版本，有一系列基础功能，你可以官网下载并永久使用，但是只能同时打开两个可用的tabs/连接/filters。如果要消除这些限制，证书价格是49美金
------------------------------------------------------

##### 四、分别说明
1.**Navicat**:提供一个强大的数据开发和管理

优点：
- 跨平台，支持多种数据库驱动
- 数据和结构的同步
- 可视化的查询和结果
- 优秀的导入导出功能
- 支持很多语言，包括波兰语、俄罗斯语、日文、葡萄牙语、汉语、简体中文、繁体中文、西班牙语、法语和英语
- 能够与其他Navicat产品兼容：Navicat Monitor, Navicat Data Modeler, Navicat Report Viewer and Navicat Data Model Essentials for enhanced features, such as analytics

缺点：
- 比较昂贵，一个单机对于大部分标准的MYSQL的证书就已经需要花费299美金，如果你想用不止一个数据库驱动，类似PostgreSQL, SQL Server, 或者 SQLite,你将需要花费1299美金购买Navicat premium包
- 它是大部分资源密集工具之一，它是基于Java开发的，会比较重，运行将消耗大部分内存
- 很少的快捷键

------------------------------------------------------
2.**DataGrip**:提供简化SQL编写并且使整个过程更高效的健壮工具

优点：
- 提供多数据库驱动
- 拥有智能的上下文敏感和编码语法提示
- 拥有可视化的表格编辑，你可以添加、删除、编辑和克隆数据行
- 提供版本控制支持
- 重构支持（主要是会给你SQL优化的建议，使你能够重构sql）

缺点：
- 用户体验并不十分友好，你需要花费一些时间去学习如何使用它
- 和相似的工具比较，它运行并不迅速，当处理大容量数据库的时候会比较缓慢，将耗费GB等级的内存
- （补充：听说导入导出功能并不完善）

------------------------------------------------------
3.**TablePlus**:通过本地化构建，Tableplus是一个强大并且轻量级的关系型数据库查询、编辑和管理工具

优点：
- Mac的本地化构建，相当快速、轻量级、稳定，能够在半秒之内就启动
- 简洁的交互界面
- 多条件数据过滤（能够通过勾选，选择需要查看的字段）
- 流式的结果和异步的加载，能够快速地展示查询结果，并且也不会阻塞界面展示
- 能够非常快速地对表格数据和结构进行内嵌修改，你可以在查询结果上直接进行修改
- 智能的查询编辑器，能够有高亮语法、即时自动补全、SQL格式化、查询历史和关键词绑定的收藏
- 是一个插件系统,能够进一步扩展应用程序
- 每一个操作都有一个相对应的快捷键

缺点：
- Linux上还没有对应的版本
- 用户管理目前还不支持
- 并没有ERD的图表特性（这个Navicat是有的）
- 免费版本，最多同时打开两个tabs/连接/filters（付费则能够去除限制）

