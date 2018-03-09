package com.nineShadow.robotManager;

import com.nineShadow.model.Cleaner;
import com.nineShadow.model.Packet;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.model.RedPackageRoom;
import com.nineShadow.socket.websocket.WebSocketClientHandler;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class WalkByRobot implements Runnable {
    private Integer playerId;
    private static final Logger logger = Logger.getLogger(WalkByRobot.class);

    private String roomName;



    public  WalkByRobot(Integer playerId){
        this.playerId=playerId;
    }
    @Override
    public void run() {

        try {
            sleep(new Random().nextInt(6000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!RobotPool.robotOn)
            return;
        try{
            logger.info("\n开始roomName:"+roomName);
            getInRoom();

            try {
                sleep(new Random().nextInt(60000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (1==1)
//                throw  new RuntimeException("onpppppppppp");
            synchronized (RobotPool.timeMap){
                if(RobotPool.timeMap.get(roomName)==null||new Date().getTime()-RobotPool.timeMap.get(roomName).getTime()>RobotPool.minutes*60*1000)
                {
                    //发红包
                    sendRedPacket();
                    RobotPool.timeMap.put(roomName,new Date());
                    throw new RuntimeException("换人");
                }
            }
            Collection<Packet> packages=RobotPool.redPacketIds.get(this.roomName).values();
            logger.info("packages:"+packages);
            while(RobotPool.robotOn){
                //抢红包
                pickupRedPacket();
                sleep(1000+new Random().nextInt(1000));
                synchronized (RobotPool.timeMap){
//                        logger.info("\nRobotPool.timeMap:"+RobotPool.timeMap);
                    if(RobotPool.timeMap.get(roomName)==null||new Date().getTime()-RobotPool.timeMap.get(roomName).getTime()>RobotPool.minutes*60*1000)
                    {

//                            logger.info("\nRobotPool.timeMap:"+RobotPool.timeMap);
                        //发红包
                        sendRedPacket();
                        RobotPool.timeMap.put(roomName,new Date());
                        throw new RuntimeException("换人");
                    }
                }
//                    logger.info("\ntimeMap:"+timeMap);


            }


        }catch (Exception e){
            logger.error("\nplayerId:"+playerId+"roomName:"+roomName+";channelHandler"+RobotPool.channelHandlerContextMap.get(playerId));
            synchronized (map){
                map.put(roomName,map.get(roomName)-1);
            }
            e.printStackTrace();
            synchronized (RobotPool.connectedRobots){
                synchronized (RobotPool.robots){
                    PlayerBase playerBase=RobotPool.connectedRobots.remove(playerId);
                    RobotPool.robots.put(playerId,playerBase);
                }
            }

            RobotKiller.shutdown(RobotPool.robotOn,playerId);
        }
        logger.info("shutdownnnnnnnnnnnnnnnn");
    }
    private List<Integer> redPacketIds=new ArrayList<>();
    private void pickupRedPacket() {

        synchronized (RobotPool.redPacketIds.get(roomName)){
            for(Packet packet:RobotPool.redPacketIds.get(roomName).values()){
                if(redPacketIds.contains(packet.getId()))
                    continue;
                if(new Date().getTime()-packet.getTime().getTime()>6000){
                    logger.info("\nRobotPool.redPacketIds.get(roomName):"+RobotPool.redPacketIds.get(roomName));

                    WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                            "bombGame/pickRedPackage",new Object[]{""+packet.getId()});
                    redPacketIds.add(packet.getId());
                }

                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void sendRedPacket() {
        synchronized (RobotPool.robotRoomMap){
            int money=0;
            final StringBuffer boom=new StringBuffer();

            RedPackageRoom room=RobotPool.redPacketRoomMap.get(roomName);
            logger.info("\nroom:"+room);
            money=room.getMinMoney()+new Random().nextInt(room.getMaxMoney()-room.getMinMoney());
            int packageNum=new Integer(room.getPackageNums());
            String []numbers=room.getBombNums().split(",");
            int boombNumbers =new Integer(numbers[new Random().nextInt(numbers.length)]);
            logger.info("\nboombNumbers:"+boombNumbers);

            if(numbers.length>1&&(boombNumbers>5||boombNumbers<3)){
                boombNumbers=3+new Random().nextInt(3);
            }
            List<Integer> bombs=new ArrayList<>();
            for(int i=0;i<boombNumbers;i++){
                Integer random=new Random().nextInt(10);
                int round=0;
                for( int index=0;index<100;index++){
                    if(!bombs.contains(random)){
                        break;
                    }
                    if(round>=10){
                        random=(round+1)%10;
                    }else{
                        random=new Random().nextInt(10);
                    }
                    round++;
                    if(index>90)
                        return;
                }


                bombs.add(random);
            }
            bombs.forEach(x->boom.append(x+","));
            WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                    "bombGame/sendRedPackage",new Object[]{money,boom.toString().substring(0,boom.toString().length()-1),packageNum});
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private boolean pickUp(Packet packet,String roomName) {
        if(packet!=null){
            WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                    "bombGame/getInRoom",new Object[]{roomName});
            long time=new Date().getTime()-packet.getTime().getTime();
            if(time<6000)
                try {
                    sleep(6000-time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            else{
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                    "bombGame/pickRedPackage",new Object[]{""+packet.getId()});
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }else
            return false;
    }
    public static final  Map<String,Integer> map=new HashMap<>();
    private void getInRoom() {
        logger.info("进入进入房间接口");
        int size=RobotPool.roomNameList.size();
        int random=new Random().nextInt(size);
        map.putIfAbsent(RobotPool.roomNameList.get(random),0);
        map.putIfAbsent(RobotPool.roomNameList.get(1),0);
        synchronized (map){
            int i=0;
            while(map.get(RobotPool.roomNameList.get(random))>=10){
                random=(random+1)%size;
                logger.info("重复："+ (i++)+"次");

                if(i>size)
                    throw new RuntimeException("重复过多");
            }
//            random=1;
            roomName=RobotPool.roomNameList.get(random);
            map.put(roomName,map.get(roomName)+1);
        }

        WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                "bombGame/getInRoom",new Object[]{this.roomName});
        try {

            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getRandom(List<String> list) {
        synchronized (map){
            for(String st:list){
                map.putIfAbsent(st,0);
                if(map.get(st)<9){
                    map.put(st,map.get(st)+1);
                    return st;
                }
            }
        }
        return list.get(0);
    }
}
