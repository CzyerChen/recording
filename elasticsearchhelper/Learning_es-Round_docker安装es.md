> 因为最近学习了docker,也就用了docker来安装部署了一下elasticsearch

> 出现的问题主要有：原生方式安装/kitematic安装，因为是学习，也是从5.1-5。5-6.8的版本的跨度，一路摸，错了再试，错了查一查

> 这边主要结合kibana，结合可视化，来对docker下的elasticsearch进行尝试

### 单节点es安装
```text
#查找指定版本
docker search elasticsearch
#获取指定版本
docker pull elasticsearch:6.7.1
#查看版本
docker images
#将内部的配置文件映射出来，方便修改
#可以在外部修改，也可以进入容器修改elasticsearch.yml
#跨域配置
cluster.name: "docker-test"
network.host: 0.0.0.0
http.cors.enabled: true
http.cors.allow-origin: "*"
discovery.zen.minimum_master_nodes: 1


#首先在本地定义一个
echo "http.host: 0.0.0.0" >> /opt/elasticsearch/config/elasticsearch.yml

#运行单节点模式
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -p 5601:5601 -e "discovery.type=single-node" -v /opt/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml -v /opt/elasticsearch/data:/usr/share/elasticsearch/data -d 362c5cb1669b（容器id）

#检查安装
curl http://127.0.0.1:9200

```
- 同时单独安装kibana，和es的步骤很相似，最好把kibana.yml的配置文件映射出来，并且配置好es的访问URL
- es需要允许跨域访问
```text
docker search kibana
docker pull kibana:6.7.1(和es版本一致)
docker run -it -d -e ELASTICSEARCH_URL=http://127.0.0.1:9200 --name kibana --network=container:elasticsearch kibana:6.7.1
# 主要注意内部通信，需要能够进行通信
server.host 默认的"0"也行 配置"0.0.0.0"也行
server.name 标示一下应用
elasticsearch.hosts ["http://127.0.0.1:9200"] 此时这边是借用网络，因而es对外是什么样的访问方式这边就怎么写 默认的elasticsearch是不行的，不是什么hostname
```
- 另外，如果es没有打开5601的端口，是由kibana自己打开的
```text
#自己打开5601端口，就是一个独立的容器
docker run --name kibana -e ELASTICSEARCH_URL=http://127.0.0.1:9200 -p 5601:5601 -d 388661dcd03e
#是独立的容器，有虚拟IP ，因而连接es就不能通过127.0.0.1的形式了，就需要通过内部的虚拟ip进行通信，相当于是两个虚拟机，是独立的
#差别在于这里
elasticsearch.hosts ["http://172.17.0.2:9200"]

```
### 一个单节点 es 和 kibana
- 用来测试玩玩还是可以，产险应该不会用单节点
- 支持的版本是6.5.4
```text
docker pull elasticsearch-kibana
docker run -d -p 9200:9200 -p 9300:9300 -p 5601:5601 --name eskibana  nshou/elasticsearch-kibana
```
- 依赖一个dockerfile
```text
FROM openjdk:jre-alpine

LABEL maintainer "nshou <nshou@coronocoya.net>"

ARG ek_version=6.5.4

RUN apk add --quiet --no-progress --no-cache nodejs wget \
 && adduser -D elasticsearch

USER elasticsearch

WORKDIR /home/elasticsearch

ENV ES_TMPDIR=/home/elasticsearch/elasticsearch.tmp ES_DATADIR=/home/elasticsearch/elasticsearch/data

RUN wget -q -O - https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-oss-${ek_version}.tar.gz \
 |  tar -zx \
 && mv elasticsearch-${ek_version} elasticsearch \
 && mkdir -p ${ES_TMPDIR} ${ES_DATADIR} \
 && wget -q -O - https://artifacts.elastic.co/downloads/kibana/kibana-oss-${ek_version}-linux-x86_64.tar.gz \
 |  tar -zx \
 && mv kibana-${ek_version}-linux-x86_64 kibana \
 && rm -f kibana/node/bin/node kibana/node/bin/npm \
 && ln -s $(which node) kibana/node/bin/node \
 && ln -s $(which npm) kibana/node/bin/npm

CMD sh elasticsearch/bin/elasticsearch -E http.host=0.0.0.0 --quiet & kibana/bin/kibana --host 0.0.0.0 -Q

EXPOSE 9200 5601
```
### 集群节点es安装
- 利用docker-compose的容器编排技术，通过配置和简单命令就能够启动一个集群
```text
version: '2'
networks:
  esnet:
services:
  es01:
    image: elasticsearch:6.7.1
    container_name: es01
    environment:
      - node.name=es01
      - cluster.name=docker-cluster-test
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - http.cors.enabled=true
      - http.cors.allow-origin=*
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata01:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
      - 9300:9300
    networks:
      - esnet
  es02:
    image: elasticsearch:6.7.1
    container_name: es02
    environment:
      - node.name=es02
      - cluster.name=docker-cluster-test
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - "discovery.zen.ping.unicast.hosts=es01"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata02:/usr/share/elasticsearch/data
    ports:
      - 9201:9200
      - 9301:9300
    networks:
      - esnet
    depends_on:
      - es01
  kibana01:
    image: kibana:6.7.1
    container_name: kibana01
    environment:
      - SERVER_NAME=kibana-cluster
      - SERVER_HOST=0.0.0.0
      - ELASTICSEARCH_HOSTS=http://es01:9200
      - XPACK_MONITORING_ENABLED=true

    ports:
      - 5601:5601
    networks:
      - esnet 
    depends_on:
      - es01
    external_links:
      - es01
volumes:
  esdata01:
    driver: local
  esdata02:
    driver: local
```
- 通过书写docker-compose.yml和利用docker-compose down/docker-compose up -d/docker-compose logs来运作，相当便利


### 安装elk,[elk文档](https://elk-docker.readthedocs.io)
- 这个就更加省力了，github上很多打好的镜像都已经放到docker hub中，因而我们通过搜索elk看到一个star数最高的，就是它了
```text
docker search elk
docker pull sebp/elk:670 ---- 我这边用的是这个版本
#接下来可以使用run 也可以使用容器编排 来运行
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -p9300:9300 -it --name elk  -d sebp/elk:670

或者docker-compose.yml
elk:
  image: sebp/elk:670
  ports:
    - "5601:5601"
    - "9200:9200"
    - "5044:5044"
    - "9300:9300"
docker-compose up -d
docker-compose down

``` 
- 配置logstash
- 选择性地启动es logstash 或者kibana
- 有一些启动环境参数可供选择和配置
```text
TZ: the container's time zone (see list of valid time zones), e.g. America/Los_Angeles (default is Etc/UTC, i.e. UTC).

ES_HEAP_SIZE: Elasticsearch heap size (default is 256MB min, 1G max)

Specifying a heap size – e.g. 2g – will set both the min and max to the provided value. To set the min and max values separately, see the ES_JAVA_OPTS below.

ES_JAVA_OPTS: additional Java options for Elasticsearch (default: "")

For instance, to set the min and max heap size to 512MB and 2G, set this environment variable to -Xms512m -Xmx2g.

ES_CONNECT_RETRY: number of seconds to wait for Elasticsearch to be up before starting Logstash and/or Kibana (default: 30)

ES_PROTOCOL: protocol to use to ping Elasticsearch's JSON interface URL (default: http)

Note that this variable is only used to test if Elasticsearch is up when starting up the services. It is not used to update Elasticsearch's URL in Logstash's and Kibana's configuration files.

CLUSTER_NAME: the name of the Elasticsearch cluster (default: automatically resolved when the container starts if Elasticsearch requires no user authentication).

The name of the Elasticsearch cluster is used to set the name of the Elasticsearch log file that the container displays when running. By default the name of the cluster is resolved automatically at start-up time (and populates CLUSTER_NAME) by querying Elasticsearch's REST API anonymously. However, when Elasticsearch requires user authentication (as is the case by default when running X-Pack for instance), this query fails and the container stops as it assumes that Elasticsearch is not running properly. Therefore, the CLUSTER_NAME environment variable can be used to specify the name of the cluster and bypass the (failing) automatic resolution.

LS_HEAP_SIZE: Logstash heap size (default: "500m")

LS_OPTS: Logstash options (default: "--auto-reload" in images with tags es231_l231_k450 and es232_l232_k450, "" in latest; see Breaking changes)

NODE_OPTIONS: Node options for Kibana (default: "--max-old-space-size=250")

MAX_MAP_COUNT: limit on mmap counts (default: system default)

Warning – This setting is system-dependent: not all systems allow this limit to be set from within the container, you may need to set this from the host before starting the container (see Prerequisites).

MAX_OPEN_FILES: maximum number of open files (default: system default; Elasticsearch needs this amount to be equal to at least 65536)

KIBANA_CONNECT_RETRY: number of seconds to wait for Kibana to be up before running the post-hook script (see Pre-hooks and post-hooks) (default: 30)

ES_HEAP_DISABLE and LS_HEAP_DISABLE: disable HeapDumpOnOutOfMemoryError for Elasticsearch and Logstash respectively if non-zero (default: HeapDumpOnOutOfMemoryError is enabled).
```
```text
sudo docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it \
    -e ES_HEAP_SIZE="2g" -e LS_HEAP_SIZE="1g" --name elk sebp/elk:670
```
- [更多配置文档](https://elk-docker.readthedocs.io)

- 在容器启动前和启动后有对应地sh脚本可以运行，其中可以配置一些环境变量
- 创建你的应用和elk一体的网络配置
- 关于elk的默认配置在/etc/logstash/conf.d下面，如果是运行的配置文件在/opt//logstash/config下面
- 除了进入容器去修改你的配置文件，比较友好的方式，可以将容器运行所需的配置文件映射到外部来，因而可以不进入容器内部来操作修改配置文件，docker run -v来映射
- 或者在dockerfile中就写明映射的位置，就不需要run的时候指定了
```text
FROM sebp/elk

ENV ES_HOME /opt/elasticsearch
WORKDIR ${ES_HOME}

RUN yes | CONF_DIR=/etc/elasticsearch gosu elasticsearch bin/elasticsearch-plugin \
    install -b ingest-geoip
```
- 考虑到上面的elk是单节点的es，那么如果我想是es的集群可以吗？其实你单独一个节点一个节点装，或者用docker-compose都是可以的，但是基于现有的elk也可以做到
- 已有的elk加上不同节点的es集群
```text
主节点
network.host: 0.0.0.0
network.publish_host: <reachable IP address or FQDN> --- 这里不是docker的内部IP

从节点
elasticsearch-slave.yml
network.host: 0.0.0.0
network.publish_host: <reachable IP address or FQDN>
discovery.zen.ping.unicast.hosts: ["elk-master.example.com"]

然后启动
sudo docker run -it --rm=true -p 9200:9200 -p 9300:9300 \
  -v /home/elk/elasticsearch-slave.yml:/etc/elasticsearch/elasticsearch.yml \
  sebp/elk
 
```
- 已有的elk加上同一节点的es集群
```text
先起一个elk
sudo docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name elk sebp/elk

然后在一个elasticsearch-slave.yml的配置文件
network.host: 0.0.0.0
discovery.zen.ping.unicast.hosts: ["elk"]

然后再起一个节点,docker run --rm=true在容器退出时就自动删除容器
sudo docker run -it --rm=true \
  -v /var/sandbox/elk-docker/elasticsearch-slave.yml:/etc/elasticsearch/elasticsearch.yml \
  --link elk:elk --name elk-slave sebp/elk

```
- 优化你的集群，安全性的考虑：以上镜像是由带xpack ，因而有安全性保障，当然也可以去除
- [一些问题说明](https://elk-docker.readthedocs.io)


#### 对于需要添加插件的情况，可以在dockerfile中进行修改
```text
FROM sebp/elk

WORKDIR ${LOGSTASH_HOME}
RUN gosu logstash bin/logstash-plugin install logstash-input-rss

```

#### 对于持久化日志文件，那就可以通过文件映射的方式，打印到本机上

### 快照和restore