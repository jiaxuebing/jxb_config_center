package com.jxb.demo.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderSearch {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @desc 查询订单
     * @return
     */
    public String searchOrder(){
        String result = null;
        SearchRequest searchRequest = new SearchRequest("order_index_2");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.termQuery("orderUser","贾学兵"));
        searchSourceBuilder.query(QueryBuilders.fuzzyQuery("orderSource","京东"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try{
             response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
       SearchHits hits = response.getHits();

        for(SearchHit hit:hits){
           result += hit.getSourceAsString();
        }
        return result;
    }

}
