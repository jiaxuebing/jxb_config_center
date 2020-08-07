package com.jxb.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxb.demo.entity.energy.EnergyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    private static Logger logger = LoggerFactory.getLogger(EnergyController.class);

    @RequestMapping(value = "/info",method = RequestMethod.POST)
    public Object info(@RequestBody Map<String,List<EnergyEntity>> dataMap){
        logger.info("#######[EnergyController.info]#####start");
        logger.info("#######data:{}", JSONObject.toJSONString(dataMap));
        return dataMap;
    }

}
