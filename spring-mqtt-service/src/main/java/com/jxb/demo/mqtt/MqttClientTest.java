package com.jxb.demo.mqtt;

import com.jxb.demo.mqtt.factory.DeviceMqttClientFactory;
import com.jxb.demo.mqtt.factory.MqttConnectInfo;
import com.jxb.demo.mqtt.handler.DeviceMqttHandlerImpl;
import org.eclipse.paho.client.mqttv3.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MqttClientTest {

    public static void main(String[] args)throws  Exception{
        String topicName = "device/test1";
        String broker = "tcp://127.0.0.1:1883";
        //String broker = "tcp://115.28.2.201:1883";
        String clientId = "device-jxb-01";
        //1 获取mqttClient
        MqttConnectInfo mqttConnectInfo = new MqttConnectInfo();
        mqttConnectInfo.setBrokerUrl(broker);
        mqttConnectInfo.setClientId(UUID.randomUUID().toString());
         MqttClient mqttClient = DeviceMqttClientFactory.createMqttClient(mqttConnectInfo);
        //2 创建mqttHandler
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setUserName("haier");
//        mqttConnectOptions.setPassword("haier_123".toCharArray());
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(100);


    }

    static class DeviceCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) {
          //连接丢失在这里进行重连
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //subcribe topic 接受消息处理
            System.out.println("接收消息主题："+topic);
            System.out.println("接收消息QoS："+message.getQos());
            System.out.println("接收消息内容："+new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete......"+token.isComplete());
        }
    }

}
