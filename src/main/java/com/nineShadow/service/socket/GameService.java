package com.nineShadow.service.socket;

import com.alibaba.fastjson.JSONObject;
import com.nineShadow.model.Cleaner;
import com.nineShadow.model.Packet;
import com.nineShadow.robotManager.GetPacketRobot;
import com.nineShadow.robotManager.Robot;
import com.nineShadow.robotManager.RobotPool;
import com.nineShadow.robotManager.WalkByRobot;
import com.nineShadow.socket.websocket.WebSocketClient;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

@Service("default")
public class GameService {
    private static final Logger logger = Logger.getLogger(GameService.class);
    public void redPackageCreated(final ChannelHandlerContext ctx,  JSONObject jsonObject){

        jsonObject=(JSONObject)jsonObject.get("a");

        Integer redPacketId=jsonObject.getInteger("packetID");
        String roomName=jsonObject.getString("room");
        Date createTime;
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
           createTime=format.parse(jsonObject.getString("time"));
        } catch (ParseException e) {
            e.printStackTrace();
            createTime=new Date();
            logger.info("jsonobjectttt"+jsonObject);
        }
        logger.info("redPacketId"+redPacketId);

//        callPacketGetter(redPacketId,roomName, createTime);
        if(RobotPool.connectedRobots.containsKey(RobotPool.channelHandlerContext2UserMap.get(ctx))){
            Packet packet=new Packet();
            packet.setTime(createTime);
            packet.setId(redPacketId);
//            this.callCleaner(roomName,packet);
            callPacketGetter(redPacketId,roomName,createTime);

        }



    }
    public void pickedUp(final ChannelHandlerContext ctx,  JSONObject jsonObject){
        try{
            jsonObject=(JSONObject)jsonObject.get("a");
            Integer redPacketId=jsonObject.getInteger("id");
            logger.info("\nredPacketId:"+redPacketId);
            for(String key:RobotPool.redPacketIds.keySet()){
                Map<Integer,Packet>list=RobotPool.redPacketIds.get(key);
                synchronized (list){
                    if(list.containsKey(redPacketId)){
                        list.remove(redPacketId);
                    }

                }

            }

        }catch (ConcurrentModificationException e){
            e.printStackTrace();
        }
    }
    private void callCleaner(String roomName,Packet packet){

        ThreadPoolExecutor cleanerService=(ThreadPoolExecutor)RobotPool.cleanerService;

        Integer playerId;
        synchronized (RobotPool.cleaners){
            synchronized (RobotPool.redPacketIds){
                synchronized ( RobotPool.cleaners){
                    if(packet.getTime()!=null)
                        RobotPool.redPacketIds.get(roomName).put(packet.getId(),packet);
                    for(int i=0;i<Integer.parseInt(RobotPool.redPacketRoomMap.get(roomName).getPackageNums());i++) {
                        while(cleanerService.getActiveCount()>100){
                            System.out.println("阻塞"+cleanerService.getActiveCount()+";"+cleanerService.getQueue().size());
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Cleaner cleaner = new Cleaner();
                        cleaner.setConnected(false);
                        cleaner.setPacketId(packet.getId());
                        cleaner.setRoomName(roomName);
                        playerId=RobotPool.robots.keySet().iterator().next();
                        RobotPool.cleanerRobot.put(playerId,RobotPool.robots.get(playerId));
                        RobotPool.robots.remove(playerId);
                        cleaner.setPlayerId(playerId);
                        RobotPool.cleaners.computeIfAbsent(roomName, k -> new ArrayList<>());
                        RobotPool.cleaners.get(roomName).add(cleaner);

                        WebSocketClient.addMore(playerId);
                        RobotPool.cleanerService.execute(new GetPacketRobot(roomName,playerId));
                    }
                }

            }
        }
    }
    public void notPickedUps(final ChannelHandlerContext ctx, final JSONObject jsonObject){
        List<JSONObject>jsonObjects= (List<JSONObject>) jsonObject.get("a");
        logger.info("RobotPool.connectedRobots.containsKey(RobotPool.channelHandlerContext2UserMap.get(ctx))"+RobotPool.connectedRobots.containsKey(RobotPool.channelHandlerContext2UserMap.get(ctx)));
        if(RobotPool.connectedRobots.containsKey(RobotPool.channelHandlerContext2UserMap.get(ctx)))
            for(JSONObject json:jsonObjects){

                logger.info("json:"+json);
                if(json.get("room")!=null){
                    String roomName=json.get("room").toString();
                    Integer packetId=new Integer(json.get("packetID").toString());
                    Date createTime;
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

                        createTime=format.parse(json.getString("time"));
                    } catch (Exception e) {
                        logger.info("josnssssss:"+json);

                        e.printStackTrace();
                        createTime=new Date();
                    }
                    Packet packet=new Packet();
                    packet.setTime(createTime);
                    packet.setId(packetId);
//                    this.callCleaner(roomName,packet);

                    callPacketGetter(packetId,roomName,createTime);
                }

                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


    }
    private void callPacketGetter(Integer packetId,String roomName,final Date time){
        Packet packet=new Packet();
        packet.setId(packetId);
        packet.setTime(new Date());

        final Map<Integer,Packet> packets=RobotPool.redPacketIds.get(roomName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepTime=new Date().getTime()-time.getTime();
                try {
                    sleep(6000-sleepTime>0?6000-sleepTime:0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (packets){

                    if(!packets.keySet().contains(packet.getId()))
                        packets.put(packet.getId(),packet);
                }
            }
        }).start();

//        logger.info(""+packets);
    }


}
