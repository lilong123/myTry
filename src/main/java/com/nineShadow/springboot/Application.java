package com.nineShadow.springboot;

import com.alibaba.fastjson.JSONObject;
import com.nineShadow.model.PlayerBase;
import com.nineShadow.niurobot.NiuRobotSystem;
import com.nineShadow.robotManager.Robot;
import com.nineShadow.robotManager.RobotManager;
import com.nineShadow.robotManager.RobotPool;
import com.nineShadow.robotManager.WalkByRobot;
import com.nineShadow.socket.websocket.LocalMemory;
import com.nineShadow.socket.websocket.WebSocketClient;
import com.nineShadow.util.JsonUtil;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@EnableAutoConfiguration

@MapperScan("com.nineShadow")//告诉mapper所在的包名
@ComponentScan(basePackages={"com.nineShadow"})
public class Application {

	private Logger logger=Logger.getLogger(this.getClass());
	
    @RequestMapping("/getRobotOn")
    @ResponseBody
    public String getRobotOn(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        map.put("on", RobotPool.robotOn1);
        map.put("on1", RobotPool.robotOn);
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/getThreadInfo")
    @ResponseBody
    public String getThreadInfo(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        ThreadPoolExecutor service;
        if(RobotPool.scheduledExecutorService instanceof ThreadPoolExecutor){
            service=(ThreadPoolExecutor)RobotPool.scheduledExecutorService;
            map.put("activeCount",service.getActiveCount());
            map.put("completedTaskCount",service.getCompletedTaskCount());
            map.put("corePoolSize",service.getCorePoolSize());
            map.put("poolSize",service.getPoolSize());
            map.put("taskCount",service.getTaskCount());
            map.put("queue.size",service.getQueue().size());
        }
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/getCleanerInfo")
    @ResponseBody
    public String getCleanerInfo(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        ThreadPoolExecutor service;
        if(RobotPool.cleanerService instanceof ThreadPoolExecutor){
            service=(ThreadPoolExecutor)RobotPool.cleanerService;
            map.put("activeCount",service.getActiveCount());
            map.put("completedTaskCount",service.getCompletedTaskCount());
            map.put("corePoolSize",service.getCorePoolSize());
            map.put("poolSize",service.getPoolSize());
            map.put("taskCount",service.getTaskCount());
            map.put("queue.size",service.getQueue().size());
        }
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/get")
    @ResponseBody
    public String robotDistribute(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        map.put("connected", RobotPool.connectedRobots.size());
        map.put("connecting", RobotPool.connectingRobots.size());
        map.put("robots", RobotPool.robots.size());
        map.put("connected", RobotPool.connectedRobots.size());
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/inRoomService")
    @ResponseBody
    public String getWalkerBy(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        ThreadPoolExecutor service;
        if(RobotPool.inRoomService instanceof ThreadPoolExecutor){
            service=(ThreadPoolExecutor)RobotPool.inRoomService;
            map.put("activeCount",service.getActiveCount());
            map.put("completedTaskCount",service.getCompletedTaskCount());
            map.put("corePoolSize",service.getCorePoolSize());
            map.put("poolSize",service.getPoolSize());
            map.put("taskCount",service.getTaskCount());
            map.put("queue.size",service.getQueue().size());
        }
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/getManage")
    @ResponseBody
    public String getManage(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map=new HashMap<>();
        ThreadPoolExecutor service;
        if(RobotPool.manageService instanceof ThreadPoolExecutor){
            service=(ThreadPoolExecutor)RobotPool.manageService;
            map.put("activeCount",service.getActiveCount());
            map.put("completedTaskCount",service.getCompletedTaskCount());
            map.put("corePoolSize",service.getCorePoolSize());
            map.put("poolSize",service.getPoolSize());
            map.put("taskCount",service.getTaskCount());
            map.put("queue.size",service.getQueue().size());
        }
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/setSendP")
    @ResponseBody
    public String setSendP(HttpServletRequest request, HttpServletResponse response){
        String p=request.getParameter("p");
        RobotPool.sendP=Float.valueOf(p);
        Map<String,Object> map=new HashMap<>();
        map.put("success", 1);
        map.put("message", "OK");
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/setMinutes")
    @ResponseBody
    public String setMinutes(HttpServletRequest request, HttpServletResponse response){
        String minutes=request.getParameter("minutes");
        RobotPool.minutes=Integer.valueOf(minutes);
        Map<String,Object> map=new HashMap<>();
        map.put("success", 1);
        map.put("message", "OK");
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/testT")
    @ResponseBody
    public String testT(HttpServletRequest request, HttpServletResponse response){
        logger.info("testtttttt");
        List<PlayerBase> playerBaseList=LocalMemory.playerMapper.selectAll();
        Map<Integer, PlayerBase> robotsWalkBy=playerBaseList.stream().filter(LocalMemory::isRedPacketRobotIn)
                .collect(Collectors.toMap(PlayerBase::getId, Function.identity()));
        synchronized (RobotPool.channelHandlerContextMap){
            for(Integer id:robotsWalkBy.keySet()){
                logger.info("关闭："+id);
                RobotPool.channelHandlerContextMap.put(id,null);
            }
        }

        Map<String,Object> map=new HashMap<>();
        map.put("success", 1);
        map.put("message", "OK");
        return JSONObject.toJSONString(map);
    }
    @RequestMapping("/setRobotOn")
    @ResponseBody
    public String setRobotOn(HttpServletRequest request, HttpServletResponse response){
        //新设置状态
        String on=request.getParameter("on");
        //之前一个状态
        Boolean robotOnOld=RobotPool.robotOn;
        RobotPool.robotOn= Boolean.valueOf(on);
        RobotPool.robotOn1= Boolean.valueOf(on);

        if(!robotOnOld&&Boolean.valueOf(on)) {
            Set<Integer> keys=new HashSet<>(RobotPool.robots.keySet());
            for (Integer key:keys){
                RobotPool.robots.remove(key);
            }
            keys=new HashSet<>(RobotPool.connectedRobots.keySet());
            for (Integer key:keys){
                RobotPool.connectedRobots.remove(key);
            }
            keys=new HashSet<>(RobotPool.connectingRobots.keySet());
            for (Integer key:keys){
                RobotPool.connectingRobots.remove(key);
            }
            for(String key:WalkByRobot.map.keySet()){
                WalkByRobot.map.put(key,0);
            }
            WebSocketClient.start(Application.ip);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("success", 1);
        map.put("message", "OK");
        return JSONObject.toJSONString(map);
    }

 




    
    public static void main(String[] args) {
//    	Logger log=Logger.getLogger();
        SpringApplication.run(Application.class, args);
//        try {
//            sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        LocalMemory.init();
//        NiuRobotSystem.OpenNiuRobot();
//        NiuRobotSystem.initNiuRobot();

        RobotPool.manageService.execute(new RobotManager());
        WebSocketClient.start(Application.ip);
    }
    public static String ip="ws://101.132.181.253:17014";
//    public static String ip="ws://localhost:17014";
    /**
     * 获取牛牛状态
     * @return
     */
    @RequestMapping(value="/getNiuRobotState",produces = "application/json; charset=UTF-8")
    public String getNiuRobotState(){
    	Map<String,Object> map=new HashMap<>();
    	try {
			map.put("success",true);
			map.put("message","牛牛机器人状态获取成功");
			map.put("state", NiuRobotSystem.isOpenRobot);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("success",false);
			map.put("message","牛牛机器人状态获取失败");
		}
    	return JsonUtil.MaptoString(map);
    }
    /**
     * 关闭牛牛机器人
     * @return
     */
    @RequestMapping(value="/closeNiuRobot",produces = "application/json; charset=UTF-8")
    public String closeNiuRobot(){
    	Map<String,Object> map=new HashMap<>();
    	try {
			NiuRobotSystem.CloseNiuRobot();
			map.put("success",true);
			map.put("message","牛牛机器人关闭成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("success",false);
			map.put("message","牛牛机器人关闭失败");
		}
    	return JsonUtil.MaptoString(map);
    }
    
    /**
     * 启动牛牛机器人
     * @return
     */
    @RequestMapping(value="/openRobot",produces = "application/json; charset=UTF-8")
    public String openRobot(){
    	Map<String,Object> map=new HashMap<>();
    	try {
			NiuRobotSystem.OpenNiuRobot();
			map.put("success",true);
			map.put("message","牛牛机器人启动成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
			map.put("success",false);
			map.put("message","牛牛机器人启动失败");
		}
    	return JsonUtil.MaptoString(map);
    }
    
    @RequestMapping("/test")
    public String test(){
    	return "success";
    }
}