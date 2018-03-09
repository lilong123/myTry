package com.nineShadow.socket.websocket;

import com.nineShadow.dao.NiuniuRoomMapper;
import com.nineShadow.dao.PlayerBaseMapper;
import com.nineShadow.dao.RedPackageRoomMapper;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.model.RedPackageRoom;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class LocalMemory {
    public static PlayerBaseMapper playerMapper;
    public static NiuniuRoomMapper niuroomMapper;
    public static RedPackageRoomMapper redPackageRoomMapper;

    public static void init(){
        playerMapper=  SpringUtil.getBean(PlayerBaseMapper.class);
        niuroomMapper= SpringUtil.getBean(NiuniuRoomMapper.class);
        redPackageRoomMapper= SpringUtil.getBean(RedPackageRoomMapper.class);
    }
    //所有空闲的机器人
    final  public static Map<Integer, PlayerBase> robots=new HashMap<>();
    //所有加入比赛的机器人
    final public static Map<Integer, PlayerBase>gamingRobots=new HashMap<>();
    //所有正在尝试链接的机器人
    final public static Map<Integer, PlayerBase> connectingRobots=new HashMap<>();
    //所有链接了的机器人
    final public static Map<Integer,PlayerBase> connectedRobots=new HashMap<>();
    //机器人最小ID
    public static Integer robotIdMin=100200;
    //机器人最大ID
    public static Integer robotIdMax=100999;
    //机器人三公最小下注
    public static Integer sangongMinScore=1;
    //机器人三公最大下注
    public static Integer sangongMaxScore=200;
    //机器人三公最小下注时间
    public static Integer sangongMinScoreTime=1000;
    //机器人三公最大下注时间
    public static Integer sangongMaxScoreTime=5000;
    //机器人三公亮牌最大时间
    public static Integer sangongMaxLoudPaiTime=3000;
    //机器人牛牛确认最大时间
    public static Integer niuniuMaxLoudPaiTime=3000;
    //机器人抢庄的可能性
    public static Double wantBankerRatio=1.0;
    //机器人抢庄最大权重
    public static int robotBankerMax=3;
    //机器人抢庄最小权重
    public static int robotBankerMin=1;
    //机器人抢庄最大时间
    public static int robotBankerMaxTime=8000;
    //机器人抢庄最小时间
    public static int robotBankerMinTime=5000;
    //机器人牛牛最大下注时间
    public static int betMaxTime=10000;
    //机器人牛牛最小下注时间
    public static int betMinTime=1000;
    //机器人牛牛最大下注
    public static int betMax=10;
    //机器人牛牛最小下注
    public static int betMin=1;
    //机器人牛牛小结算确认时间
    public static int resultComfirmTime=3000;
    public  static Boolean isRobot(Integer id){
        return id>= LocalMemory.robotIdMin&&id<= LocalMemory.robotIdMax;
    }

    public  static Boolean isRedPacketRobot(PlayerBase playerBase){

        return playerBase.getId()>100183&&playerBase.getId()<100639;

    }

    public  static Boolean isRedPacketRobotIn(PlayerBase playerBase){

        return playerBase.getId()>100095&&playerBase.getId()<100183;

    }

    public  static Boolean isNiuNiuRobot(PlayerBase playerBase){
        //TODO
        return false;

    }

}
