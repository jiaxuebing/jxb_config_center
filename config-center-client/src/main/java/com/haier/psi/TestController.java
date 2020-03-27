package com.haier.psi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Value("${zk.address}")
    private String zkAddr;

    @Autowired
    private User user;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        System.out.println("====[访问test]===zkAddr:"+user.getZkAddr());
      String result = null;
      result = user.getZkAddr();

      //configurableApplicationContext.refresh();
      return result;
    }

}
