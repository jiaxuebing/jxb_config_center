package com.jxb.demo.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * @desc httpService http相关服务
 * @author jiaxuebing
 * @date 2020-05-26
 */
public class HttpService {


    /**
     * @desc 创建httpClient连接池
     * @return
     */
    @Bean("poolingHttpClientConnectionManager")
    public PoolingHttpClientConnectionManager getConnectionManager(){
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(1000);
        cm.setDefaultMaxPerRoute(150);
        return cm;
    }

    //设置requestConfig 请求配置
    //设置retryHandler重试机制
    //设置keepalive保活策略
    //设置回收空闲连接策略
    //实现多线程执行的httpClient

    @Bean("httpRequestRetryHandler")
    public HttpRequestRetryHandler getHttpRequestRetryHandler(){
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

            @Override
            public boolean retryRequest(IOException exception,int executionCount,HttpContext context) {
                if (executionCount >= 5) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        return retryHandler;
    }


    @Bean("httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("poolingHttpClientConnectionManager")PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                                  @Qualifier("requestConfig")RequestConfig requestConfig,
                                                  @Qualifier("httpRequestRetryHandler")HttpRequestRetryHandler httpRequestRetryHandler){
      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
              .setConnectionManager(poolingHttpClientConnectionManager)
              .setDefaultRequestConfig(requestConfig)
              .setRetryHandler(httpRequestRetryHandler);
      return httpClientBuilder;
    }

    @Bean("requestConfig")
    public RequestConfig getRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(10000)
                .setSocketTimeout(300000).build();
        return requestConfig;
    }


    @Bean("httpClient")
    public CloseableHttpClient getHttpClient(@Qualifier("httpClientBuilder")HttpClientBuilder httpClientBuilder){
      CloseableHttpClient httpClient =  httpClientBuilder.build();
      return httpClient;
    }

}
