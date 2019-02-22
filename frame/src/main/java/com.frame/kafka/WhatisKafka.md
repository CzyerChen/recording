> kafka由于大数据场景的层出不穷，处于性能的出色，成为了大数据消息系统的宠儿，也让我认识了它，今天想做一个完整的学习和总结

### 一、Kafka初级教程
教程包括：
- Kafka的概述
- Kafka的特性
- Kafka使用场景
- Kafka的安装使用（单节点、集群）
- Kafka相关名词解释
- Kafka常用命令
- Kafka核心配置
- 为什么要用MQ
- 其他MQ


### 二、Kafka中级教程
教程包括：
- Kafka架构和原理
- Kafka消息存储方式
- Kafka集群Leader节点选举算法
- Partition Replication的分配
- Replication数据同步
- Kafka集群Leader节点选举算法
- 消息传输保障
- 消息去重
- Controller节点
- fail over流程
- Producer消息路由
- Consumer 和Consumer Group
- Offset的保存
- 内部网络框架
- Rebalance机制
- Kafka自带管理工具
- Kafka监控工具
- Kafka的使用DEMO

### 三、Kafka高级教程
高级教程涉及一些问题的思考：
- Kafka +事务：Kafka自身并不支持事务，因而面对事务场景，如何使用消息事务实现最终一致性方案
- Kafka消息丢失和重复的场景及解决方案分析
- 解决KafKa数据存储与顺序一致性保证
- 如何确定Kafka的分区数、key和consumer线程数
