package com.haier.psi.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * @desc 监听队列
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class RedisListenerQueue {

    private String listenerListKey="infTest:listenerList";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @desc 添加元素到listener队列
     * @param obj 元素对象
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void add(Object obj){
        redisTemplate.opsForList().leftPush(listenerListKey,obj);
    }

    /**
     * @desc 获取监听队列数据
     * @return
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public Object getListenerData(){
       Object resultObj = redisTemplate.opsForList().rightPop(listenerListKey,0, TimeUnit.SECONDS);
       return resultObj;
    }

}
