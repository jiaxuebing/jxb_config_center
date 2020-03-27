package com.haier.psi.consumer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisSubConsumer {

    @Resource
    private RedisTemplate redisTemplate;


    public void doOrder(String msg){
        System.out.println("接收到信息："+msg);
        redisTemplate.opsForList().rightPush("orderList",msg);
    }

}
