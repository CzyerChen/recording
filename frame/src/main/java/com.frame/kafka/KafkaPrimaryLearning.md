kafka初级教程将从以下几个方面展开：
- Kafka的概述
- Kafka的特性
- Kafka使用场景
- Kafka的安装使用（单节点、集群）
- Kafka相关名词解释
- Kafka常用命令
- Kafka核心配置


### 一、Kafka的概述
- Kakfa起初是由LinkedIn公司开发的一个分布式的消息系统，后成为Apache的一部分，它使用Scala编写
- Kafka是一个分布式消息队列，具有高性能、持久化、多副本备份、横向扩展能力的功能
- 像其他MQ类似，向生产者投递消息，消费者订阅主题，消费数据
- 消息中间件在应用当中充当解耦、异步消息投递、消息持久化、削峰这样的角色
- 越来越多的开源分布式处理系统如Cloudera、Apache Storm、Spark等都支持与Kafka集成

### 二、Kafka的特性
既然是分布式的结构，它非常突出的特性就是它的分布式特性
- 水平扩展
支持节点的灵活扩展，通过协调算法、主节点选举算法加入集群，并位置良好的集群生态

- 高吞吐率
有第一个特点作为支撑，横向节点的扩展，也使队列的消费能力、吞吐量有很大的提升，可以支撑更大的并发消息处理和持久化的能力

### 三、Kafka使用场景
- 1.日志收集：现在越来越多公司开始重视日志的管理和收集，其中Kafka也可以接收各种服务日志，进行统一消费，例如Hadoop\Hnase\Solr
- 2.消息系统：解耦生产者和消费者，是系统的交互和关联灵活，由于其持久化的支持，还可以做消息缓存
- 3.用户活动跟踪：可以将各种类型的用户数据发送到不同的topic上面，针对不同topic的数据做不同的处理和分析，可以是实时的，也可以是离线的
- 4.运营指标：收集运营指标，用以监控
- 5.流式处理：和sparkstreaming和storm配合使用
- 6.事件源：进行事件推送和消费

### 四、Kafka的安装使用（单节点、集群）
- 环境说明：
```text
操作系统：Cent OS 7

Kafka版本：kafka_2.10 (2.10在程序操作中提供了扩展功能)

JDK版本：1.8.0_171

zookeeper-3.4.10
```
- 1.安装步骤
    - 1.1 下载安装包
    - 1.2 解压安装包到指定目录
    - 1.3 Kafka内部目录：
        - /bin 操作kafka的可执行脚本，还包含windows下脚本
        - /config 配置文件所在目录
        - /libs 依赖库目录
        - /logs 日志数据目录，目录kafka把server端日志分为5种类型，分为:server,request,state，log-cleaner，controller
    - 1.4 配置
        - 配置zookeeper进入kafka安装工程根目录编辑config/server.properties
          
        - kafka最为重要三个配置依次为：broker.id、log.dir、zookeeper.connect，
          
        - 其他kafka server端config/server.properties参数说明和解释如下:
          
        - server.properties配置属性说明

- 2.运行Kafka
    - 2.1 启动：bin/kafka-server-start.sh config/server.properties &
    - 2.2 检测启动端口： netstat -tunlp| grep "(2181|9092)" ，或者通过jps查看“Kafka”进程
    - 2.3 测试联通: 通过Kafka命令建立一个生产者一个消费者，在生产者的console当中输入消息，看消费者是否有数据接收
         - bin/kafka-console-producer.sh --zookeeper bigdata:2181 --topic test
         - bin/kafka-console-consumer.sh --zookeeper bigdata:2181  --topic test --from-beginning(这个参数没有默认获取最新数据，不展示历史，可以百度查看其他设置，设置查看最新的几条)
            
```text
server.properties

# 每一个broker在集群中是唯一表示，必须是正数，不可重复，IP地址变了，broker没有变，则消息的消费也不会影响
broker.id=0                   
# broker处理消息的最大线程数，一般情况下数量为cpu核数           
num.network.threads=2
# broker处理磁盘IO的线程数，数值为cpu核数2倍
num.io.threads=8
# socket的发送缓冲区，socket的调优参数SO_SNDBUFF
socket.send.buffer.bytes=1048576
# socket的接受缓冲区，socket的调优参数SO_RCVBUFF
socket.receive.buffer.bytes=1048576
# socket请求的最大数值，防止serverOOM，message.max.bytes必然要小于socket.request.max.bytes，会被topic创建时的指定参数覆盖
socket.request.max.bytes=104857600
# kafka数据的存放地址
log.dirs=/tmp/kafka-logs
# 每个topic的分区个数，若是在topic创建时候没有指定的话会被topic创建时的指定参数覆盖，默认为1
num.partitions=2
# 数据文件保留多长时间， 存储的最大时间超过这个时间
log.retention.hours=168
# topic的分区是以一堆segment文件存储的，这个控制每个segment的大小，会被topic创建时的指定参数覆盖
log.segment.bytes=536870912
# 文件大小检查的周期时间，是否处罚 log.cleanup.policy中设置的策略
log.retention.check.interval.ms=60000
# 是否开启日志清理
log.cleaner.enable=false
# zookeeper集群的地址
zookeeper.connect=localhost:2181
```
更多说明：https://blog.csdn.net/lizhitao/article/details/25667831


   
- 3.以上使单机的安装和测试，关于集群的搭建
    - 3.1 需要修改server.properties文件
    ```text
    broker.id=1(这个ID是每一个服务节点的标识，只要都不一样就可以了)
    listeners=PLAINTEXT://bigdata:9092
    zookeeper.connect=bigdata:2181
    ```
   - 3.2 在多个节点上分别启动zookeeper和Kafka
    ```text
    bin/kafka-server-start.sh ../config/server.properties 
    ```
   - 3.3 关于测试是一样的，选择一个存在的topic，然后在一个节点启动生产者，在另外多个节点同时订阅，查看是否能够收到消息
   - 3.4 关于启动失败，请查看log下面的日志
 
### 五、Kafka相关名词解释
- Broker : Kafka集群中服务器节点的标识

- Topic: kafka是面向topic,每发送一条消息到Kafka都需要发送到一个对应的topic

- Partition: partition是物理的概念，每一个topic包含几个partition，Kafka分配的单位是partition，对应的实际的存储

- Producer：消息生产者，负责发送数据到Kafka topic

- Consumer ：消息消费者，负责从Kafka topic 上读取数据

- Consumer Group：high-level consumer API 中，每个 consumer 都属于一个 consumer group，每条消息只能被 consumer group 中的一个 Consumer 消费，但可以被多个 consumer group 消费

- replica：partition 的副本，保障 partition 的高可用

- leader：replica 中的一个角色， producer 和 consumer 只跟 leader 交互

- follower：replica 中的一个角色，从 leader 中复制数据

- controller：kafka 集群中一个控制服务器，用来进行 leader election 以及 各种 failover

- zookeeper：kafka 通过 zookeeper 来存储集群的 meta 信息，用于节点的协调管控


### 六、Kafka常用命令
- 启动kafka
```text
nohup bin/kafka-server-start.sh config/server.properties > /dev/null 2>&1 &
```
- topic API
```text
# 创建topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic test

# 查看所有topic
bin/kafka-topics.sh --list --zookeeper localhost:2181

# 删除topic
bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic test
注节点删除有坑：
匹配中delete.topic.enable=true才可以通过以上命令删除，因为开启了真删除
如果没有开启，list所有topic会显示待删除，需要手动删除删除kafka存储目录（server.properties文件log.dirs配置，默认为"/tmp/kafka-logs"）相关topic目录

# 查看topic详情
bin/kafka-topics.sh --zookeeper localhost:2181 --topic test --describe
```
- console API
```text
#创建console生产者
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

#创建console消费者
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic middleware --from-beginning
```
- 查看某个topic的message数量
```text
./bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic consumer-send
```

- Consumer Group API
```text
# 查看consumer Group列表
./bin/kafka-consumer-groups.sh  --list  --bootstrap-server localhost:9092

# 查看指定group.id的消费情况
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-1 --describe

# 删除group
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test-1 --delete

# 重置offset,group不能active
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test_4 --reset-offsets -to-offset 100 --topic consumer-send --execute

# 导出offset
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group test_4 --reset-offsets -to-offset 100 --topic consumer-send --export > 1.txt


```

### 七、Kafka核心配置
看了大神的博客，相当详细，很清晰：https://www.cnblogs.com/wangzhuxing/p/10111831.html


