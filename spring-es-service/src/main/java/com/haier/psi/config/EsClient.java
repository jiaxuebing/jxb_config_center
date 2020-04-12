package com.haier.psi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EsClient {

    @Bean("restHighLevelClient")
    public RestHighLevelClient getEsClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("10.200.17.35",9200,"http")
                ));
        return client;
    }

}
