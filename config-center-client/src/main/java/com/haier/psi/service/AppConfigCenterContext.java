package com.haier.psi.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppConfigCenterContext implements ApplicationContextAware {


    public static ApplicationContext applicationContext;

    @Autowired
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppConfigCenterContext.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName){
        Object obj = applicationContext.getBean(beanName);
        return obj;
    }

}
