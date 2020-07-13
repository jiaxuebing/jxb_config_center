package com.jxb.demo.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HttpConnectionPool implements HttpConnectionPoolMBean {

    @Override
    public String showPoolingStatus(String msg) {
        System.out.println("*****msg:{"+msg+"}****");
        return msg;
    }

    public static void main(String[] args) throws Exception{
        //1 创建mbeanserver ManagementFactory.getPlatformMBeanServer();
         MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();;
        //2 创建MBean
        HttpConnectionPool HttpConnectionPool = new HttpConnectionPool();
        HttpConnectionPool.showPoolingStatus("welcome to jmx");
        ObjectName objectName = new ObjectName("com.jxb.httpservice:status=HttpConnectionPool");
        //注册 MBean到MBeanServer上
        mBeanServer.registerMBean(HttpConnectionPool,objectName);

        //4 创建适配层
        Registry registry = LocateRegistry.createRegistry(8089);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:8089/HttpConnectionPoolServer");
        System.out.println("JMXServiceURL: " + url.toString());
        JMXConnectorServer jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
        jmxConnServer.start();
        Thread.sleep(1000 * 60 * 10);

    }

}
