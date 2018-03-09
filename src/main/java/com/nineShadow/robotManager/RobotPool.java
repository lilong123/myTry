package com.nineShadow.robotManager;

import com.nineShadow.model.Cleaner;
import com.nineShadow.model.Packet;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.model.RedPackageRoom;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RobotPool {

    public static Integer minutes=1;
    //所有空闲的机器人
    public static Map<Integer, PlayerBase> robots=null;
    //所有巡逻机器人
    public final static Map<Integer, PlayerBase> robotsWalkBy=new HashMap<>();

    public static final Map<String,Date> timeMap=new HashMap<>();

    //所有正在尝试链接的机器人
    final public static Map<Integer, PlayerBase> connectingRobots=new HashMap<>();
    //所有链接了的机器人
     public static Map<Integer,PlayerBase> connectedRobots=new HashMap<>();
    //userId到ChannelHandlerContext的映射
    final public static Map<Integer,ChannelHandlerContext> channelHandlerContextMap=new HashMap<>();
    //ChannelHandlerContext到userId的映射
    final public static Map<ChannelHandlerContext,Integer> channelHandlerContext2UserMap=new HashMap<>();
    /**
     * 线程池
     */
    public static ScheduledExecutorService scheduledExecutorService = Executors
            .newScheduledThreadPool(15* 2);
    /**
     * 线程池
     */
    public static ScheduledExecutorService manageService = Executors
                    .newScheduledThreadPool( 1);
    /**
     * 抢红包机器人线程池
     */
    public static ScheduledExecutorService cleanerService = Executors
            .newScheduledThreadPool(100* 1);
    /**
     * 抢红包机器人线程池
     */
    public static ScheduledExecutorService inRoomService = Executors
            .newScheduledThreadPool(100);
    //机器人所在的房间
    public static final Map<Integer,String> robotRoomMap=new HashMap<>();
    //机器人开关
    public static boolean robotOn=true;
    public static float sendP=0.0f;
    public static long getPacketSeconds=7;
    public static Map<Integer,PlayerBase>  cleanerRobot =new HashMap<>();
    public static boolean robotOn1=true;


    public static  long getPeopleNumInThisRoom(String room){
        return robotRoomMap.values().stream().filter(room::equals).count();
    }

//    public static final List<String> roomNameList= Arrays.asList("大厅一","大厅二","大厅三",
//            "大厅四","大厅五","大厅六","大厅七","大厅八","大厅九","大厅十");
    public static final List<String> roomNameList= new ArrayList<>();
    //每个房间容纳最大机器人
    public static long maxNumPerRoom=1;
    //机器人所在的房间
    public static Map<String,RedPackageRoom> redPacketRoomMap;

    /**
     * 红包id<房间名，红包ID>
     */
    public static Map<String,Map<Integer,Packet>> redPacketIds=new HashMap<>();
    public static Map<Integer,String> hashResponse=new HashMap<>();
    public static Map<Integer,Robot> robotThreads=new HashMap<>();
    public static String autoSendSignal="=";
    public static String getSignal="#";

    //清理房间中的红包
    public static Map<String,List<Cleaner>> cleaners=new HashMap<>();
    //清理房间中的红包
    public static Map<Integer,String> cleaner2RoomName=new HashMap<>();

}
