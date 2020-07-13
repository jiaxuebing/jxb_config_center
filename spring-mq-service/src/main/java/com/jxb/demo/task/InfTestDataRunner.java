package com.jxb.demo.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.*;
import java.util.Enumeration;


/**
 * @desc 周期性任务执行器
 * @author jiaxuebing
 * @date 2020-04-12
 */
@Component
public class InfTestDataRunner implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(InfTestDataRunner.class);

    private Object lock = new Object();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private InfTestConsumer infTestConsumer;

    @Autowired
    private InfTestFilter infTestFilter;

    @Autowired
    private InfTestListener infTestListener;

    @Autowired
    private RedisDelayQueue redisDelayQueue;



    @Override
    public void run(String... args) throws Exception {
      logger.info("===执行run方法===");
      Thread runThread = Thread.currentThread();
      //初始化runner调度信息
      init(runThread);
      infTestListener.listen();

//      while(true){
//          String runSatus = (String)redisTemplate.opsForValue().get("infTest:runFlag");
//          synchronized (lock){
//              while("pause".equals(runSatus)){
//                  //唤醒monitorrunner
//                  lock.notifyAll();
//                  //阻塞当前线程
//                  lock.wait();
//                  runSatus = (String)redisTemplate.opsForValue().get("infTest:runFlag");
//              }
//          }
//          if("stop".equals(runSatus)){
//              System.out.println("=====run方法执行stop======");
//              break;
//          }
//          //处理业务逻辑
//          System.out.println("======正在执行==run方法==");
//
//          //getHostIp();
//          System.out.println("外网ip："+getWip());
//          try {
//              Thread.sleep(1000);
//          }catch (Exception e){
//              e.printStackTrace();
//          }
//      }
        logger.info("===run方法运行结束==");

    }

    /**
     * @desc 任务执行器启动初始化
     * @param runThread 执行器线程对象
     * @author jiaxuebing
     * @date 2020-04-12
     */
    private void init(Thread runThread){
        //测试一下
        long now = System.currentTimeMillis();
        for(int i=1;i<=1000;i++){
            long delay = (i * 1000) + now;
            redisDelayQueue.add("task-"+i,delay);
        }
        for(int i=1;i<10;i++){
            TestMsg msg = new TestMsg();
            msg.setMsg("wwww-"+i);
            msg.setMsgCount(i);
            msg.setMsgNo("task001-"+i);
            infTestListener.addListener(msg);
        }
        //向redis中初始化runFlag
        redisTemplate.opsForValue().set("infTest:runFlag","run");
        //开启监控thread
        Thread monitorThread = new Thread(new MonitorRunner(runThread,redisTemplate));
        monitorThread.start();
        //开启消费者线程
        Thread infTestConsumerThread = new Thread(new InfTestConsumerRunner());
        infTestConsumerThread.start();
        //开启过滤任务线程
        Thread infTestFilterThread = new Thread(new InfTestFilterRunner());
        infTestFilterThread.start();
    }


    private String getWip(){
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress()&& !ip.isLoopbackAddress()&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
         } else {
            return localip;
         }
    }

    /**
     * @desc 监测执行器
     * 通过监听调度指令随时操作当前runner
     * @author jiaxuebing
     * @date 2020-04-12
     */
    private class MonitorRunner implements Runnable{
        private Thread runThread;
        private RedisTemplate redisTemplate;

        public MonitorRunner(Thread runThread,RedisTemplate redisTemplate){
          this.runThread = runThread;
          this.redisTemplate = redisTemplate;
        }

        @Override
        public void run() {
            logger.info("=====启动MonitorRunner=====");
            while(true){
                String runStatus = (String)redisTemplate.opsForValue().get("infTest:runFlag");
                synchronized (lock){
                    Thread.State runThreadState= runThread.getState();
                    while("run".equals(runStatus)){
                        if(Thread.State.WAITING == runThreadState){
                            //唤醒执行线程
                            lock.notify();
                        }
                        try{
                            //阻塞监视线程
                            lock.wait();
                            runStatus = (String)redisTemplate.opsForValue().get("infTest:runFlag");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                if("stop".equals(runStatus)){
                  break;
                }
                //监控主线程
                System.out.println("======主线程被阻塞======");

                try {
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            logger.info("========MonitorRunner监听结束=========");

        }
    }

    /**
     * @desc 消费者运行器
     * @author jiaxuebing
     * @date 2020-04-12
     */
    private class InfTestConsumerRunner implements Runnable{
        @Override
        public void run() {
            logger.info("=====[启动InfTestConsumerRunner----消息消费线程启动]====");
            infTestConsumer.consume();
        }
    }


    /**
     * @desc 任务转换器--将满足条件任务从zset转到list，消费者可以消费
     * @author jiaxuebing
     * @date 2020-04-12
     */
    private class InfTestFilterRunner implements Runnable{
        @Override
        public void run() {
          logger.info("======[启动infTestFilter---任务过滤线程启动]=====");
            infTestFilter.filter();
        }
    }

    public void runMethod(Object targetObj,String methodName){
        Method method = null;
        try{
             Class clazz = targetObj.getClass();
             method = clazz.getMethod(methodName);
             method.invoke(targetObj);
        }catch(Exception e){

        }

    }

}
