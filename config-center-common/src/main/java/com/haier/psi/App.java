package com.haier.psi;

import com.haier.psi.zookeeper.DistributeLock;
import com.haier.psi.zookeeper.ZookeeperManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    static int num =100;
    public static void main( String[] args )throws  Exception
    {
        ZookeeperManager zookeeperManager = new ZookeeperManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
              try{
                  Thread.sleep(5000);
                  String data = "{\"zk.address\":\"dahai.110.98:2180\",\"db.driver\":\"mysql.driver\",\"zk.app.node\":\"/appNode/app1\"}";
                  zookeeperManager.updateNodeData("/psi_app/test_edop",data,false);
                  //System.out.println("===[node  config is updating success! ]===");
                  String data1 = zookeeperManager.getZkNodeData("/psi_app/test_edop",false);
                  System.out.println("===[修改后数据："+data1+"]===");
              }catch (Exception e){
                  e.printStackTrace();
              }
            }

        }).start();
        zookeeperManager.startZk();


        //boolean flag = zookeeperManager.isNodeEphemeral("/test-ephemeral-node",false);
        //System.out.println("===[isEphemeral:"+flag+"]===");
        //zookeeperManager.delZkNode("/distributeId",false);
        //zookeeperManager.createPersistNode(path,"",false,false);
        //String testNode = zookeeperManager.createEphemeralNode("/jxb-","",false,false);
        //String testNode_1 = zookeeperManager.createEphemeralNode("/wwww/jxb-","",true,false);
        //String idNode = zookeeperManager.createPersistNode("/distributeId","",false,true);
        //System.out.println("===[testNode:"+testNode+"***testNode-1:"+testNode_1+"]===");
//        CountDownLatch starter = new CountDownLatch(1);
//
//        for(int i=0;i<20;i++){
//            new Thread(new Runnable(){
//                @Override
//                public void run() {
//                    DistributeLock lock = null;
//                    try{
//                        lock = new DistributeLock(zookeeperManager);
//                        starter.await();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    lock.lock();
//                    num=num-1;
//                    System.out.println("===num:"+num);
//                    lock.unLock();
//                }
//            }).start();
//        }
//        starter.countDown();


    }
}
