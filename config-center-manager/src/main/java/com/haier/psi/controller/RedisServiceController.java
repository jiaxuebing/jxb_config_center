package com.haier.psi.controller;

import com.haier.psi.kafka.producer.LogProducer;
import com.haier.psi.redis.RedisMsgQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class RedisServiceController {

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisMsgQueue redisMsgQueue;

    @Resource
    private LogProducer logProducer;

    //static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * @desc redis事务测试成功
     * @return
     */
    @RequestMapping(value = "/redisExec",method = RequestMethod.GET,produces = "application/json")
    public String redisExec(){
        String result = null;

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                redisTemplate.setEnableTransactionSupport(true);
                redisTemplate.execute(
                        new SessionCallback() {
                            @Override
                            public Object execute(RedisOperations operations) throws DataAccessException {
                                List<Object> result = null;
                                //监控productNum
                                operations.watch("productNum");
                                try{
                                    countDownLatch.await();
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                                operations.multi();
                                operations.opsForValue().decrement("productNum");
                                operations.opsForList().rightPush("buyerList",name);
                                try{
                                    result = operations.exec();
                                    System.out.println(result.toString());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }
                );
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                redisTemplate.opsForValue().decrement("productNum");
                countDownLatch.countDown();
            }
        });
        t2.start();
        //必须手动释放连接
        RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        result = "success";
        return result;
    }

    @RequestMapping(value = "/redisTransaction",method = RequestMethod.GET,produces = "application/json")
    public String redisTransaction(){
        String result = null;
        redisTemplate.opsForValue().decrement("productNum");
        result = "redisTransaction is success";
        //countDownLatch.countDown();
        return result;
    }

    /**
     * @redis 发布与订阅实现
     * @return
     */
    @RequestMapping(value = "/redisPub",method = RequestMethod.GET,produces = "application/json")
    public String redisPub(){
      String result = "发送 消息";
      for(int i = 1000;i>=0;i--){
          redisTemplate.convertAndSend("order-oms","订单号：haier1009001"+i);
      }
      return result;
    }

    /**
     * @desc 向队列添加信息
     * @return
     */
    @RequestMapping(value="/sendMsg",method = RequestMethod.GET,produces = "application/json")
    public String sendMsg(){
        String result = "发送信息成功";
        for(int i=0;i<100;i++){
            redisMsgQueue.addMsg("order-"+i);
        }
        return result;
    }

    /**
     * @desc 从redis队列中取出数据
     * @return
     */
    @RequestMapping(value="/takeMsg",method = RequestMethod.GET,produces = "application/json")
    public String takeMsg(HttpServletResponse response){
      String result = "从redis队列中取出数据";
      PrintWriter pw = null;
      try{
        pw = response.getWriter();
        while(true){
            String msg = redisMsgQueue.takeMsg();
            if(msg==null) break;
                pw.write(msg);
                pw.flush();


        }

      }catch (Exception e){
        e.printStackTrace();
      }

      return result;
    }

    /**
     * @desc 从redis队列中取出数据
     * @return
     */
    @RequestMapping(value="/kafkaSendMsg",method = RequestMethod.GET,produces = "application/json")
    public String kafkaSendMsg(HttpServletResponse response){
        String result = "发送消息";
        for(int i=1;i<=5000;i++){
            Integer partition = i%2;
            logProducer.send("order-msg-"+i,partition);
        }
        logProducer.kafkaMsgTtl();
        return result;
    }

    @RequestMapping(value="/testThread",method = RequestMethod.GET,produces = "application/json")
    public String testThread(){
      String result = "===测试线程池阻塞状态===";
        ExecutorService exec = Executors.newFixedThreadPool(5);
        ThreadPoolExecutor pool = null;
        pool.getActiveCount();

      return result;
    }

}
