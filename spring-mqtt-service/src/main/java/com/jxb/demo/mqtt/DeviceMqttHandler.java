package com.jxb.demo.mqtt;

/**
 * @desc 设备连接mqtt操作
 */
public interface DeviceMqttHandler {

    /**
     * @desc 连接EMQX broker
     */
     void connect();

    /**
     * @desc 发布消息
     * @param topic
     * @param Qos
     * @param content
     * @return
     */
     boolean publish(String topic,int Qos,String content);

    /**
     * @desc 订阅主题
     * @param topic
     * @return
     */
     boolean subscribe(String topic);
}
