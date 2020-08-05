package com.jxb.demo.utils;

import java.util.UUID;

public class GenerateUtils {

    /**
     * @desc 创建序列化ID
     * @return
     */
    public static String createSerialId(){
        String serialId = UUID.randomUUID().toString();
        return serialId;
    }

}
