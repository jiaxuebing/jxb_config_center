package com.jxb.demo.service;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoceAppTask{

    private static Logger logger = LoggerFactory.getLogger(BoceAppTask.class);

    private final CloseableHttpClient httpClient;
    private final HttpClientContext httpClientContext;
    private Integer taskId;
    private CookieStore cookieStore;


    public BoceAppTask(CloseableHttpClient closeableHttpClient,Integer taskId){
        this.httpClient = closeableHttpClient;
        this.httpClientContext = HttpClientContext.create();
        this.cookieStore = new BasicCookieStore();
        httpClientContext.setCookieStore(cookieStore);
        this.taskId = taskId;
    }

    /**
     * @desc 接口任务--分组work
     */
    private class TaskGroupWork implements Runnable{

        private final CloseableHttpClient httpClient;
        private final HttpClientContext httpClientContext;
        private String groupInfo;

        public TaskGroupWork(CloseableHttpClient httpClient,HttpClientContext httpClientContext,String groupInfo){
            this.httpClient = httpClient;
            this.httpClientContext = httpClientContext;
            this.groupInfo = groupInfo;
        }

        @Override
        public void run() {

        }
    }


}
