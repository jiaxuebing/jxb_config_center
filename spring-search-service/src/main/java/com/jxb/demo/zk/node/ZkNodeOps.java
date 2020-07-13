package com.jxb.demo.zk.node;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @desc zkNode的增删改查操作
 * @author jiaxuebing
 * @date 2020-07-13
 */
public class ZkNodeOps {

    private static Logger logger = LoggerFactory.getLogger(ZkNodeOps.class);

    private CuratorFramework client;

    public ZkNodeOps(CuratorFramework client){
        this.client = client;
    }

    /**
     * @desc 创建zk节点【持久、临时、持久序号、临时序号】
     * @param nodePath 节点路径
     * @param persistFlag true 持久 false 临时
     * @param seqFlag true 带序号 false 不带序号
     */
    public void createNode(String nodePath,boolean persistFlag,boolean seqFlag) throws Exception{
        logger.info("********[ZkNodeOps::createNode]===>>>start********");
        if(persistFlag){//如果是持久节点
            if(seqFlag){//如果是带序号
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(nodePath);

            }else{//不带序号
                client.create().creatingParentsIfNeeded().forPath(nodePath,"jxb".getBytes());

            }
        }else{
            if(seqFlag){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(nodePath);

            }else{
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
            }
        }
        logger.info("********[ZkNodeOps::createNode]===>>>end********");
    }

    /**
     * @desc 获取zk节点的子节点
     * @param nodePath 路径
     * @return
     */
    public List<String> getZkNodeChildren(String nodePath) throws  Exception{
        logger.info("********[ZkNodeOps::getZkNodeChildren]===>>>start********");
        List<String> childrenList = null;
        childrenList = client.getChildren().forPath(nodePath);
        logger.info("********[ZkNodeOps::getZkNodeChildren]===>>>end********");
        return childrenList;
    }

}
