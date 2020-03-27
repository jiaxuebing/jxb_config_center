package com.haier.psi.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @desc 基于zk实现分布式锁
 */
public class DistributeLock {

    //默认分布式锁节点
    private String lockNode="/zk_lock_node";

    //当前节点
    private String currentNode;

    //前一个节点
    private String preNode;

    private ZookeeperManager zookeeperManager;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public DistributeLock(ZookeeperManager zookeeperManager){
      this.zookeeperManager = zookeeperManager;
    }

    //初始化zk的分布式锁节点
    public void init(){
        //创建分布式锁节点
        zookeeperManager.createPersistNode(lockNode,"zkLockNode",false,false);
    }

    public void lock(){
        init();
        //在zk的lock节点下，创建带序号的临时节点
        String node = zookeeperManager.createEphemeralNode(lockNode+"/","",true,false);
        //查询lock节点下的所有子节点
        List<String> nodeList = zookeeperManager.findNodeChildren(lockNode,false);
        //从小到大顺序排序
        Collections.sort(nodeList);
        String lockNodeName = nodeList.get(0);
        currentNode = node.substring(node.lastIndexOf("/")+1);
        //获取锁
        if(lockNodeName.equals(currentNode)){
            return;
        }

        //没有获取锁，则进行监视他的前一个元素
        for(int i=0;i<nodeList.size();i++){
            if(currentNode.equals(nodeList.get(i))){
                preNode = nodeList.get(i-1);
                break;
            }
        }
        try{
            zookeeperManager.getZooKeeper().exists(lockNode + "/" + preNode, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getType().equals(Event.EventType.NodeDeleted)){
                        System.out.println("===锁释放了===");
                        countDownLatch.countDown();
                    }

                }
            });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void unLock(){
        zookeeperManager.delZkNode(lockNode+"/"+currentNode,false);
    }


}
