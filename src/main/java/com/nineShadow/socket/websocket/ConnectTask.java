package com.nineShadow.socket.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class ConnectTask extends Thread {
    private static final int bindPort=18007;
    static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");
    public int userId;
    //    ConnectorTaskKiller killer;
    public ConnectTask(int userId){
        try{
//            Long x=sign.getRobotStartTime().getTime()-new Date().getTime();
//            MinaClient.scheduledExecutorService.execute(new ConnectorTaskKiller(userId,sign.getGameroundnum()*1000*2*60+x));
//            this.userId=userId;
//            this.sign=sign;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
