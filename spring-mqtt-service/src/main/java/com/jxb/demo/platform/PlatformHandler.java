package com.jxb.demo.platform;

import com.jxb.demo.mqtt.DeviceMqttHandler;
import com.jxb.demo.mqtt.factory.DeviceMqttClientFactory;
import com.jxb.demo.mqtt.factory.MqttConnectInfo;
import com.jxb.demo.mqtt.handler.DeviceMqttHandlerImpl;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @desc 平台客户端处理
 * 主要与设备通过mqtt协议进行交互
 */
@Configuration
public class PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(PlatformHandler.class);

    @Value("${platform.clientId}")
    private String clientId;

    @Value("${platform.brokerUrl}")
    private String brokerUrl;

    @Value("${mqttConnectOptions.keepAliveInternal}")
    private int keepAliveInternal;

    @Value("${mqttConnectOptions.cleanSession}")
    private boolean cleanSession;

    @Value("${mqttConnectOptions.connectTimeout}")
    private int connectTimeout;

    @Value("${mqttConnectOptions.userName}")
    private String userName;

    @Value("${mqttConnectOptions.password}")
    private String password;

    private MqttClient mqttClient;

    private MqttConnectOptions mqttConnectOptions;


    @Bean("deviceMqttHandler")
    public DeviceMqttHandler createDeviceMqttHandler(){
        MqttConnectInfo connectInfo = new MqttConnectInfo();
        connectInfo.setClientId(clientId);
        connectInfo.setBrokerUrl(brokerUrl);
        mqttClient = DeviceMqttClientFactory.createMqttClient(connectInfo);
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(cleanSession);
        mqttConnectOptions.setKeepAliveInterval(keepAliveInternal);
        mqttConnectOptions.setConnectionTimeout(connectTimeout);
        return new DeviceMqttHandlerImpl(mqttClient, mqttConnectOptions, new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.error("###########[PlatformHandler::MQTT::Connect Lost]##########");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                logger.error("*****[topicName:{}]****",topic);
                logger.error("*****[Qos:{}]****",message.getQos());
                logger.error("*****[message:{}]****",new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
