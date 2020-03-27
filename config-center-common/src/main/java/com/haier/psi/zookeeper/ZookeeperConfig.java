package com.haier.psi.zookeeper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    //@Value("${config.center.zookeeper}")
    private String zkAddress;

    //@Value("${config.center.zookeeper.timeout}")
    private int sessionTimeout;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
