package com.nineShadow.robotManager;

import com.nineShadow.model.Cleaner;
import com.nineShadow.model.Packet;
import com.nineShadow.socket.websocket.WebSocketClientHandler;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Random;

import static java.lang.Thread.sleep;

public class GetPacketRobot implements Runnable {
    private static final Logger logger = Logger.getLogger(Robot.class);
    private String roomName;
    private Integer robotId;
    public GetPacketRobot (String roomName,Integer robotId){
        this.roomName=roomName;
        this.robotId=robotId;
    }
    @Override
    public void run() {
        logger.info("cleaner thread start...");
        try{


            if(RobotPool.robotOn){
                Date startTime=new Date();
                sleep(6000+new Random().nextInt(3000));

//                pickupRedPacket();



            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }

    private void off() {
        logger.info("offffffffffffffff");
        RobotPool.channelHandlerContextMap.get(robotId).close();
        synchronized (RobotPool.robots){
            synchronized (RobotPool.cleanerRobot){
                RobotPool.robots.put(robotId,RobotPool.cleanerRobot.get(robotId));
                RobotPool.cleanerRobot.remove(robotId);
            }
        }
        logger.info("RobotPool.robots.size()"+RobotPool.robots.size());


    }
}
