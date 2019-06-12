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
 