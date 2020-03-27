package com.haier.psi.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc 设置配置中心的bean处理器
 */

@Component
public class ConfigCenterBeanPostProcessor implements BeanPostProcessor {

    public static Map<String,Object> beanMap = new HashMap<>();


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(Value.class)){
                beanMap.put(beanName,bean);
            }
        }
        return bean;
    }
}
