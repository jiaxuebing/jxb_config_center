package com.jxb.demo.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc 任务过滤器--符合条件的推送到list
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class InfTestFilter {

    private static Logger logger = LoggerFactory.getLogger(InfTestFilter.class);

    @Autowired
    private RedisDelayQueue redisDelayQueue;

    /**
     * @desc 比较延迟任务并且进行转换，zset--list
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void filter(){
        long score = 0L;
        while(true){
            score = System.currentTimeMillis();
            redisDelayQueue.filterDelayData(score);
//            try{
//                Thread.sleep(2000);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }

}
