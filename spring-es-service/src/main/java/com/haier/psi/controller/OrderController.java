package com.haier.psi.controller;

import com.haier.psi.index.ManageIndex;
import com.haier.psi.search.OrderSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private ManageIndex manageIndex;

    @Autowired
    private OrderSearch orderSearch;

    @RequestMapping(value = "/insertOrder",method = RequestMethod.GET)
    public String insertOrder(){
        String result = null;
       // manageIndex.createIndex("order_index");
        manageIndex.addOrderEntity();
        System.out.println("=========创建索引及插入数据成功=======");
        result = "插入数据成功";
        return result;
    }

    @RequestMapping(value = "/searchOrder",method = RequestMethod.GET)
    public String searchOrder(){
        String result = null;
        result = orderSearch.searchOrder();
        logger.info("=======查询成功==result:{}=====",result);
        return result;
    }

}
