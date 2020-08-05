package com.jxb.demo.timer;

import com.jxb.demo.entity.TimerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @desc 时钟服务
 */
public class TimerService {

    private static Logger logger = LoggerFactory.getLogger(TimerService.class);

    //设定周期执行线程池
    private final static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(30);

    //存储设定的周期任务，暂不支持分布式
    private final static Map<String,ScheduledFuture> timerMap = new ConcurrentHashMap<>();


    /**
     * @desc 添加周期任务
     * @param entityList 实体列表
     */
    public static void addTimerTask(List<TimerEntity> entityList){
        logger.info("########[TimerService.addTimerTask]#######start######");
        if(entityList != null && entityList.size()>0){
            for(TimerEntity timerEntity:entityList){
                //创建周期任务
                ScheduledFuture timer = scheduledExecutorService.scheduleAtFixedRate(timerEntity.getTask(),timerEntity.getDelay(),
                        timerEntity.getPeriod(),timerEntity.getTimeUnit());
                timerMap.put(timerEntity.getTimerName(),timer);
            }
        }
        logger.info("########[TimerService.addTimerTask]#######end######");
    }

    /**
     * 2 删除
     * 3 停止
     */

}
