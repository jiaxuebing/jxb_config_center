package com.jxb.demo.worker;

import com.jxb.demo.mqtt.DeviceMqttHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class DeviceMqttRunner implements CommandLineRunner {

    @Resource
    private DeviceMqttHandler deviceMqttHandler;

    @Override
    public void run(String... args) throws Exception {
//        deviceMqttHandler.connect();
//        deviceMqttHandler.subscribe("device/#");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(;;){
//                    //WebSocketServer.broadCast(GenerateUtils.createSerialId());
//                    try{
//                        Thread.sleep(1000);
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//        }).start();

    }
}
