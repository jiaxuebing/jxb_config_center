package com.jxb.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * @desc 索引及映射操作的controller
 * 创建、修改、删除、判断索引是否存在
 * 映射的创建、修改、获取
 * @author jiaxuebing
 * @date 2020-04-05
 */
@RestController
@RequestMapping("/device/data")
public class IndexController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "/visit",method = RequestMethod.GET)
    public Object testMirror(HttpServletRequest request){
        Object result = "access data success";
        String name = request.getParameter("name");
        logger.info("####接收了数据：{}####",name);
        return result;
    }


}
