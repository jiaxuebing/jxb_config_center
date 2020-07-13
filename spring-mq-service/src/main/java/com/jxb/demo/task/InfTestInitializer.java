package com.jxb.demo.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @desc 任务初始化
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class InfTestInitializer {

    private static Logger logger = LoggerFactory.getLogger(InfTestInitializer.class);

    /**
     * @desc 初始化任务到zset
     * @author jiaxuebing
     * @date 2020-04-12
     */
    public void initialize(){
        logger.info("===[InfTestInitializer.initialize]===任务初始化开始===");
        //1 从数据库获取任务相关信息
        //2 处理推送到zset中
        logger.info("===[InfTestInitializer.initialize]===任务初始化结束===");
    }



}
