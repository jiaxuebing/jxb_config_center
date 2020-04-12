package com.haier.psi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc 接口测试的消费者--消费延迟任务
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class InfTestConsumer {

    private static Logger logger = LoggerFactory.getLogger(InfTestConsumer.class);

    @Autowired
    private RedisDelayQueue redisDelayQueue;


    /**
     * @desc 消费延迟任务
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void consumeInfTestData(){
        String msg = null;
        while(true){
            //1 获取消息队列的消息
            msg = redisDelayQueue.getDelayListData();
            if(msg != null){
                //处理逻辑
                System.out.println("==============正在消费数据："+msg+"==================");
                //2 消费数据
                //3 比对是否变更信息，如果不变更添加下次执行任务时间

            }
        }
    }


}
