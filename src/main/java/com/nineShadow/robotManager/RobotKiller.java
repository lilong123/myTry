package com.nineShadow.robotManager;

import com.nineShadow.model.PlayerBase;
import com.nineShadow.socket.websocket.WebSocketClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import static java.lang.Thread.sleep;

public class RobotKiller {
    private static final Logger logger = Logger.getLogger(Robot.class);

    public static void shutdown(boolean isFull,Integer playerId) {
        ChannelHandlerContext ctx=RobotPool.channelHandlerContextMap.get(playerId);
        ChannelFuture future=ctx.close();
        try {
            future.get();
        }catch (Exception e){
            logger.error("关闭出错");
        }
        synchronized (RobotPool.channelHandlerContextMap) {
            RobotPool.channelHandlerContext2UserMap.remove(ctx);
            RobotPool.channelHandlerContextMap.remove(playerId);
            RobotPool.robotRoomMap.remove(playerId);
        }
        PlayerBase playerBase=RobotPool.connectedRobots.get(playerId);
        synchronized (RobotPool.connectedRobots){
            synchronized (RobotPool.robots){
                RobotPool.connectedRobots.remove(playerId);
                RobotPool.robots.put(playerId,playerBase);
            }
        }
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e.getStackTrace());

        }
        logger.info("ifFull"+isFull);
        logger.info("RobotPool.robotOn"+RobotPool.robotOn);
        if (isFull)
            for(int i=0;i<1;i++)
                WebSocketClient.addMore();
    }

}
