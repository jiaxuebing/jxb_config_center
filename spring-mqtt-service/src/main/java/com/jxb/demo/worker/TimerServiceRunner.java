package com.jxb.demo.worker;

import com.jxb.demo.constant.TimerConstant;
import com.jxb.demo.entity.TimerEntity;
import com.jxb.demo.timer.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TimerServiceRunner implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(TimerServiceRunner.class);

    @Resource
    private FloatTaskFactory floatTaskFactory;

    @Override
    public void run(String... args) throws Exception {
        logger.info("#####[TimerServiceRunner]####start####");
        List<TimerEntity> entityList = new ArrayList<>();
        //添加智慧电梯的周期任务
        TimerEntity tokenEntity = floatTaskFactory.floatTaskFactory(TimerConstant.FLOAT_ACCESS_TOKE,0,1410, TimeUnit.MINUTES);
        entityList.add(tokenEntity);
        TimerEntity openEntity = floatTaskFactory.floatTaskFactory(TimerConstant.FLOAT_REALDATA_OPEN,5,110, TimeUnit.SECONDS);
        entityList.add(openEntity);
        TimerEntity getEntity = floatTaskFactory.floatTaskFactory(TimerConstant.FLOAT_REALDATA_GET,5,3, TimeUnit.SECONDS);
        entityList.add(getEntity);
        TimerService.addTimerTask(entityList);
        logger.info("#####[TimerServiceRunner]####end####");
    }

}
