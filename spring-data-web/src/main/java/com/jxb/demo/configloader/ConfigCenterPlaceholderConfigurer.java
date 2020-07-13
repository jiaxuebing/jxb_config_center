package com.jxb.demo.configloader;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import java.io.IOException;
import java.util.Properties;

/**
 * @desc 自定义加载properties属性配置文件重写mergeProperties
 * 配置中心的节点获取：
 * 1）configcenter_部门名称_业务名称_应用名称
 */
public class ConfigCenterPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        //从配置中心获取配置信息并且加载


        return null;
    }

}
