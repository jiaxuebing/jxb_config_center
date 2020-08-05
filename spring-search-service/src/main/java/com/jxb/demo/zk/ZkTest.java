package com.jxb.demo.zk;

import com.jxb.demo.zk.node.ZkNodeOps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
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
                //zkNodeOps.createNode("/jxb/nameService/demo",true,false);
                //zkNodeOps.deleteNode("/jxb/nameService/demo");
                List<String> childrenList = zkNodeOps.getZkNodeChildren("/jxb");
                for(String child:childrenList){
                    System.out.println("*****[child:"+child+"]****");
                }
                //zkNodeOps.setNodeData("/jxb/nameService","你好，中国我爱你");
//                String nodeData = zkNodeOps.getNodeData("/jxb/nameService");
//                System.out.println("+++++++[nodeData:"+nodeData+"]");
                final NodeCache nodeCache = new NodeCache(client,"/jxb/application",false);
                nodeCache.start();
                nodeCache.getListenable().addListener(new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        byte[] bytes = nodeCache.getCurrentData().getData();
                        if(bytes != null){
                            System.out.println("/jxb/application****data:"+new String(bytes));
                        }
                    }
                });
                zkNodeOps.deleteNode("/jxb/application");
                zkNodeOps.createNode("/jxb/application",true,false);
                Thread.sleep(1000);
                zkNodeOps.setNodeData("/jxb/application","我修改了一次");
                Thread.sleep(1000);
                zkNodeOps.setNodeData("/jxb/application","我修改了2次");
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
