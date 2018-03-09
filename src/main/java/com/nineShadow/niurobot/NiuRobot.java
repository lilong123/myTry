package com.nineShadow.niurobot;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import com.nineShadow.util.DbThreads;
import com.nineShadow.util.HttpRequestUtil;
import com.nineShadow.util.JsonUtil;
import com.nineShadow.util.MyTimerTask;



public class NiuRobot extends WebSocketClient{
	/**
	 * 线程安全的数据队列
	 */
	private Queue<String> msgs=new  ConcurrentLinkedQueue<>();
	/**
	 * 日志打印对象
	 */
	private Logger logger=Logger.getLogger(this.getClass());
	/**
	 * 机器人id
	 */
	private Integer id;
	/**
	 * 机器人密码
	 */
	private String pwd;
	/**
	 * 机器人所在的房间编号
	 */
	private String roomId;
	/**
	 * 上一次进的房间id
	 */
	private String lastRoomId="";
	/**
	 * 是否在桌子上
	 */
	private boolean isOnDesk=false;
	/**
	 * 是否可以退桌/退房，在房间但没有准备
	 */
	private boolean isCouldQuit=true;
	/**
	 * 是否到必须换房时间
	 */
	private boolean isMustChangeRoom=false;
	/**
	 * 
	 */
	private boolean isInRoom=false;
	
	private MyTimerTask changeRoomTask;
	
	private Thread HandleMsgThread=new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//系统被开启，系统被关闭后当前游戏还没有结束
			while(NiuRobotSystem.isOpenRobot || (!NiuRobotSystem.isOpenRobot && (!isCouldQuit && isInRoom))){
				String msg=msgs.poll();
				if(msg==null){
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				HandleMsg(msg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info(id+"机器人即将关闭");
			if(changeRoomTask!=null){
				changeRoomTask.cancel();
			}
//			try {
////				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			close();
		}
	});
	
	public NiuRobot(URI uri,int id,String pwd) {
		super(uri,new Draft_17());
		// TODO Auto-generated constructor stub
		this.id=id;
		this.pwd=pwd;
		//开始数据处理
		HandleMsgThread.start();
	}
	/**
	 * 数据处理
	 * @param msg
	 */
	public void HandleMsg(String msg){
		Map<String,Object> callback=JsonUtil.StringtoMap(msg);
		String method=(String) callback.get("m");
		@SuppressWarnings("unchecked")
		Map<String,Object> params=(Map<String, Object>) callback.get("a");
		if(method!=null && params!=null){
			switch(method){
			case "login/login":{GetRoomList(params);}break;
			case "niugame/getNiuNiuRoomList":{IntoRoom(params);}break;
			case "<NiuroomInfo>":{WhetherNeedQuit(params);}break;//进入房间后判断人数，人数过多退出
			case "<canInTable>":{GrabTable(params);};break;
			case "niugame/wantInTable":{WhetherGrabDeskSuccess(params);}break;
			case "<sendnewplayer>":{WhetherNeedReady_NewPlayer(params);}break;
//			case "<deskChange>":{WhetherNeedReady_DeskChange(params);}break;
			case "<roomcountchange>":{
				logger.info("isCouldQuit="+isCouldQuit);
				if(isCouldQuit){
					WhetherNeedQuitRoom((int)params.get("onlinecount"));
					}
				}break;
			case "<canRob>":{GrabBranker(params);}break;
			case "<robResult>":{Bet(params);}break;
			case "<chooseniu>":{ChooseNiu(params);}break;
			case "<isMoneyEnough>":{InitNextGame(params);}break;
			case "<reconnect>" :{Reconnected(params);}break;
			case "niugame/wantQuitRoom":{CheckQuitRoom(params);}break;
			}
		}else{
			logger.info("服务端参数异常");
		}
	}
	/**
	 * 登录
	 */
	private void login(){
		send(new Object[]{id,pwd}, "login/login");
	}
	/**
	 * 获取牛牛大厅
	 * @param params
	 */
	private void GetRoomList(Map<String,Object> params){
		//Map<String,Object> callback=JsonUtil.StringtoMap(params);
		if(NiuRobotSystem.isOpenRobot){
			if(params.containsKey("success") && params.get("success").equals(1)){
				send(new Object[]{}, "niugame/getNiuNiuRoomList");
				logger.info(id+"登录成功，获取牛牛大厅");
				return;
			}
			logger.info(id+"登录失败");
		}else{
			logger.info(id+"机器人已被关闭");
		}
	}
	/**
	 * 进入房间
	 * @param params
	 */
	private  void IntoRoom(Map<String,Object> params){
			if(params.containsKey("success") && params.get("success").equals(1)){
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> rooms=(List<Map<String, Object>>) params.get("roomList");
				Random random=new Random();
				List<String> roomIds=new ArrayList<>(); 
				while(true){
					if(roomIds.size()>=rooms.size()){
//						logger.info("所有房间都已经不需要进入机器人了");
						//没有房间可进，进入等待循环等待，直到有房间可进或者线程被终止
						WaitAndGetRoomList();
						return;
					}
					int index=random.nextInt(rooms.size());
					String roomId=(String) rooms.get(index).get("roomID");
					int onlinecount=(int) rooms.get(index).get("onlineCount");
					logger.info(roomId+"该房间的机器人人数为"+NiuRobotSystem.RoomRobots.get(roomId).size());
					if(onlinecount>0 && onlinecount<NiuRobotSystem.MaxCount  && !roomId.equals(lastRoomId)){
						synchronized (NiuRobotSystem.RoomRobots) {
							if (NiuRobotSystem.RoomRobots.get(roomId).size() < NiuRobotSystem.MaxCount) {
								NiuRobotSystem.RoomRobots.get(roomId).add(id);
								send(new Object[] { roomId }, "niugame/intoNiuNiuRoom");
								this.roomId = roomId;
								logger.info(id + "进入房间");
								return;
							}
						}
					}else{
						roomIds.add(roomId);
					}
				}
			}
			logger.info(id+"获取房间列表失败");
	}
	/**
	 * 判断房间人数是否允许机器人在里面
	 * @param params
	 */
	private void WhetherNeedQuit(Map<String,Object> params){
		if(params.containsKey("onlineCount")){
			int onlinecount=(int) params.get("onlineCount");
			if(!WhetherNeedQuitRoom(onlinecount)){
				//这表示真正的进入了房间
				isInRoom=true;
				//真正的进入房间后，在准备之前，可以退出房间
				isCouldQuit=true;
				//如果机器人确实进入了房间，则开启退出房间的倒计时(倒计时结束时，机器人必须退出房间，进入其他房间)
				DbThreads.executor(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(isInRoom && isCouldQuit && !isOnDesk){
							send(new Object[]{roomId}, "niugame/wantInTable");
							isOnDesk=true;	
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				logger.info(id+"开启倒计时");
				changeRoomTask=new MyTimerTask(NiuRobotSystem.ChangeTime) {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						logger.info(id+"倒计时结束"+"isCouldQuit="+isCouldQuit);
						if(isCouldQuit){
							QuitRoom();
						}else{
							isMustChangeRoom=true;
						}
					}
				};
				changeRoomTask.starttimer();
			}
		}else{
			logger.info(id+" 获取房间信息异常");
		}
	}
	/**
	 * 抢桌
	 * @param params
	 */
	private void GrabTable(Map<String,Object> params){
		logger.info(id+"的抢桌状态isOnDesk="+isOnDesk);
		if(!isOnDesk){
			send(new Object[]{this.roomId}, "niugame/wantInTable");
			isOnDesk=true;	
		}
	}
	/**
	 * 是否抢桌成功
	 * @param params
	 */
	private void WhetherGrabDeskSuccess(Map<String,Object> params){
		if(params.containsKey("success") && params.get("success").equals(1)){
			boolean havePersonReadyed=false;
			if(params.containsKey("list")){
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> list=(List<Map<String, Object>>) params.get("list");
				for(Map<String,Object>l:list){
					if(l.get("ready").equals(true)){
						havePersonReadyed=true;
						break;
					}
				}
			}
			logger.info("isOnDesk"+isOnDesk);
			logger.info("havePersonReadyed"+havePersonReadyed);
			logger.info("isCouldQuit"+isCouldQuit);
			if(isOnDesk && havePersonReadyed && isCouldQuit){
				send(new Object[]{this.roomId}, "niugame/beReady");
				isCouldQuit=false;
			}
		}else{
			logger.info(id+"抢桌失败！");
			isOnDesk=false;
		}
	}
	/**
	 * 有人有新的操作判断是否要准备
	 * @param params
	 * @return
	 */
	private boolean WhetherNeedReady_NewPlayer(Map<String,Object> params){

		boolean havePersonReadyed=false;
		if(params.containsKey("ready")){
			havePersonReadyed=(boolean) params.get("ready");
		}
		//机器人必须自己在桌上，并且有人准备，并且自己可准备（即可以随时退出的状态）
		logger.info(id+"\tisOnDesk="+isOnDesk+"\thavePersonReadyed="+havePersonReadyed+"\tisCouldQuit="+isCouldQuit);
		if(isOnDesk && havePersonReadyed && isCouldQuit){
			send(new Object[]{this.roomId}, "niugame/beReady");
			isCouldQuit=false;
			return true;
		}
		return false;
	}
	/**
	 * 有座位变动判断是否要准备
	 * @param params
	 * @return
	 */
//	private boolean WhetherNeedReady_DeskChange(Map<String,Object> params){
//		boolean havePersonReadyed=false;
//		@SuppressWarnings("unchecked")
//		List<Map<String,Object>> list=(List<Map<String, Object>>) params.get("list");
////		logger.info(id+"座位变动时，list人数="+list.size());
//		for(Map<String,Object> l:list){
//			if(l.get("ready").equals(true)){
//				havePersonReadyed=true;
//				break;
//			}
//		}
//		//机器人必须自己在桌上，并且有人准备，并且自己可准备（即可以随时退出的状态）
////		logger.info(id+"\tisOnDesk="+isOnDesk+"\thavePersonReadyed="+havePersonReadyed+"\tisCouldQuit="+isCouldQuit);
//		if(isOnDesk && havePersonReadyed && isCouldQuit){
//			send(new Object[]{this.roomId}, "niugame/beReady");
//			isCouldQuit=false;
//			return true;
//		}
//		return false;
//	}
	/**
	 * 抢庄
	 * @param params
	 */
	private void GrabBranker(Map<String,Object> params){
		int maxnum=(int) params.get("maxnum");
		logger.info(id+"得最大可抢倍数为"+maxnum);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(maxnum>0 && maxnum<=4){
			Random random=new Random();
			int bet=random.nextInt((maxnum+1));
			send(new Object[]{this.roomId,bet+""}, "niugame/rob");
		}else if(maxnum==0){
			send(new Object[]{this.roomId,"0"},"niugame/rob");
		}else if(maxnum>4){
			send(new Object[]{this.roomId,"0"},"niugame/rob");
		}else{
			logger.info(id+"你不在桌上无法抢庄");
		}
	}
	/**
	 * 下注
	 * @param params
	 */
	private void Bet(Map<String,Object> params){
		int maxnum=(int) params.get("maxbet");
		logger.info(id+"得最大可抢下注倍数为"+maxnum);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(maxnum>1 && maxnum<=5){
			Random random=new Random();
			int bet=random.nextInt(maxnum)+1;
			send(new Object[]{this.roomId,bet+""},"niugame/bet");
		}else if(maxnum==1){
			send(new Object[]{this.roomId,"1"},"niugame/bet");
		}else if(maxnum>5){
			send(new Object[]{this.roomId,"1"},"niugame/bet");
		}else{
			logger.info(id+"你当前不能下注");
		}
	}
	
//	public static void main(String[] args) {
//		int maxnum=5;
//		while(true){
//			Random random=new Random();
//			int bet=random.nextInt(maxnum)+1;
//			System.out.println("下注倍数="+bet);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	/**
	 * 翻牌
	 * @param params
	 */
	private void ChooseNiu(Map<String,Object> params){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		send(new Object[]{this.roomId},"niugame/chooseNiu");
	}
	/**
	 * 断线重连的时候，修改机器人状态
	 * @param params
	 */
	private void Reconnected(Map<String,Object> params){
		if(params.containsKey("state") && params.get("state").equals(0)){
			isOnDesk=true;
		}
	}
	/**
	 * 初始化  isCouldQuit和isOnDesk两个状态参数
	 * @param params
	 */
	private void InitNextGame(Map<String,Object>params){
		isCouldQuit=true;
//		//如果机器人已经关闭，则所有机器人玩玩本局之后退出房间
//		if(NiuRobotSystem.isOpenRobot){
//			QuitRoom();
//			return;
//		}
		//每一把结束时判断是否到了退出该房间的时间
		if(isMustChangeRoom){
			//如果到了必须退出的时间就退出
			QuitRoom();
			return;
		}
		int sitNum=(int) params.get("ownsitNum");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list=(List<Map<String, Object>>) params.get("list");
		if(sitNum!=-1){
			for(Map<String,Object> l:list){
				if(l.get("sitNum").equals(sitNum) && l.get("isenough").equals(0)){
					isOnDesk=false;
					HttpRequestUtil.sendGet(NiuRobotSystem.ChangeMoneyUrl,"id="+id+"&goldcount=5000&money=5000");
					break;
				}
			}
		}else{
			logger.info(id+"你不在桌上");
		}
	}
	/**
	 * 检验是否真的退出房间了
	 * @param params
	 */
	private void CheckQuitRoom(Map<String,Object>params){
		if(params.containsKey("success")){
			if(params.get("success").equals(1)){
				//如果真的退了，则开始获取其他房间列表进入
				this.lastRoomId=this.roomId;
				NiuRobotSystem.RoomRobots.get(roomId).remove(id);
				isInRoom=false;
				this.roomId=null;
				WaitAndGetRoomList();
			}else{
				//如果没有退出，等待500ms后重新退出
				try {
					Thread.sleep(500);
					QuitRoom();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			logger.info(id+"退出房间异常");
		}
	}
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub
		logger.info(id+"连接异常……");
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		if(message.substring(0, 1).equals("-")){
			send("+");
			return;
		}
		msgs.offer(message);
		logger.info(id+"收到信息："+message);
	}

	
	@Override
	public void onOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub
		logger.info(id+"开始建立连接……");
		login();
	}
	/**
	 * 发送数据
	 * @param params 参数
	 * @param ApiName API名字
	 */
	public  void send(Object[] params,String ApiName){
		StringBuffer msg=new StringBuffer();
		msg.append("{\"a\":[");
		for(int i=0;i<params.length;i++){
//			System.out.println("param="+params[i]);
			msg.append("\""+params[i].toString()+"\"");
			if(i<params.length-1){
				msg.append(",");
			}
		}
		msg.append("],\"m\":\""+ApiName+"\"}");
//		logger.info("发送的数据为"+msg.toString());
		try {
			send(msg.toString());
		} catch (NotYetConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(id+"号机器人链接已断开");
			HandleMsgThread.interrupt();
		}
	}
	/**
	 * 等待一定时间，重新获取房间列表
	 */
	private void WaitAndGetRoomList(){
		try {
			Thread.sleep(NiuRobotSystem.WaitTime);
			msgs.clear();//将队列中的数据都清除
			send(new Object[]{}, "niugame/getNiuNiuRoomList");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 是否人数过多需要退出房间,或者一个人的时候也退出
	 * @param onlinecount
	 * @return true人数过多，false为人数没有超过上限
	 */
	private boolean WhetherNeedQuitRoom(int onlinecount){
//		if(onlinecount>1 && onlinecount<=NiuRobotSystem.MaxCount){
			return false;
//		}else{
//			logger.info("房间人数已超过预定人数，"+id+"机器人退出");
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			QuitRoom();
//			return true;
//		}
	}
	/**
	 * 退出房间
	 */
	private void QuitRoom(){
		send(new Object[]{this.roomId},"niugame/wantQuitRoom");
		if(changeRoomTask!=null){
			changeRoomTask.cancel();
			changeRoomTask=null;
		}
		isMustChangeRoom=false;
		isOnDesk=false;
		isCouldQuit=false;//已经退出房间了就不能再退出了
	}
}
