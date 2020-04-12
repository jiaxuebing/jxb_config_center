package com.haier.psi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc 任务变动监听器
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class InfTestListener {

    private static Logger logger = LoggerFactory.getLogger(InfTestListener.class);

    @Autowired
    private RedisListenerQueue redisListenerQueue;

    @Autowired
    private RedisDelayQueue redisDelayQueue;

    /**
     * @desc 监听库中改变的任务状态，同步执行集合中的任务
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void listen(){
        Object msg = null;
        while(true){
            msg = redisListenerQueue.getListenerData();
            //新增及修改
            //redisDelayQueue.add();
            //删除
            //redisDelayQueue.delDelayData();
        }

    }

    /**
     * @desc 添加监听任务到监听队列
     * @param msg
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void addListener(Object msg){
       redisListenerQueue.add(msg);
    }




}
