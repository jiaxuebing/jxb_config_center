package com.jxb.demo.zk;

import com.jxb.demo.zk.node.ZkNodeOps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class ZkTest {

    static class ZkManager implements Runnable{

        private CuratorFramework client;

        public ZkManager(CuratorFramework client){
            this.client = client;
        }

        @Override
        public void run() {

            ZkNodeOps zkNodeOps = new ZkNodeOps(client);
            try{
                //Thread.sleep(5000);
                zkNodeOps.createNode("/jxb/nameService",true,false);
                List<String> childrenList = zkNodeOps.getZkNodeChildren("/jxb");
                for(String child:childrenList){
                    System.out.println("*****[child:"+child+"]****");
                }
            }catch (Exception e){
                e.printStackTrace();

            }
        }

    }

    public static void main(String[] args) throws Exception{
        //设置重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",retryPolicy);
        client.start();
        new Thread(new ZkManager(client)).start();
        Thread.sleep(100000000);

        //创建节点
//        client.create().forPath("/spring-demo");
//        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/spring-demo/nameService");

    }

}
