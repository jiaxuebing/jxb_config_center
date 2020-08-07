package com.jxb.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class MqttServiceApp
{
    public static void main( String[] args )throws  Exception
    {
        SpringApplication.run(MqttServiceApp.class,args);
    }
}
