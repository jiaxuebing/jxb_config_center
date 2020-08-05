package com.jxb.demo.mqtt.factory;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * @desc mqttClient连接信息
 */
public class MqttConnectInfo {

    private String brokerUrl;

    private String clientId;

    private MqttConnectOptions mqttConnectOptions;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }
}
