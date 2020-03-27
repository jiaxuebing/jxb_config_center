package com.haier.psi.service;

import com.alibaba.fastjson.JSONObject;
import com.haier.psi.zookeeper.ZookeeperManager;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

/**
 * @desc 重写加载配置文件的方法
 * 需要从zookeeper中配置的节点上拉取配置信息
 */

@Configuration
public class ConfigPropertyPlaceholder extends PropertyPlaceholderConfigurer {


    public static ZookeeperManager zookeeperManager = new ZookeeperManager();

   // private

    private BeanFactory beanFactory;

    //应用配置节点路径
    private String appConfigPath;


    @Override
    protected Properties mergeProperties() throws IOException {

        Properties properties = super.mergeProperties();
        appConfigPath = "/psi_app/test_edop";//(String)properties.get("config.application.name");
        Properties appProp = pullConfigInfo();
        properties.putAll(appProp);
        return properties;
    }

    /**
     * @desc 从该应用的节点去拉取配置信息
     */
    public Properties pullConfigInfo(){
        Properties properties = null;
        //拉取应用节点的配置信息
        String data = zookeeperManager.getZkNodeData(appConfigPath,new ConfigWatcher());
        //格式化配置信息【可以进行加解密的操作】
        //将json转化为map数组
        System.out.println("===[配置信息："+data+"]===");
        properties = JSONObject.parseObject(data, Properties.class);
        //将配置信息加载到应用中
        return properties;
    }

    /**
     * @desc 配置监视器
     */
    private class ConfigWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            //当节点数据改变时，更新应用的配置信息
            if(event.getType() == Event.EventType.NodeDataChanged){
                try{
                    Properties configProperties = pullConfigInfo();
                    refreshConfigBean(configProperties);
                }catch (Exception e){
                  e.printStackTrace();
                }
            }
        }

    }

    /**
     * @desc 动态刷新bean的来自于配置中心的属性信息
     * @param configProperties 配置集合
     * @author jiaxuebing
     * @date 2019-11-18
     */
    public void refreshConfigBean(Properties configProperties) throws Exception{
        Set<String> keySet = ConfigCenterBeanPostProcessor.beanMap.keySet();
        for(String key : keySet){
            Object bean = ConfigCenterBeanPostProcessor.beanMap.get(key);
            Class clazz = bean.getClass();
            Field[]fields = clazz.getDeclaredFields();
            for(Field field : fields){
               if(field.isAnnotationPresent(Value.class)) {
                   Value value = field.getAnnotation(Value.class);
                   String configKey = value.value();//${}
                   configKey = configKey.substring(2,configKey.lastIndexOf("}"));
                   field.setAccessible(true);
                   field.set(bean,configProperties.get(configKey));
                }
            }
        }
    }

}
