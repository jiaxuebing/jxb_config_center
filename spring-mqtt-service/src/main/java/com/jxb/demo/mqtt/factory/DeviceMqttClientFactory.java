package com.jxb.demo.mqtt.factory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * @desc MqttClientFactory工厂类
 */
public class DeviceMqttClientFactory {

    private static Logger logger = LoggerFactory.getLogger(DeviceMqttClientFactory.class);

    /**
     * @desc 创建mqttClient
     * @param mqttConnectInfo
     * @return
     */
    public static MqttClient createMqttClient(MqttConnectInfo mqttConnectInfo){
        MqttClient mqttClient = null;
        if(Objects.isNull(mqttConnectInfo)){
            throw new RuntimeException("创建mqttClient连接参数不可以为null");
        }
        try{
            String clientId = mqttConnectInfo.getClientId();
            String brokerUrl = mqttConnectInfo.getBrokerUrl();
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(brokerUrl,clientId,persistence);
        }catch (MqttException me){
           logger.error("******[DeviceMqttClientFactory::createMqttClient]::ERROR:{}",me);
        }catch (Exception e){
            logger.error("******[DeviceMqttClientFactory::createMqttClient]::ERROR:{}",e);
        }
        return mqttClient;
    }

}
