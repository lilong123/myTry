package com.nineShadow.robotManager;

import com.nineShadow.model.PlayerBase;
import com.nineShadow.socket.websocket.LocalMemory;
import com.nineShadow.socket.websocket.WebSocketClient;
import com.nineShadow.springboot.Application;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class RobotManager implements Runnable {
    private static final Logger logger = Logger.getLogger(Robot.class);

    @Override
    public void run() {
        ThreadPoolExecutor serviceWalerBy=(ThreadPoolExecutor)RobotPool.inRoomService;
        ThreadPoolExecutor watcher=(ThreadPoolExecutor)RobotPool.scheduledExecutorService;
        try {
            sleep(10000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        while(true){
//            logger.info("running...");
//            try {
//                sleep(1000);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            if(RobotPool.robots.size()<10||RobotPool.connectedRobots.size()<10||watcher.getQueue().size()>0){
//                RobotPool.robotOn=false;
//                for (ChannelHandlerContext ctx:RobotPool.channelHandlerContextMap.values()){
//                    logger.info("主动关闭");
//                    try{
//                        ctx.close();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//            if(RobotPool.robotOn1){
//                if(!RobotPool.robotOn&&watcher.getActiveCount()<=1&&serviceWalerBy.getActiveCount()<=1){
//                    ThreadPoolExecutor service=(ThreadPoolExecutor)RobotPool.cleanerService;
//                    if((service.getActiveCount()<=30&&RobotPool.connectedRobots.size()==0 )||watcher.getActiveCount()<=0){
//                        for (ChannelHandlerContext ctx:RobotPool.channelHandlerContextMap.values()){
//                            logger.info("主动关闭");
//                            try{
//                                ctx.close();
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                        RobotPool.connectedRobots=new HashMap<>();
//                        int size=RobotPool.connectingRobots.size();
//                        synchronized (RobotPool.connectingRobots){
//                            for(int i=0;i<size;i++){
//                                if(RobotPool.connectingRobots.keySet().size()>0)
//                                    RobotPool.connectingRobots.remove(RobotPool.connectingRobots.keySet().iterator().next());
//                            }
//                        }
//
//                        RobotPool.robotOn=true;
//                        logger.info("robotOn");
//                        WebSocketClient.start(Application.ip);
//                        try {
//                            sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//
//                }
//            }
//
//
//            try {
//                sleep(100);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
