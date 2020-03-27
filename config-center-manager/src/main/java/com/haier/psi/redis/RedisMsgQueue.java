package com.haier.psi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * @desc 使用redis实现队列queue
 * 队列key默认使用--redisMsgQueue
 */

@Component
public class RedisMsgQueue {

    @Autowired
    private RedisTemplate redisTemplate;

    private String queueKey = "redisMsgQueue";

    /**
     * @desc 向队列添加消息
     * @param msg 消息
     */
    public void addMsg(String msg){
      redisTemplate.opsForList().leftPush(queueKey,msg);
    }

    /**
     * @desc 取出队列的元素
     * @return
     */
    public String takeMsg(){
       Object msg = redisTemplate.opsForList().rightPop(queueKey,0, TimeUnit.SECONDS);
       return msg==null?null:msg.toString();
    }

}
