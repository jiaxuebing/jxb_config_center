package com.haier.psi.config;

import com.haier.psi.consumer.RedisSubConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {


    private String orderTopic = "order-oms";

    /**
     * @desc 创建消息监听器容器
     * @param connectionFactory
     * @param orderMsgListenerAdapter
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter orderMsgListenerAdapter){
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory);
        redisMessageListenerContainer.addMessageListener(orderMsgListenerAdapter,new PatternTopic(orderTopic));
        return redisMessageListenerContainer;
    }

    @Bean(name="orderMsgListenerAdapter")
    public MessageListenerAdapter createMsgListenerAdapter(RedisSubConsumer redisSubConsumer){
        return new MessageListenerAdapter(redisSubConsumer,"doOrder");
    }


}
