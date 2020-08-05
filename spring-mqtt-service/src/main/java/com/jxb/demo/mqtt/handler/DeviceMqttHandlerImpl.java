package com.jxb.demo.mqtt.handler;

import com.jxb.demo.mqtt.DeviceMqttHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @desc mqttHandler实现
 * 连接
 * 发布
 * 订阅
 */
public class DeviceMqttHandlerImpl implements DeviceMqttHandler {

    private static Logger logger = LoggerFactory.getLogger(DeviceMqttHandlerImpl.class);

    private MqttClient mqttClient;

    private MqttConnectOptions mqttConnectOptions;

    private MqttCallback mqttCallback;

    public DeviceMqttHandlerImpl(MqttClient mqttClient,MqttConnectOptions mqttConnectOptions,MqttCallback mqttCallback){
        this.mqttClient = mqttClient;
        this.mqttConnectOptions = mqttConnectOptions;
        this.mqttCallback = mqttCallback;
    }

    @Override
    public void connect() {
        try{
            //设置回调
            mqttClient.setCallback(mqttCallback);
            mqttClient.connect(mqttConnectOptions);
        }catch (Exception e){
            logger.error("******[connect::ERROR]:{}",e);
        }
    }

    @Override
    public boolean publish(String topic, int Qos, String content) {
        //构建mqttMessage
        MqttMessage mqttMessage = new MqttMessage(content.getBytes());
        mqttMessage.setQos(Qos);
        try{
            mqttClient.publish(topic,mqttMessage);
        }catch (MqttPersistenceException mpe){
            logger.error("******[publish::ERROR]:{}",mpe);
            return false;
        }catch (MqttException me){
            logger.error("******[publish::ERROR]:{}",me);
            return false;
        }
        return true;
    }

    @Override
    public boolean subscribe(String topic) {
        try{
            mqttClient.subscribe(topic);
        }catch (MqttException me){
            logger.error("*******[subscribe::ERROR]:{}",me);
            return false;
        }
        return true;
    }

    /**
     * @desc 设备消息回调
     */
    private class DeviceMsgCallback implements MqttCallback{
        @Override
        public void connectionLost(Throwable cause) {
            logger.error("****[MqttClient:{} is connectionLost,retrying.....]***",mqttClient.getClientId());
            try{
                mqttClient.connect();
            }catch (Exception e){
                logger.error("******[connectionLost::ERROR]:{}",e);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.error("*****[topicName:{}]****",topic);
            logger.error("*****[Qos:{}]****",message.getQos());
            logger.error("*****[message:{}]****",new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            logger.error("*****[deliveryComplete:{}]****",token.isComplete());
        }
    }
}
