package com.jxb.demo.http;

import com.jxb.demo.entity.HttpBaseEntity;
import com.jxb.demo.entity.HttpServiceResponse;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @desc http服务
 */
@Component
public class HttpService {

    //创建日志
    private static Logger logger = LoggerFactory.getLogger(HttpService.class);

    public static String formContentType = "application/x-www-form-urlencoded";

    public static String jsonContentType = "application/json";

    @Resource
    private CloseableHttpClient httpClient;

    /**
     * @desc 处理get请求
     * @param httpEntity
     * @return
     */
    public HttpServiceResponse<String> doGet(HttpBaseEntity httpEntity){
        HttpServiceResponse<String> httpServiceResponse = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(httpEntity.getUrl());
        //获取header参数
        Map<String,String> headerMap = httpEntity.getHeaderMap();
        //设置header
        if(headerMap != null && headerMap.size()>0){
            this.setHeader(headerMap,httpGet);
        }
        int respCode = -1;
        String errorMsg = "success";
        String respData = null;
        try{
            response = httpClient.execute(httpGet);
            respCode = response.getStatusLine().getStatusCode();
            respData = EntityUtils.toString(response.getEntity(), "UTF-8");
        }catch (Exception e){
            logger.error("######[doGet::ERROR]:{}",e);
            errorMsg = "fail";
        }finally{
            try{
                //关闭response
                closeResponse(response);
                httpServiceResponse = HttpServiceResponse.httpServiceResponseFactory(respCode,errorMsg,respData);
            }catch (Exception e){
            }

        }
        return httpServiceResponse;
    }

    /**
     * @desc 处理post请求
     * @param httpEntity
     * @return HttpServiceResponse
     */
    public HttpServiceResponse<String> doPost(HttpBaseEntity httpEntity){
        HttpServiceResponse<String> httpServiceResponse = null;
        CloseableHttpResponse response = null;
        int respCode = -1;
        String errorMsg = "success";
        String respData = null;
        HttpPost httpPost = new HttpPost(httpEntity.getUrl());
        //获取header参数
        Map<String,String> headerMap = httpEntity.getHeaderMap();
        String contentType = jsonContentType;
        //设置header
        if(headerMap != null && headerMap.size()>0){
           contentType = this.setHeader(headerMap,httpPost);
        }

        //设置content数据
        //表单提交
        try{
            Map<String,String> contentMap = httpEntity.getContentMap();
            if(contentMap != null && contentMap.size()>0){
                Set<Map.Entry<String,String>> entrySet = contentMap.entrySet();
                if(formContentType.equals(contentType)){
                    //设置表单请求数据
                    this.createUrlEncodeEntity(entrySet,httpPost);
                }else if(jsonContentType.equals(contentType)){//json提交
                    //设置json请求数据
                    this.createStringEntity(entrySet,httpPost);
                }
            }
            response = httpClient.execute(httpPost);
            respCode = response.getStatusLine().getStatusCode();
            respData = EntityUtils.toString(response.getEntity(), "UTF-8");

        }catch (Exception e){
            logger.error("########[doPost::ERROR]:{}",e);
            errorMsg = "fail";
        }finally {
            try{
                httpServiceResponse = HttpServiceResponse.httpServiceResponseFactory(respCode,errorMsg,respData);
                closeResponse(response);
            }catch (Exception e){

            }
        }
        return httpServiceResponse;
    }

    /**
     * @desc 关闭响应体释放内存
     * @param response
     * @throws IOException
     */
    private void closeResponse(CloseableHttpResponse response) throws IOException {
        if (response != null) {
            //将entity中的content消耗，释放内存
            EntityUtils.consume(response.getEntity());
            response.close();
        }
    }

    /**
     * @desc 设置json数据格式
     * @param entrySet
     * @param httpEntityEnclosingRequestBase
     */
    private void createStringEntity(Set<Map.Entry<String,String>> entrySet, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase){
        StringEntity stringEntity = null;
        for(Map.Entry<String,String> entry:entrySet){
            stringEntity = new StringEntity(entry.getValue(), "utf-8");
            stringEntity.setContentEncoding("utf-8");
        }
        httpEntityEnclosingRequestBase.setEntity(stringEntity);
    }

    /**
     * @desc 设置表单数据格式
     * @param entrySet
     * @param httpEntityEnclosingRequestBase
     * @throws UnsupportedEncodingException
     */
    private void createUrlEncodeEntity(Set<Map.Entry<String,String>> entrySet, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) throws UnsupportedEncodingException {
        List<NameValuePair> reqList = new ArrayList<>();
        for(Map.Entry<String,String> entry:entrySet){
            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(),entry.getValue());
            reqList.add(nameValuePair);
        }
        httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(reqList));
    }

    /**
     * @desc 设置header
     * @param headerMap
     * @param httpRequest
     */
    private String setHeader(Map<String,String> headerMap, HttpRequest httpRequest){
        String contentType = null;
        Set<Map.Entry<String,String>> entrySet = headerMap.entrySet();
        for(Map.Entry<String,String> entry:entrySet){
            if(httpRequest instanceof HttpGet){
                if("contentType".equals(entry.getKey())){
                    contentType = entry.getValue();
                    httpRequest.addHeader("Content-Type",contentType);
                    continue;
                }
                httpRequest.addHeader(entry.getKey(),entry.getValue());
            }else if(httpRequest instanceof HttpPost){
                if("contentType".equals(entry.getKey())){
                    contentType = entry.getValue();
                    httpRequest.setHeader("Content-Type",contentType);
                    continue;
                }
                httpRequest.setHeader(entry.getKey(),entry.getValue());
            }
        }
        return contentType;
    }

}
