package com.jxb.demo.kafka.producer;

import com.jxb.demo.util.DataUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import javax.annotation.Resource;

/**
 * @desc 生产者
 */
@Component
public class LogProducer {

    private Logger logger = LoggerFactory.getLogger(LogProducer.class);

    @Resource
    private KafkaTemplate<String,Object> kafkaTemplate;

    /**
     * @desc 消息生产者异步
     * @param orderMsg
     * @param partition
     */
    public void send(String orderMsg,Integer partition){
        ListenableFuture<SendResult<String,Object>> future = kafkaTemplate.send("order-msg-queue",partition,null,orderMsg);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("消息发送失败");
                //逻辑处理
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.info("消息发送成功：");
                RecordMetadata recordMetadata = result.getRecordMetadata();
                logger.info("topic:"+recordMetadata.topic()+",partition:"+recordMetadata.partition());
            }
        });

    }

    /**
     * @desc 生产者通过Header标记特殊信息
     */
    public void kafkaMsgTtl(){
        ProducerRecord ttlRecord = new ProducerRecord<>("order-msg-queue",
                0,
                System.currentTimeMillis(),
                null,
                "msg_1",new RecordHeaders().add(new RecordHeader("msg-code", DataUtils.long2Bytes(5000))));

        ListenableFuture<SendResult<String,Object>> future = kafkaTemplate.send(ttlRecord);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                 logger.info("====消息发送成功=====");
            }
        });
    }
}
