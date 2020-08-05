package com.jxb.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxb.demo.constant.TimerConstant;
import com.jxb.demo.entity.HttpBaseEntity;
import com.jxb.demo.entity.HttpServiceResponse;
import com.jxb.demo.entity.TimerEntity;
import com.jxb.demo.http.HttpService;
import com.jxb.demo.timer.TimerService;
import com.jxb.demo.worker.FloatTaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/device/data")
public class DeviceController {

    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Resource
    private HttpService httpService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private FloatTaskFactory floatTaskFactory;

    @GetMapping("/test")
    public Object test(){

        HttpBaseEntity httpBaseEntity = new HttpBaseEntity();
        httpBaseEntity.setUrl("http://192.168.1.235/_webtalk/_cur/datainterface.php");
        httpBaseEntity.setMethod("post");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("contentType",HttpService.formContentType);
        httpBaseEntity.setHeaderMap(headerMap);
        Map<String,String> contentMap = new HashMap<>();
        //username="+m_username+"&password="+m_password+"&type="+m_type+"&scode="+scode1+'&value='+key
        contentMap.put("username","admin");
        contentMap.put("password","123456");
        contentMap.put("type","writedata");
        contentMap.put("scode","C_9999_DO_0000");
        contentMap.put("value","0");
        httpBaseEntity.setContentMap(contentMap);
        HttpServiceResponse<String> response = httpService.doPost(httpBaseEntity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    contentMap.put("value","1");
                    httpBaseEntity.setContentMap(contentMap);
                    httpService.doPost(httpBaseEntity);
                    try{
                        Thread.sleep(3000);
                    }catch (Exception e){

                    }
                    contentMap.put("value","0");
                    httpBaseEntity.setContentMap(contentMap);
                    httpService.doPost(httpBaseEntity);
                }
            }
        }).start();
        return response;
    }

    @GetMapping("/visit")
    public Object visit(HttpServletRequest request){
        Object result = "access data success";
        return result;
    }

    /**
     * @desc 智慧电梯--获取token
     * @return
     */
    @RequestMapping(value = "/xjFloat",method = RequestMethod.POST)
    public Object xjFloat(){
        Object result = null;
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
        result = response;
        return result;
    }

    @RequestMapping(value = "/getFloatData",method = RequestMethod.POST)
    public Object getFloatData(){
        logger.info("#######执行DeviceController.getFloatData######");
        Object result = null;
        HttpBaseEntity httpBaseEntity = new HttpBaseEntity();
        httpBaseEntity.setUrl("http://140.249.172.102:18080/v1.0/api/sendDevRltCmd.do");
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("contentType",HttpService.formContentType);
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
        if("0000".equals(code)){
            HttpBaseEntity httpBaseEntity2 = new HttpBaseEntity();
            httpBaseEntity2.setUrl("http://140.249.172.102:18080/v1.0/api/getDevRltData.do");
            Map<String,String> headerMap2 = new HashMap<>();
            headerMap2.put("contentType",HttpService.formContentType);
            headerMap2.put("api-key",accessToken);
            httpBaseEntity2.setHeaderMap(headerMap2);
            Map<String,String> contentMap2 = new HashMap<>();
            contentMap2.put("code","TIM0219114001");
            contentMap2.put("type","6");
            httpBaseEntity2.setContentMap(contentMap2);
            HttpServiceResponse<String> response2 = httpService.doPost(httpBaseEntity2);
            result = response2;
        }
        return result;
    }


}
