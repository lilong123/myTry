package com.nineShadow.socket.websocket;

import com.nineShadow.model.PlayerBase;
import com.nineShadow.model.RedPackageRoom;
import com.nineShadow.robotManager.Robot;
import com.nineShadow.robotManager.RobotManager;
import com.nineShadow.robotManager.RobotPool;
import com.nineShadow.springboot.Application;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class WebSocketClient {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WebSocketClient.class);

//    public static void main(String []args){
//        for(int i=0;i<501;i++)
//        System.out.println("\n" +
//                "insert into `user` (`id`, `bank_number`, `birthday`, `bonus`, `current_exploit`, `exploit`, `head_url`, `last_exploit`, `login_date`, `nick_name`, `password`, `phone`, `point`, `promoter_id`, `promoter_name`, `promoter_type`, `real_name`, `regist_date`, `status`, `user_name`, `user_type`, `v_point`, `day_last`, `got_exploit_price`, `last_exploit_day`, `bank`, `win_rate`, `pretty_in_top`," +
//                " `robot_type0`, `robot_type1`, `robot_type2`, `win_ratio`, `give_permission`, " +
//                "`first_time`) values" +
//                "('"+(i+3174)+"','','','','9213','74713','4','300','2018-02-11','user"+(i+3174)+"','c1f68ec06b490b3ecb4066b1b13a9ee9','','5334','-1','','0','user"+(i+3174)+"','2016-07-15 22:25:56','0','user3173','4','10083','0','0','2018-02-11',NULL,'0.35','','-1','-1','1','0%|0%|33.3%','0','2018-02-11 16:00:00');");
//    }


    public static void start(String uri){
        //初始化房间MAP
        logger.error("starting...");
        RobotPool.redPacketRoomMap=LocalMemory.redPackageRoomMapper.selectAll().stream()
                .collect(Collectors.toMap(RedPackageRoom::getRoomName,Function.identity()));
        int roomNameSize=RobotPool.roomNameList.size();
        for(int i=0;i<roomNameSize;i++){
            RobotPool.roomNameList.remove(0);
        }
        RobotPool.redPacketRoomMap.values().forEach(x->RobotPool.roomNameList.add(x.getRoomName()));
        List<PlayerBase> playerBaseList=LocalMemory.playerMapper.selectAll();
        for(String roomName:RobotPool.roomNameList){
            RobotPool.redPacketIds.put(roomName,new HashMap<>());
        }
        //初始化玩家map
        RobotPool.robots=playerBaseList.stream().filter(LocalMemory::isRedPacketRobot)
                .collect(Collectors.toMap(PlayerBase::getId, Function.identity()));


        for(int i=0;i<RobotPool.roomNameList.size()*10;i++){

            try {
                synchronized (RobotPool.robots){
                    synchronized (RobotPool.connectingRobots){
                        RobotPool.scheduledExecutorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callOneRobot(uri,null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }

//                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private final URI uri;
    private Channel ch;
    private static final EventLoopGroup group = new NioEventLoopGroup();

    public WebSocketClient(final String uri) {
        this.uri = URI.create(uri);
    }

    public void open() throws Exception {
        Bootstrap b = new Bootstrap();
        String protocol = uri.getScheme();
        if (!"ws".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
        // If you change it to V00, ping is not supported and remember to change
        // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
        final WebSocketClientHandler handler =
                new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false, HttpHeaders.EMPTY_HEADERS, 1280000));

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast("ws-handler", handler);
                    }
                });

        //System.out.println("WebSocket Client connecting");
        ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
        logger.info("ending");
    }

    public void close() throws InterruptedException {
        //System.out.println("WebSocket Client sending close");
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
        //group.shutdownGracefully();
    }

    public void eval(final String text) throws IOException {
        ch.writeAndFlush(new TextWebSocketFrame(text));
    }

    public static void addMore() {

            logger.info("addMore");
            synchronized (RobotPool.robots){
                synchronized (RobotPool.connectingRobots){

                    RobotPool.scheduledExecutorService.execute(()-> {
                        try {
                            callOneRobot(Application.ip,null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
            }


    }
    private static  void callOneRobot(String uri,Integer id) throws Exception {
        synchronized (RobotPool.robots){
            synchronized (RobotPool.connectingRobots){

                if(id==null){
                    List<Integer>ids= new ArrayList<>(RobotPool.robots.keySet());
                    Collections.shuffle(ids);
                    id=ids.get(0);
                }
                RobotPool.connectingRobots.put(id,RobotPool.robots.get(id));
                RobotPool.robots.remove(id);
            }

        }

        new WebSocketClient(uri).open();
        logger.info("socket open");
    }

    public static void addMore(Integer key) {
        try {
            new WebSocketClient(Application.ip).open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("socket open");
    }
}
