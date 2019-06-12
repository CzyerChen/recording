
### 监控工具
- [cerebro工具 ](https://github.com/lmenezes/cerebro)
- [Elaticsearch-HQ工具](https://github.com/royrusso/elasticsearch-HQ)

### 数据迁移
- [Elasticsearch-migration工具](https://github.com/medcl/elasticsearch-migration)
- [Elasticsearch-Exporter](https://github.com/mallocator/Elasticsearch-Exporter)
- [Elasticsearch-dump](https://github.com/taskrabbit/elasticsearch-dump)

### 可视化工具
#### kibana
- 从5.1用到6.7 ，看到了es全家桶在做很大的努力，功能基本能够满足需求，7.0也把xpack认证给免费了，非常看好
- 查询，图表，dsl，聚合实现，地图，报告，通知基本都有了，但是像机器学习那样的功能，也不太会用

#### elasticsearch-head
- 过于老旧，简单的查询和过滤查询功能，能够执行dsl语句，其余也就没有了
- 功能少，不好用
```text
docker pull mobz/elasticsearch-head:5
ocker run -d --name es_head -p 9100:9100 mobz/elasticsearch-head:5
```
#### dejavu
- 这个可视化工具，可以在线修改数据很方便，其余导入导出查看功能已经很普遍了，顶多就是ui正常一点
- 我觉得不好用，功能太少
```text
docker search dejavu
docker pull dejavu:latest
docker run -p 1358:1358 -d appbaseio/dejavu

测试：curl -X PUT http://localhost:9200/test

```
#### elastichd
- 由于此次尝试的版本是6.7.1,所以很遗憾，目前并没有支持到

### ik分词的安装
```text
#进入es内部
docker exec -it 容器id   /bin/bash

cd /plugins
mkdir ik
cd ik

wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.8.1/elasticsearch-analysis-ik-6.7.1.zip
unzip elasticsearch-analysis-ik-6.7.1.zip

docker restart elasticsearch

```
### [更多工具](https://blog.csdn.net/hellozhxy/article/details/81381581)
