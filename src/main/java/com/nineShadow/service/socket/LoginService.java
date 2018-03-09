package com.nineShadow.service.socket;

import com.alibaba.fastjson.JSONObject;
import com.nineShadow.robotManager.Robot;
import com.nineShadow.robotManager.RobotPool;
import com.nineShadow.robotManager.RobotStarter;
import com.nineShadow.socket.websocket.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("login")
public class LoginService {
    private static final Logger logger = Logger.getLogger(LoginService.class);

    public void login(final ChannelHandlerContext ctx,  JSONObject jsonObject){
//        jsonObject=jsonObject.getJSONObject("a");
//        logger.info(jsonObject);
//        Integer id=new Integer(jsonObject.get("userId").toString());
//        if(RobotPool.connectedRobots.containsKey(id)){
//            Robot robot=new Robot(id);
//            RobotPool.scheduledExecutorService.execute(robot);
//            RobotPool.robotThreads.put(id,robot);
//        }


    }
}
