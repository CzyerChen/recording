package com.kafkatransaction;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 22 18:42
 */
public class TestKafkaTransaction {

  /*  public static void main(String[] args){
        //获取producer配置
        Properties properties = buildProducerProperties();
        //创建producer
        Producer<String,String> producer = new KafkaProducer<String, String>(properties);
        //初始化事务
        producer.initTransactions();
        try {
            Thread.sleep(1000);
            //开启事务
            producer.beginTransaction();
            //发送数据
            producer.send(new ProducerRecord<>("testTopic","test1"));
            //发送数据
            Future<RecordMetadata> metadataFuture = producer.send(new ProducerRecord<>("testTopic-1", "test2"));
            //提交事务
            producer.commitTransaction();
        }catch (Exception e){
         e.printStackTrace();
           producer.abortTransaction();
        }

    }
*/
    public static void main(String[] args){
        //获取producer配置
        Properties producerProperties = buildProducerProperties();
        //获取producer配置
        Properties consumerProperties = buildConsumerProperties();
        //创建producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(producerProperties);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
        //初始化事务
        producer.initTransactions();
        //订阅信息
        consumer.subscribe(Arrays.asList("consumer-topic"));
      for(;;){
          //开启事务
          producer.beginTransaction();
          //consumer获取数据
          ConsumerRecords<String, String> records = consumer.poll(500);
          try {
              Map<TopicPartition, OffsetAndMetadata> commit = new HashMap<>();
              for (ConsumerRecord record : records) {
                  //记录topic分区
                  TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                  //记录offset
                  OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset());
                  //保存topic 分区 offset
                  commit.put(topicPartition, offsetAndMetadata);
                  //发送消息
                  Future<RecordMetadata> future = producer.send(new ProducerRecord<>("consumer-send", record.value() + "sss"));
                  //向事务协调者提交消费offset
                  producer.sendOffsetsToTransaction(commit, "group-1");
                  //提交事务
                  producer.commitTransaction();
              }
          }catch (Exception e){
              e.printStackTrace();
              //回滚事务
              producer.abortTransaction();
          }
      }

    }



    public static Properties buildProducerProperties(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("retries", 2); // 重试次数
        props.put("batch.size", 100); // 批量发送大小
        props.put("buffer.memory", 33554432); // 缓存大小，根据本机内存大小配置
        props.put("linger.ms", 1000); // 发送频率，满足任务一个条件发送
        props.put("client.id", "producer-syn-2"); // 发送端id,便于统计
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("transactional.id","producer-1"); // 每台机器唯一
        props.put("enable.idempotence",true); // 设置幂等性
        return props;
    }

    public static Properties buildConsumerProperties(){
        Properties props =  new Properties();
        props.put("bootstrap.servers", "192.168.35.129:9092");
        props.put("group.id", "group-consume");
        props.put("session.timeout.ms", 30000);       // 如果其超时，将会可能触发rebalance并认为已经死去，重新选举Leader
        props.put("enable.auto.commit", "false");      // 开启自动提交
        props.put("auto.commit.interval.ms", "1000"); // 自动提交时间
        props.put("auto.offset.reset","earliest"); // 从最早的offset开始拉取，latest:从最近的offset开始消费
        props.put("client.id", "producer-syn-1"); // 发送端id,便于统计
        props.put("max.poll.records","100"); // 每次批量拉取条数
        props.put("max.poll.interval.ms","1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("isolation.level","read_committed"); // 设置隔离级别
        return props;
    }




}
