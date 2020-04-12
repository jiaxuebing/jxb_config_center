package com.haier.psi.controller;

import com.haier.psi.index.ManageIndex;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc 索引及映射操作的controller
 * 创建、修改、删除、判断索引是否存在
 * 映射的创建、修改、获取
 * @author jiaxuebing
 * @date 2020-04-05
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ManageIndex manageIndex;

    /**
     * @desc 索引下添加mapping
     * @return
     */
    @RequestMapping(value = "/putMapping",method = RequestMethod.GET)
     public String putMapping(){
        String result = null;
        logger.info("======索引添加映射===[IndexController.putMapping]====start====");
        manageIndex.createIndex("order_index_2");
        manageIndex.putIndexMapping("order_index_2");
        result = "putmaping is success";
        logger.info("======索引添加映射===[IndexController.putMapping]====end====");
        return result;
     }


}
