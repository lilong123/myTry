package com.nineShadow.controller;

import com.alibaba.fastjson.JSONObject;
import com.nineShadow.robotManager.RobotPool;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@EnableAutoConfiguration
@RequestMapping("/redPacket")
public class RedPacket {

}
