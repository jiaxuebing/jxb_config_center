package com.jxb.demo.service;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @desc 拨测的应用接口任务执行
 * @author jiaxuebing
 * @date 2020-05-20
 */
@Service
public class BoceAppService {

    private static Logger logger = LoggerFactory.getLogger(BoceAppService.class);

    @Resource
    private CloseableHttpClient httpClient;

    //【task-group】任务组线程池，最大线程数默认为30
    private ExecutorService groupMultiWorker = Executors.newFixedThreadPool(30);

    //【task-group-inf】任务-组-接口线程池，最大线程数默认为100
    private ExecutorService infMultiWorker = Executors.newFixedThreadPool(100);



    //实现执行的方法逻辑

    /**
     * @desc get请求处理方式
     * @return
     */
    public String doGet(){
        String result = null;

        return result;
    }

    public String doPost(){
        String result = null;
        return result;
    }

    private String doCheckPoint(){
        String result = null;
        return result;
    }

    public void doBoceWork(Integer taskId){
      //根据taskId 取出任务信息
      String groupInfo = "";

    }

    /**
     * @desc 任务组work
     */
    private class TaskGroupWork implements Runnable{
        @Override
        public void run() {

        }

    }

}
