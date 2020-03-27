package com.haier.psi.kafka.consumer;

import com.haier.psi.util.DataUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Component
public class OrderConsumer{

   private static Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

   @Resource
   private Map<String,Object> consumerConfig;

   @Resource
   private RedisTemplate redisTemplate;

    /**
     * @desc 设置kafka的consumer批量消费信息,同时设置并发消费
     * @return
     */
    @Bean("orderListenerContainerFactory")
   public KafkaListenerContainerFactory createContainerFactory(){
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerConfig));
        factory.setConcurrency(2);
        //设置批量消费
        factory.setBatchListener(true);
//        //表示被过滤的消息将被丢弃
//        factory.setAckDiscarded(true);
//        //添加消息过滤器
//        factory.setRecordFilterStrategy(new RecordFilterStrategy() {
//            @Override
//            public boolean filter(ConsumerRecord consumerRecord) {
//                String value = consumerRecord.value().toString();
//                String checkVal = value.substring(value.lastIndexOf("-")+1);
//                Integer lastOrder = Integer.parseInt(checkVal);
//                if(lastOrder%2==0){
//                  return true;//设置为true，则是要过滤掉的消息
//                }
//                return false;//false 表示要消费的消息
//            }
//        });
        //设置提交方式为手动提交
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
   }

    @KafkaListener(topics = "order-msg-queue",containerFactory = "orderListenerContainerFactory")
    public void doOrder(List<ConsumerRecord<String,String>> recordList, Acknowledgment acknowledgment){
        if(recordList!=null){
            logger.info("ListSize:{}",recordList.size());
            try{
            for(ConsumerRecord<String,String> record:recordList){
                //logger.info("接受信息：partition:{},offset:{},value:{}",record.partition(),record.offset(),record.value());
                logger.info("接受信息：partition:{},value:{}",record.partition(),record.value());
                Headers headers  = record.headers();
                //RecordHeaders headers = (RecordHeaders) record.headers();
                Iterator<Header> headerIt = headers.iterator();
                while(headerIt.hasNext()){
                    Header header = headerIt.next();
                    if("msg-code".equals(header.key())){
                        long timeout = DataUtils.bytes2Long(header.value());
                       logger.info("====[header===key:{}=====value:{}]====",header.key(),timeout);
                       Thread.sleep(6000);
                       if(record.timestamp()+ timeout < System.currentTimeMillis()){
                          logger.info("===消息:{},TTL:{}，该消息已经过期==",record.value(),timeout);
                       }
                    }
                }

            }}catch(Exception e){

            }finally{
                //提交偏移量
                acknowledgment.acknowledge();
            }

        }

    }

}
