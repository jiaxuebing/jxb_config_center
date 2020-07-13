package com.jxb.demo.index;


import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ManageIndex {

    private static Logger logger = LoggerFactory.getLogger(ManageIndex.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @desc 创建索引
     */
    public void createIndex(String indexName){
        try{
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @desc 是否存在索引
     * @param indexName
     * @return
     */
    public boolean existsIndex(String indexName){
      boolean isExists = true;
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        try{
            isExists =  restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

      return isExists;
    }

    /**
     * @desc 创建索引映射
     */
    public void putIndexMapping(String indexName){
        PutMappingRequest putMappingRequest = new PutMappingRequest(indexName);
        XContentBuilder mappingBuilder = null;
        try {
            mappingBuilder = XContentFactory.jsonBuilder();
            mappingBuilder.startObject();
            {
                mappingBuilder.startObject("properties");
                {
                    mappingBuilder.startObject("orderUser");
                    {
                        mappingBuilder.field("type","keyword");
                        mappingBuilder.field("store",true);
                    }
                    mappingBuilder.endObject();
                    mappingBuilder.startObject("orderNo");
                    {
                        mappingBuilder.field("type","keyword");
                        mappingBuilder.field("store",true);
                    }
                    mappingBuilder.endObject();
                    mappingBuilder.startObject("orderSource");
                    {
                        mappingBuilder.field("type","text");
                        mappingBuilder.field("analyzer","ik_max_word");
                        mappingBuilder.field("store",true);
                    }
                    mappingBuilder.endObject();
                    mappingBuilder.startObject("orderCreateTime");
                    {
                        mappingBuilder.field("type","date");
                        mappingBuilder.field("store",true);
                        mappingBuilder.field("format","yyyy-MM-dd HH:mm:ss");
                    }
                    mappingBuilder.endObject();
                }
                mappingBuilder.endObject();

            }
            mappingBuilder.endObject();
            putMappingRequest.source(mappingBuilder);
            AcknowledgedResponse response = restHighLevelClient.indices().putMapping(putMappingRequest,RequestOptions.DEFAULT);
            if(response.isAcknowledged()){
                logger.info("=====创建索引：[{}]的mapping is success!=========",indexName);
            }

        }catch (Exception e){
          e.printStackTrace();
        }

    }

    /**
     * @desc 删除索引
     * @param indexName
     */
    public void deleteIndex(String indexName){
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("order_index");
        try{
            restHighLevelClient.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * @desc 插入orderEntity
     */
    public void addOrderEntity(){
        Map map = new HashMap();
        map.put("orderNo","psi202004021990");
        map.put("orderSource","京东商城");
        map.put("orderUser","韩迎新");
        map.put("orderCreateTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try{
            IndexRequest indexRequest = new IndexRequest("order_index_2","_doc").source(map);
            restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
            logger.info("==========orderEntity 文档创建成功========");
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
