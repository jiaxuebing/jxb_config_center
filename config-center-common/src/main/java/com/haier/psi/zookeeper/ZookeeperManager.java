package com.haier.psi.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @desc zookeeper管理类
 * @author jiaxuebing
 * @date 2019-11-04
 */
//@Service
public class ZookeeperManager {

  private static Logger logger = LoggerFactory.getLogger(ZookeeperManager.class);

//  @Resource
//  private ZookeeperConfig zookeeperConfig;

  //zk的运行状态标记，0 关闭 1 运行
  private int runState=0;

  //设置zk的重试次数
  private int maxConnectTimes = 10;

  //重连次数
  private int connectTimes=1;

  //创建线程同步工具(保证client与zookeeper处于连接状态)
  private CountDownLatch zkCountDownLatch = new CountDownLatch(1);

  private ZooKeeper zooKeeper = null;

  private ZkNodeOpera zkNodeOpera = null;

  private DistributeIdGenerator distributeIdGenerator = null;


  public ZookeeperManager(){
      zkNodeOpera = new ZkNodeOpera();
      distributeIdGenerator = new DistributeIdGenerator();
  }

  public ZooKeeper buildZookeeper(){
      logger.info("===执行[ZookeeperManager.buildZookeeper]****zookeeper-client连接zookeeper服务端******]===");
      try{
          //1释放zk连接
          closeZkConnection();
          //zooKeeper = new ZooKeeper(zookeeperConfig.getZkAddress(),zookeeperConfig.getSessionTimeout(),new ZkManagerWatcher());
          zooKeeper = new ZooKeeper("localhost:2181",30000,new ZkManagerWatcher());
      }catch (Exception e){
          logger.error("===[execute-thread:{},执行创建zookeeper客户端失败：{}]===",Thread.currentThread().getName(),e);
      }
      return zooKeeper;
  }


    /**
     * @desc zookeeper的watcher监视器
     * 1）实现zookeeper的断连重连
     */
  private class ZkManagerWatcher implements Watcher {

      @Override
      public void process(WatchedEvent event) {
          if(Event.KeeperState.SyncConnected == event.getState()){//客户端连接zookeeper成功
              logger.info("===[zookeeper-client:{}***connect success]===",zooKeeper.getSessionId());
          }else if(Event.KeeperState.Disconnected == event.getState()){//连接zk失败
              logger.info("===[zookeeper-client连接zookeeper失败]===");
              reConnectZk();
          }else if(Event.KeeperState.Expired == event.getState()){//session会话过期
              logger.info("===[zookeeper的session已经过期]===");
              reConnectZk();
          }
      }

  }

    /**
     * @desc 启动zk
     */
  public void startZk(){
      try {
          buildZookeeper();
          zkCountDownLatch.await();
      }catch (Exception e){
          e.printStackTrace();
      }
  }

    /**
     * @desc 关闭zk
     */
  public void stopZk(){
      try {
          zkCountDownLatch.countDown();
          zooKeeper.close();
      }catch (Exception e){
          e.printStackTrace();
      }
  }

    /**
     * @desc  关闭zookeeper连接
     */
  public void closeZkConnection(){
      if(zooKeeper != null){
          try {
              zooKeeper.close();
          }catch (Exception e){
              logger.error("===[zookeeper-client关闭发生异常]：{}===",e);
          }
      }
  }

    /**
     * @desc 重连zookeeper
     */
  public void reConnectZk(){
      //如果重连次数小于最大尝试连接次数
      if(connectTimes <= maxConnectTimes){
          connectTimes++;
          buildZookeeper();
      }
  }

    /**
     * @desc 反回zookeeper客户端
     * @return
     */
  public ZooKeeper getZooKeeper(){
      return zooKeeper;
  }

    /**
     * @desc 查找节点下的所有子节点
     * @param path
     * @return
     */
  public List<String> findZkNodeAllChildren(String path){
      return zkNodeOpera.findZkNodeAllChildern(path);
  }

    /**
     * @desc 创建临时节点
     * @param path
     * @param data
     * @param isSeq
     * @param watcher
     * @return
     */
  public String createEphemeralNode(String path,String data,boolean isSeq,boolean watcher){
      return zkNodeOpera.createEphemeralNode(path,data,isSeq,watcher);
  }

    /**
     * @desc 创建持久化节点
     * @param path
     * @param data
     * @param isSeq
     * @param watcher
     * @return
     */
  public String createPersistNode(String path,String data,boolean isSeq,boolean watcher){
      return zkNodeOpera.createPersistNode(path,data,isSeq,watcher);
  }

    /**
     * @desc 查找节点下的子节点（一级）
     * @param path
     * @param watcher
     * @return
     */
  public List<String> findNodeChildren(String path,boolean watcher){
      return zkNodeOpera.findNodeChildren(path,watcher);
  }

    /**
     * @desc 修改节点数据
     * @param path
     * @param data
     * @param watcher
     * @return
     */
  public boolean updateNodeData(String path,String data,boolean watcher){
      return zkNodeOpera.updateNodeData(path,data,watcher);
  }

    /**
     * @desc 删除zk节点
     * @param path
     * @param watcher
     * @return
     */
  public boolean delZkNode(String path,boolean watcher){
      return zkNodeOpera.delZkNode(path,watcher);
  }

    /**
     * @desc 判断节点是否存在
     * @param path
     * @param watcher
     * @return
     */
  public boolean isZkNodeExist(String path,boolean watcher){
      return zkNodeOpera.isZkNodeExist(path,watcher);
  }

    /**
     * @desc 判断节点是否是临时节点
     * @param path
     * @param watcher
     * @return
     */
  public boolean isNodeEphemeral(String path,boolean watcher){
      return zkNodeOpera.isNodeEphemeral(path,watcher);
  }

    /**
     * @desc 生成分布式ID
     * @param path
     * @return
     */
  public String generateDistributeId(String path){
      return distributeIdGenerator.distributeUniqueId(path);
  }

    /**
     * @desc 获取zk节点数据
     * @param path Zk节点路径
     * @param watcher 是否设置监听器
     * @return
     */
    public String getZkNodeData(String path,Object watcher){
        return zkNodeOpera.getZkNodeData(path,watcher);
    }

    /**
     * @desc zookeeper的节点管理操作
     */
  private class ZkNodeOpera{

        /**
         * @desc 创建临时节点
         * @param path 节点路径
         * @param data 节点数据
         * @param isSeq 是否创建有序号的节点
         * @return
         */
      public String createEphemeralNode(String path,String data,boolean isSeq,boolean watcher){
          String nodeName=null;
          try {
              //默认创建无序号的节点
              CreateMode createMode=CreateMode.EPHEMERAL;
              if(isSeq){
                   createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
              }else{
                  //如果创建无序号的临时节点，判断当前临时节点是否存在
                   if(isZkNodeExist(path,watcher)){
                       return nodeName;
                   }
              }
              //检查父节点是否存在
              int index = path.lastIndexOf("/");
              boolean parentExistFlag = true;
              if(index > 0){
                  String parentPath = path.substring(0,index);
                  parentExistFlag = isZkNodeExist(parentPath,watcher);
              }
              //如果父节点存在，则进行创建子节点
              if(parentExistFlag){
                  nodeName = zooKeeper.create(path,data.getBytes("UTF-8"),ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
              }
          }catch (Exception e){
              e.printStackTrace();
          }
          return nodeName;
      }

        /**
         * @desc 创建持久化节点
         * @param path 节点路径
         * @param data 节点数据
         * @param isSeq 是否创建有序号的节点
         * @return
         */
      public String createPersistNode(String path,String data,boolean isSeq,boolean watcher){
          String nodeName=null;
          try {
              //默认创建无序号的节点
              CreateMode createMode=CreateMode.PERSISTENT;
              if(isSeq){
                  createMode = CreateMode.PERSISTENT_SEQUENTIAL;
              }
              boolean existFlag = isZkNodeExist(path,watcher);
              if(!existFlag){
                  nodeName = zooKeeper.create(path,data.getBytes("UTF-8"),ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
              }
          }catch (Exception e){
              e.printStackTrace();
          }
          return nodeName;
      }

        /**
         * @desc 查找节点下的所有子节点
         * @param path
         * @return
         */
      public List<String>  findZkNodeAllChildern(String path){
          List<String> nodeList = new ArrayList<>();
          nodeChildren(path,nodeList);
          return nodeList;
      }

        /**
         * @desc 获取所有子节点
         * @param path
         * @return
         */
      private void nodeChildren(String path,List<String> nodesList){
         try{
             List<String> nodeList = zooKeeper.getChildren(path,false);
             List<String> nodePathList = new ArrayList<>();
             //拼装node节点
             for(String node:nodeList){
                 nodePathList.add("/".equals(path)?path+node:path+"/"+node);
             }
             nodesList.addAll(nodePathList);
             if(nodeList != null && nodeList.size() > 0){
                 for(String nodePath:nodePathList){
                     nodeChildren(nodePath,nodesList);
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
         }
      }

        /**
         * @desc 获取节点下的一级子节点
         * @param path 节点路径
         * @return
         */
      public List<String> findNodeChildren(String path,boolean watcher){
        List<String> nodeList = null;
        try{
            nodeList = zooKeeper.getChildren(path,watcher);
        }catch (Exception e){
            e.printStackTrace();
        }
        return nodeList;
      }

        /**
         * @desc 修改节点数据
         * @param path 节点路径
         * @param data 节点数据
         * @return
         */
      public boolean updateNodeData(String path,String data,boolean watcher){
          boolean updateFlag = false;
          try{
              Stat stat = new Stat();
              zooKeeper.getData(path,watcher,stat);
              zooKeeper.setData(path,data.getBytes("UTF-8"),stat.getVersion());
              updateFlag=true;
          }catch (Exception e){
              e.printStackTrace();
              updateFlag = updateNodeData(path, data, watcher);
          }
          return updateFlag;
      }

        /**
         * @desc 删除zk节点
         * @param path
         * @return
         */
      public boolean delZkNode(String path,boolean watcher){
        boolean delFlag = false;
        try {
            Stat stat = zooKeeper.exists(path,watcher);
            if(stat == null){
                return delFlag;
            }
            zooKeeper.delete(path, stat.getVersion());
            delFlag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return delFlag;
      }

        /**
         * @desc 判断zk节点是否存在
         * @param path 节点路径
         * @param watcher 是否添加监视
         * @return
         */
      public boolean isZkNodeExist(String path,boolean watcher){
        boolean existFlag = false;
        try{
            Stat stat = zooKeeper.exists(path,watcher);
            if(stat != null){
                existFlag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return existFlag;
      }

        /**
         * @desc 判断节点是否是临时节点
         * @param path
         * @return
         */
      public boolean isNodeEphemeral(String path,boolean watcher){
          boolean isEphemeral = false;
          try {
                Stat stat = zooKeeper.exists(path,watcher);
                if(stat != null){
                    long owner = stat.getEphemeralOwner();
                    if(owner != 0){
                        isEphemeral = true;
                    }
                }
          }catch (Exception e){
              e.printStackTrace();
          }
          return isEphemeral;
      }

        /**
         * @desc 获取zk节点数据
         * @param path Zk节点路径
         * @param watcher 是否设置监听器
         * @return
         */
      public String getZkNodeData(String path,Object watcher){
          String data = null;
          try{
              Stat stat = null;
              byte[] dataBytes = null;
              if(watcher instanceof Boolean){
                  stat = zooKeeper.exists(path,(Boolean) watcher);
                  dataBytes = zooKeeper.getData(path,(Boolean)watcher,stat);
              }else if(watcher instanceof Watcher){
                  stat = zooKeeper.exists(path,(Watcher) watcher);
                  dataBytes = zooKeeper.getData(path,(Watcher)watcher,stat);
              }else{
                  return null;
              }
              data = new String(dataBytes,"UTF-8");
          }catch (Exception e){
              e.printStackTrace();
          }
          return data;
      }

  }

    /**
     * @desc 分布式id生成器
     */
  private class DistributeIdGenerator{

        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        /**
         * @desc 分布式唯一ID生成
         * @param nodePath 临时节点名称
         * @return
         */
        public String distributeUniqueId(String nodePath){
            String id = null;
            nodePath = nodePath+simpleDateFormat.format(new Date())+"_";
            id = createEphemeralNode(nodePath,"",true,false);
            delZkNode(id,false);
            id = id.substring(id.lastIndexOf("/")+1);
            return id;
        }

        /**
         * @desc 初始化id生成器
         * @param path
         */
        public void initIdGenNode(String path){
            zkNodeOpera.delZkNode(path,false);
            zkNodeOpera.createPersistNode(path,"",false,false);
        }

  }


}