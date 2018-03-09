package com.nineShadow.robotManager;

import com.nineShadow.model.PlayerBase;
import com.nineShadow.model.RedPackageRoom;
import com.nineShadow.service.socket.GameService;
import com.nineShadow.socket.websocket.WebSocketClient;
import com.nineShadow.socket.websocket.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Robot implements Runnable {
    public static int minutes=3;
    public Integer playerId;
    public boolean running=true;
    private static final Logger logger = Logger.getLogger(Robot.class);
    public Robot(){

    }
    public Robot(Integer playerId){
        logger.info(playerId);
        this.playerId=playerId;

    }
    @Override
    public void run() {
        logger.info(playerId+",start...");
        try{

            logger.info("loginnnnnnnnnnnnnnnn");
            WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),"login/login",
                    new Object[]{playerId+"",RobotPool.connectedRobots.get(playerId).getPassword()+""});
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getStackTrace());
            }

            getInRoom();

            Date startTime=new Date();
            logger.info("RobotPool.robotOn&&running&&new Date().getTime()-startTime.getTime()<=1000*60*minutes"+(RobotPool.robotOn&&running&&new Date().getTime()-startTime.getTime()<=1000*60*minutes));
            if(RobotPool.robotOn&&running){
                logger.info("发红包");

//                sendRedPacket();


            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            logger.info(playerId+"再活"+RobotPool.minutes+"分钟");
            try {
                sleep(new Random().nextInt(1000*60*RobotPool.minutes)+1000*29);
            } catch (InterruptedException e) {
                logger.error(e.getStackTrace());
            }
            if(!RobotPool.robotOn)
                running=false;
            RobotKiller.shutdown(running,playerId);



            logger.info(playerId+",end...");
        }
    }



    private void getInRoom() {
        logger.info("进入进入房间接口");
        int random;
        synchronized (RobotPool.robotRoomMap){
            random=new Random().nextInt(RobotPool.roomNameList.size());
            random=7;
            int i=0;
            while(RobotPool.getPeopleNumInThisRoom(RobotPool.roomNameList.get(random))>=RobotPool.maxNumPerRoom){
                if(i>=RobotPool.roomNameList.size()){
                    running=false;
                    return;
                }
                random=(random+1)%RobotPool.roomNameList.size();
                i++;
            }
            RobotPool.robotRoomMap.put(playerId,RobotPool.roomNameList.get(random));
        }
        WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
                "bombGame/getInRoom",new Object[]{RobotPool.roomNameList.get(random)});
    }
//    private void sendRedPacket() {
//        synchronized (RobotPool.robotRoomMap){
//            int money=0;
//            final StringBuffer boom=new StringBuffer();
//            logger.info("room:"+playerId);
//            logger.info("RobotPool.robotRoomMap.get(playerId):"+RobotPool.robotRoomMap.get(playerId));
//
//            RedPackageRoom room=RobotPool.redPacketRoomMap.get(RobotPool.robotRoomMap.get(playerId));
//            logger.info("room:"+room);
//            money=room.getMinMoney()+new Random().nextInt(room.getMaxMoney()-room.getMinMoney());
//            int packageNum=new Integer(room.getPackageNums());
//            String []numbers=room.getBombNums().split(",");
//            int boombNumbers =new Integer(numbers[new Random().nextInt(numbers.length)]);
//            if(boombNumbers>5||boombNumbers<3){
//                boombNumbers=3+new Random().nextInt(3);
//            }
//            List<Integer> bombs=new ArrayList<>();
//            for(int i=0;i<boombNumbers;i++){
//                Integer random=new Random().nextInt(10);
//                int round=0;
//                for( int index=0;index<100;index++){
//                    if(!bombs.contains(random)){
//                        break;
//                    }
//                    if(round>=10){
//                        random=(round+1)%10;
//                    }else{
//                        random=new Random().nextInt(10);
//                    }
//                    round++;
//                    if(index>90)
//                        return;
//                }
//
//
//                bombs.add(random);
//            }
//            bombs.forEach(x->boom.append(x+","));
//            WebSocketClientHandler.sendByWebSocket(RobotPool.channelHandlerContextMap.get(playerId),
//                    "bombGame/sendRedPackage",new Object[]{money,boom.toString().substring(0,boom.toString().length()-1),packageNum});
//        }
//
//    }

}
