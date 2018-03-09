package com.nineShadow.robotManager;

import com.nineShadow.model.Cleaner;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.socket.websocket.LocalMemory;
import com.nineShadow.socket.websocket.WebSocketClient;
import com.nineShadow.socket.websocket.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class RobotStarter {
    private static final Logger logger = Logger.getLogger(WebSocketClient.class);

    private  static  Integer index=0;
    public static boolean bindRobot2Channal(final ChannelHandlerContext ctx){
        PlayerBase playerBase=null;
        Cleaner cleaner=needCleaner();
        synchronized (RobotPool.connectingRobots){
            synchronized (RobotPool.channelHandlerContextMap) {
                logger.info("indexxxxxxx:" + index++);
                Integer id = RobotPool.connectingRobots.keySet().iterator().next();
                playerBase = RobotPool.connectingRobots.get(id);
                if(playerBase==null){
                   playerBase= LocalMemory.playerMapper.selectByPrimaryKey(id);
                }
                if(playerBase!=null)
                    RobotPool.connectingRobots.remove(id);
                else{
                    throw new RuntimeException("空指针！id:"+id+"RobotPool.connectingRobots"+RobotPool.connectingRobots);
                }
                RobotPool.connectedRobots.put(id,playerBase);
                RobotPool.channelHandlerContextMap.put(id, ctx);
                RobotPool.channelHandlerContext2UserMap.put(ctx, id);
                logger.info("xxxxxxxxxxxxxxx登录");
                WebSocketClientHandler.sendByWebSocket(ctx, "login/login",
                        new Object[]{playerBase.getId() + "", playerBase.getPassword() + ""});
                RobotPool.inRoomService.execute(new WalkByRobot(id));
                return true;
            }
        }

    }

    private static void setCleaner() {

    }

    private static Cleaner needCleaner() {
        for(List<Cleaner> cleaners:RobotPool.cleaners.values()){
            if(cleaners!=null&&cleaners.size()>0){
                for(Cleaner cleaner:cleaners){
                    if(!cleaner.getConnected())
                        return cleaner;
                }
            }
        }
        return null;
    }
}
