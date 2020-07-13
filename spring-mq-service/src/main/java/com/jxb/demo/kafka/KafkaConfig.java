package com.jxb.demo.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean("consumerConfig")
    public Map<String,Object> consumerConfig(){
        Map<String,Object> propMap = new HashMap<>();
        propMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.200.17.35:9092,10.200.17.35:9093");
        propMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        propMap.put(ConsumerConfig.GROUP_ID_CONFIG,"order-queue");
        //设置最大拉取消息数
        propMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,50);
        propMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        return propMap;
    }


    @Bean
    public NewTopic createTopic(){
        return new NewTopic("order-msg-queue",2,(short)1);
    }

    /**
     * @desc 创建kafkaAdmin对象，自动检测集群是否存在topic
     * @return
     */
    @Bean("kafkaAdmin")
    public KafkaAdmin kafkaAdmin(){
      Map<String,Object> configMap = new HashMap<>();
      configMap.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"10.200.17.35:9092");
      return new KafkaAdmin(configMap);
    }

}
