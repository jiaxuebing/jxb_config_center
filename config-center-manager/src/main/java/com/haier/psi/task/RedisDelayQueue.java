package com.haier.psi.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @desc 使用redis构造延迟队列
 */
@Component
public class RedisDelayQueue {

    @Autowired
    private RedisTemplate redisTemplate;

    //存储延迟任务的zset的key
    private String delayZsetkey = "infTest:delayZset";

    //存储需要处理的延迟任务列表key
    private String delayListKey = "infTest:doDelayList";


    /**
     * @desc 将任务按照执行时间戳存入zset
     * @param member
     * @param score
     */
    public void add(String member,long score){
      redisTemplate.opsForZSet().add(delayZsetkey,member,score);
    }

    /**
     * @desc 过滤集合中满足score的延迟数据推送到延迟列表中
     * @param score 时间点
     * @return
     */
    public void filterDelayData(long score){
        //考虑分布式的情况需要加分布式锁
        Set<String> delaySet = redisTemplate.opsForZSet().rangeByScore(delayZsetkey,-1,score);
        //将满足条件的元素存放到list中，供其他消费者消费
        if(delaySet!=null){
            for(String delay:delaySet){
                //将满足条件的元素从zset中删除
                redisTemplate.opsForZSet().remove(delayZsetkey,delay);
                //同时将满足条件的元素添加到delayList中
                redisTemplate.opsForList().leftPush(delayListKey,delay);
            }
        }
    }

    /**
     * @desc 获取delayList数据
     * @return
     */
    public String getDelayListData(){
      String data = null;
      Object msg = redisTemplate.opsForList().rightPop(delayListKey,0, TimeUnit.SECONDS);
      data = msg==null?null:(String)msg;
      return data;
    }

    /**
     * @desc 删除集合及列表中的删除任务【保证库与redis数据一致】
     * @param member
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void delDelayData(String member){
        //将待执行集合中的任务删除
       redisTemplate.opsForZSet().remove(delayZsetkey,member);
       //同时将执行列表中未执行的任务删除
       redisTemplate.opsForList().remove(delayListKey,0,member);
    }



}
