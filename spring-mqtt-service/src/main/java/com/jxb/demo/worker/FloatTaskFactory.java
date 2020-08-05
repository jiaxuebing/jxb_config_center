package com.jxb.demo.worker;

import com.alibaba.fastjson.JSONObject;
import com.jxb.demo.entity.HttpBaseEntity;
import com.jxb.demo.entity.HttpServiceResponse;
import com.jxb.demo.entity.TimerEntity;
import com.jxb.demo.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @desc 智慧电梯的定时任务工厂对象
 */
@Component
public class FloatTaskFactory {

    private static Logger logger = LoggerFactory.getLogger(FloatTaskFactory.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private HttpService httpService;

    private final Map<String,Class<? extends Runnable>> floatMap = new HashMap<>();

    public FloatTaskFactory(){
        //整合电梯平台的周期任务对象
        floatMap.put("float-access-token",FloatAccessTokenTask.class);
        floatMap.put("float-realData-open",FloatOpenDataTask.class);
        floatMap.put("float-realData-get",FloatRealDataTask.class);
    }

    /**
     * @desc 获取智慧电梯平台的访问token
     */
    public class FloatAccessTokenTask implements Runnable{

        @Override
        public void run() {
            logger.info("######[FloatAccessToken task]#####start#####");
            HttpBaseEntity httpBaseEntity = new HttpBaseEntity();
            httpBaseEntity.setUrl("http://140.249.172.102:18080/oauth/tooken.do");
            Map<String,String> headerMap = new HashMap<>();
            headerMap.put("contentType",HttpService.formContentType);
            httpBaseEntity.setHeaderMap(headerMap);
            Map<String,String> contentMap = new HashMap<>();
            contentMap.put("clientId","000ad20522304a89a4b3377c2abffcb9");
            contentMap.put("clientSecret","222f482aec9a4e26b860d02925a5a272");
            httpBaseEntity.setContentMap(contentMap);
            HttpServiceResponse<String> response = httpService.doPost(httpBaseEntity);
            //获取token
            JSONObject jsonObject = JSONObject.parseObject(response.getData());
            jsonObject = jsonObject.getJSONObject("obj");
            String accessToken = jsonObject.getString("accessToken");
            Integer expires = jsonObject.getInteger("expires");
            logger.info("######accessToken:{}，expire:{}",accessToken,expires);
            redisTemplate.opsForValue().set("xingji-IoT:float:accessToken",accessToken);
            redisTemplate.expire("xingji-IoT:float:accessToken",expires, TimeUnit.SECONDS);
            logger.info("######[FloatAccessToken task]#####end#####");
        }
    }

    /**
     * @desc 打开电梯实时数据开关
     */
    public class FloatOpenDataTask implements Runnable{

        @Override
        public void run() {
            logger.info("######[FloatOpenDataTask task]#####start#####");
            HttpBaseEntity httpBaseEntity = new HttpBaseEntity();
            httpBaseEntity.setUrl("http://140.249.172.102:18080/v1.0/api/sendDevRltCmd.do");
            Map<String,String> headerMap = new HashMap<>();
            headerMap.put("contentType", HttpService.formContentType);
            String accessToken = (String)redisTemplate.opsForValue().get("xingji-IoT:float:accessToken");
            headerMap.put("api-key",accessToken);
            httpBaseEntity.setHeaderMap(headerMap);
            Map<String,String> contentMap = new HashMap<>();
            contentMap.put("code","TIM0219114001");
            contentMap.put("type","6");
            contentMap.put("state","1");
            httpBaseEntity.setContentMap(contentMap);
            HttpServiceResponse<String> response = httpService.doPost(httpBaseEntity);
            String code = JSONObject.parseObject(response.getData()).getString("code");
            logger.info("######[FloatOpenDataTask task]#####end#####");
        }
    }

    /**
     * @desc 电梯运行实时数据
     */
    public class FloatRealDataTask implements Runnable{

        @Override
        public void run() {
            String accessToken = (String)redisTemplate.opsForValue().get("xingji-IoT:float:accessToken");
            HttpBaseEntity httpBaseEntity= new HttpBaseEntity();
            httpBaseEntity.setUrl("http://140.249.172.102:18080/v1.0/api/getDevRltData.do");
            Map<String,String> headerMap = new HashMap<>();
            headerMap.put("contentType",HttpService.formContentType);
            headerMap.put("api-key",accessToken);
            httpBaseEntity.setHeaderMap(headerMap);
            Map<String,String> contentMap = new HashMap<>();
            contentMap.put("code","TIM0219114001");
            contentMap.put("type","6");
            httpBaseEntity.setContentMap(contentMap);
            HttpServiceResponse<String> response = httpService.doPost(httpBaseEntity);
            logger.info("result:{}",JSONObject.toJSONString(response));
        }
    }

    /**
     * @desc 智慧电梯工厂方法
     * @param taskName
     * @param delay
     * @param period
     * @param timeUnit
     * @return
     */
    public TimerEntity floatTaskFactory(String taskName,long delay,long period,TimeUnit timeUnit){
        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setTimerName(taskName);
        Class clazz = floatMap.get(taskName);
        try{
            //获取内部类的构造方法，非静态内部类
            Constructor[] constructors = clazz.getDeclaredConstructors();
            timerEntity.setTask((Runnable)constructors[0].newInstance(this));
            timerEntity.setDelay(delay);
            timerEntity.setPeriod(period);
            timerEntity.setTimeUnit(timeUnit);
        }catch (Exception e){
            logger.error("#####[floatTaskFactory::ERROR]#####:{}",e);
        }
        return timerEntity;
    }
}
