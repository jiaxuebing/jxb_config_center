package com.jxb.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/platform")
@Component
public class WebSocketServer {

    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static Map<String,Session> socketMap = new ConcurrentHashMap<>();

    /**
     * @desc 打开连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
      logger.info("########websocket connect success#########");
      try{
          String sessionId = session.getId();
          logger.info("用户：{},连接成功。",sessionId);
          //存储用户session
          socketMap.put(sessionId,session);
          this.sendMsg(session.getId(),"连接成功");
      }catch (Exception e){
          logger.error("#####用户：{},连接出现网络异常！",e);
      }
    }

    /**
     * @desc 监听消息的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session){
      logger.info("#####报文：{}#####",message);
      try{
          String sessionId = session.getId();
          this.sendMsg(sessionId,"收到信息");
      }catch (Exception e){
          e.printStackTrace();
      }
    }

    /**
     * @desc 实现服务器主动推送
     * @param msg
     */
    private void sendMsg(String userId,String msg)throws IOException {
        logger.error("sendMsg:{}",msg);
        Session session = socketMap.get(userId);
        session.getBasicRemote().sendText(msg);
    }

    @OnError
    public void onError(Session session,Throwable t){
        logger.error("用户错误原因:"+t.getMessage());
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session){
        logger.error("用户关闭连接");
        socketMap.remove(session.getId());
    }

    public static void broadCast(String msg){
        try{
            for(Map.Entry<String,Session> entry:socketMap.entrySet()){
                entry.getValue().getBasicRemote().sendText(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
