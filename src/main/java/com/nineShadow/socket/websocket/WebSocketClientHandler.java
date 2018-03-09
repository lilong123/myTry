package com.nineShadow.socket.websocket;

import com.alibaba.fastjson.JSONObject;
import com.nineShadow.dao.PlayerBaseMapper;
import com.nineShadow.robotManager.Robot;
import com.nineShadow.robotManager.RobotPool;
import com.nineShadow.robotManager.RobotStarter;
import com.nineShadow.service.socket.GameService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WebSocketClientHandler.class);
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        //System.out.println("WebSocket Client disconnected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {


        final Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            // web socket client connected
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            //链接成功
            RobotStarter.bindRobot2Channal(ctx);
            return;
        }

        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            //发消息统计
            final Date time=new Date();
            final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            logger.info("textFrame.text()"+ textFrame.text());
            // uncomment to print request
             if(textFrame.text().substring(0,1).equals("-")&&RobotPool.channelHandlerContextMap.values().contains(ctx)){
                 logger.info("发送给"+ RobotPool.channelHandlerContext2UserMap.get(ctx)+"，timestamp:"+textFrame.text());
                 RobotPool.hashResponse.put(RobotPool.channelHandlerContext2UserMap.get(ctx),RobotPool.getSignal);
                 if(RobotPool.robotOn){
                     ctx.channel().write(new TextWebSocketFrame("+"));
                     ctx.flush();
                 }
                 return;
             }
            JSONObject jsonObject=JSONObject.parseObject(textFrame.text());
            String method= (String) jsonObject.get("m");
            logTimes(method);
            final String URL;

            try{

                final Object args=jsonObject.get("a");
                method=method.replace("<","");
                method=method.replace(">","");
                String[] methods=method.split("/");
                String bean="";
                if(methods.length<2){
                    bean="default";
                    URL=methods[0];

                }else{
                    bean=methods[0];
                    URL=methods[1];
                }
                Object service = SpringUtil.getBean(bean);
                final Method m = service.getClass().getDeclaredMethod(URL, ChannelHandlerContext.class,JSONObject.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Object result = m.invoke(service, ctx,jsonObject);
                            Date timelast=new Date();
                            logger.info("\n\n\n\n花费时间："+(timelast.getTime()-time.getTime())+"\n\n\n\n");
                            if (result != null) {
                                if (result instanceof Map<?, ?>||result instanceof String) {
                                    if(result instanceof String) {
                                        sendByWebSocket(ctx, URL, JSONObject.parse((String) result));
                                    }else {
                                        sendByWebSocket(ctx, URL, result);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).start();
            }catch (Exception e){
                logger.info(e.getMessage());
            }


        } else if (frame instanceof PongWebSocketFrame) {
        } else if (frame instanceof CloseWebSocketFrame)
            ch.close();
        else if (frame instanceof BinaryWebSocketFrame) {
            // uncomment to print request
            // logger.info(frame.content().toString());
        }

    }
   final private static String lock="lock";
    private  static long index=0;
    private static Date time=null;
    private static Map<String,Integer> methodTimes=new HashMap<>();
    private void logTimes(String method) {
        if(time==null){
            time=new Date();
        }
        if(new Date().getTime()-time.getTime()>1000*60){

//            synchronized (lock){
//                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                AppendToFile.appendMethodA("getTimes.txt",
//                        dateFormater.format(time)+"-"+dateFormater.format(new Date())+"\t\t:"+index+"\r\n");
//                for(String methods:methodTimes.keySet()){
//                    AppendToFile.appendMethodA(methods.replace("/","")
//                                    .replace("<","")
//                                    .replace(">","")+"Send.txt",
//                            dateFormater.format(time)+"-"+dateFormater.format(new Date())+"\t\t:"+methodTimes.get(methods)+"\n");
//                }
//                index=0;
//                time=null;
//               methodTimes=new HashMap<>();
//            }
        }else{
            synchronized (lock){
                index++;
                methodTimes.putIfAbsent(method,0);
                methodTimes.put(method,methodTimes.get(method)+1);
            }
        }

    }

    private Class<?>[] getAndSetClasses(final JSONObject args) {
        try {
            Class<?>[] classArray = new Class<?>[args.keySet().size()+1];

            classArray[0]=ChannelHandlerContext.class;
            int index=1;
            for(String key:args.keySet()){
                classArray[index]=args.get(key).getClass();
            }
            return classArray;
        } catch (Exception e) {
            logger.error("getAndSetClasses异常", e);
        }
        return null;
    }
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }

        ChannelFuture future=ctx.close();
        try {
            future.get();
        }catch (Exception e){
            logger.error("关闭出错");
        }
    }

    /**
     * WebSocket返回
     * @param ctx channel
     * @param msg 发送消息
     */
    public static  void sendByWebSocket(final ChannelHandlerContext ctx,final String method ,final Object msg)  {
        if (ctx == null || ctx.isRemoved()) {
            throw new RuntimeException("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        Map<String,Object> message=new HashMap<>();
        message.put("m",method);
        message.put("a",msg);
        final String result=JSONObject.toJSONString(message)+"&_";
        logger.info("\n发送消息"+result);
        //统计发送消息
//        logSendTimes(method);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ctx.channel().write(new TextWebSocketFrame(result));
                    ctx.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    final private static String lock4Send="lock";
    private  static long index4Send=0;
    private static Date time4Send=new Date();

    private static Map<String,Integer> methodTimes4Send=new HashMap<>();
    private static void logSendTimes(String method) {
        if(time4Send==null){
            time4Send=new Date();
        }
        if(new Date().getTime()-time4Send.getTime()>1000*60){
//            synchronized (lock4Send){
//                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                AppendToFile.appendMethodA("sendTimes.txt",
//                        dateFormater.format(time4Send)+"-"+dateFormater.format(new Date())+"\t\t:"+index4Send+"\n");
//                for(String methods:methodTimes4Send.keySet()){
//                    AppendToFile.appendMethodA(methods.replace("/","")
//                                    .replace("<","")
//                                    .replace(">","")+"Send.txt",
//                            dateFormater.format(time)+"-"+dateFormater.format(new Date())+"\t\t:"+methodTimes4Send.get(methods)+"\n");
//                }
//                index4Send=0;
//                time4Send=new Date();
//                methodTimes4Send=new HashMap<>();
//            }
        }else{
            synchronized (lock4Send){
                index4Send++;
                methodTimes4Send.putIfAbsent(method,0);
                methodTimes4Send.put(method,methodTimes4Send.get(method)+1);
            }
        }

    }
}