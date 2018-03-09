package com.nineShadow.niurobot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nineShadow.springboot.Application;
import org.apache.log4j.Logger;

import com.nineShadow.model.NiuniuRoom;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.socket.websocket.LocalMemory;
import com.nineShadow.util.DbThreads;

public class NiuRobotSystem {
	/**
	 * 每隔多少时间机器人重新获取房间列表 /ms
	 */
	public static final int WaitTime=2000;
	/**
	 * 每隔多长时间，机器人必须进其他房间 /ms
	 */
	public static final long ChangeTime=60000;
//	public static final long ChangeTime=60000000;
	/**
	 * 
	 */
	public static boolean IsShouldBack=false;
	/**
	 * 根据房间现有人数，往房间放入适当人数，房间1人，进3人，房间2人，进2人，房间3人，进1人，桌上四人即以上，不往里放机器人
	 */
	public static final int NeedIntoPersonCount[]={0,3,2,1};
	/**
	 * 房间超多过少人，就不用进机器人了
	 */
	public static final int MaxCount=4;
	/**
	 * 所有机器人
	 */
	private static List<PlayerBase> robots;
	/**
	 * 服务器域名
	 */
	private static final String url= Application.ip;
//	private static final String url="ws://localhost:17014";
	/**
	 * 每个牛牛房间里的机器人id，String为房间的id，Integer是每个机器人的id
	 */
	public static  Map<String,List<Integer>> RoomRobots=new ConcurrentHashMap<>();
	/**
	 * 已经进入某一房间的机器人编号
	 */
	public static List<String> HaveInRoomRobotIds=new LinkedList<String>();
	/**
	 * 在线的机器人
	 */
	private static List<NiuRobot> onlinerobots=new ArrayList<>();
	
	public static boolean isOpenRobot=false;
	/**
	 * 需要的参数 String id,String goldcount,String money
	 */
	public static final String ChangeMoneyUrl="https://www.btgycc.com/bombAndNiuNiu/Player/ModifyGold";
	private static Logger logger=Logger.getLogger(NiuRobotSystem.class);
	
	/**
	 * 初始化机器人
	 */
	public static void initNiuRobot(){
		robots=LocalMemory.playerMapper.getRobotsByIds(100053,100051);
//		robots=LocalMemory.playerMapper.getRobotsByIds(100095, 100089);
		List<NiuniuRoom> rooms=LocalMemory.niuroomMapper.selectAll();
		for(int i=0;i<rooms.size();i++){
			RoomRobots.put(rooms.get(i).getRoomno().toString(),new LinkedList<Integer>());
		}
		logger.info("总共有机器人"+robots.size()+"个，牛牛房间"+RoomRobots.size()+"个");
		//让所有机器人登录
		for(PlayerBase robot:robots){
			DbThreads.executor(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						NiuRobot onlinerobot=new NiuRobot(new URI(url),robot.getId(),robot.getPassword());
						onlinerobots.add(onlinerobot);
						onlinerobot.connect();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public static void CloseNiuRobot(){
		isOpenRobot=false;
	}
	
	public static void OpenNiuRobot(){
		if(!isOpenRobot){
			isOpenRobot=true;
			initNiuRobot();
		}else{
			logger.info("机器人已经开启");
		}
	}
}
