package com.jxb.demo.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource(value = {"classpath:config/httpclient.properties"})
public class HttpClientConfig {

    private static Logger logger = LoggerFactory.getLogger(HttpClientConfig.class);

    @Value("${http.maxTotal}")
    private Integer maxTotal;//最大连接数

    @Value("${http.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute;//支持并发数

    @Value("${http.connectTimeout}")
    private Integer connectTimeout;//连接的超时时间

    @Value("${http.connectionRequestTimeout}")
    private Integer connectionRequestTimeout;//从连接池获取连接的超时时间

    @Value("${http.socketTimeout}")
    private Integer socketTimeout;//数据传输超时时间

    @Value("${http.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;//提交请求前测试连接是否可用

    private static SSLContext sslContext = null;


    static{
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                @Override
                public boolean isTrusted(X509Certificate[] xcs, String string){
                    return true;
                }
            }).build();
        } catch (KeyStoreException ex) {
            //logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            //logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (KeyManagementException ex) {
            //logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    /**
     * 实例化一个连接池管理器,并且设置最大连接数，支持并发数
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager(){
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
                .build();
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        httpClientConnectionManager.setMaxTotal(maxTotal);
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        new Thread(new IdleWork(httpClientConnectionManager)).start();
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。
     * 这里需要以参数形式注入上面实例化的连接池管理器
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager")PoolingHttpClientConnectionManager httpClientConnectionManager){
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(httpClientConnectionManager)
                .setConnectionManagerShared(true);//共享连接池
        return builder;
    }

    @Bean(name = "keepAliveStrategy")
    public ConnectionKeepAliveStrategy myStragy(){
        ConnectionKeepAliveStrategy keepAliveStrategy = null;
        keepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                Args.notNull(response, "HTTP response");
                final HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    final HeaderElement he = it.nextElement();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (final NumberFormatException ignore) {
                        }
                    }
                }
                return 30*1000;
            }
        };
        return keepAliveStrategy;
    }

    /**
     * 注入连接池，用于获取httpClient
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder")HttpClientBuilder httpClientBuilder,
                                                      @Qualifier("keepAliveStrategy")ConnectionKeepAliveStrategy keepAliveStrategy,
                                                      @Qualifier("httpRequestRetryHandler")HttpRequestRetryHandler httpRequestRetryHandler,
                                                      @Qualifier("requestConfig")RequestConfig requestConfig){
        CloseableHttpClient httpClient = httpClientBuilder
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(keepAliveStrategy)//设置保活策略
                .setRetryHandler(httpRequestRetryHandler)//设置重试策略
                .evictExpiredConnections()
                .build();
        return httpClient;
    }

    /**
     * Builder是RequestConfig的一个内部类
     * 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder(){
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout).
                setConnectionRequestTimeout(connectionRequestTimeout).
                setSocketTimeout(socketTimeout);
    }

    /**
     * 使用builder创建一个RequestConfig对象
     * @param builder
     * @return
     */
    @Bean(name = "requestConfig")
    public RequestConfig getRequestConfig(@Qualifier("builder")RequestConfig.Builder builder){
        RequestConfig requestConfig = builder.build();
        return requestConfig;
    }

    @Bean(name = "httpRequestRetryHandler")
    public HttpRequestRetryHandler getRetryHandler(){
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if(executionCount > 1){
                    return false;
                }
//                if(exception instanceof SocketTimeoutException){//响应超时不进行重试
//                    return false;
//                }
                return true;
            }
        };
        return retryHandler;
    }

    /**
     * @desc 关闭空闲连接
     */
    private class IdleWork implements Runnable{
        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleWork(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
                logger.error("========【关闭连接：：ERROR】:",ex);
            }

        }
        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

}
