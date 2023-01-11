package com.wwp.netty;

//
import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;


@Component
public class NettyServerListener {

    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通道适配器
     */
    final DefaultEventExecutorGroup businessGroup = new DefaultEventExecutorGroup(64);
    //private ServerChannelHandlerAdapter channelHandlerAdapter;
    /**
     * NETT服务器配置类
     */
    @Resource
    private NettyConfig nettyConfig;

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        //优雅退出

        for(int i = 0;i<YlcDeviceMap.getDEVICES().size();i++)
        {

        }

        Set<Map.Entry<String, Session>> entries = YlcDeviceMap.getDEVICES().entrySet();
        for (Map.Entry<String, Session> entry : entries) {
            Session value = entry.getValue();
            value.getChannel().close();
            System.out.println("关闭连接： "+value.getChannel().remoteAddress());
        }
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    private static Long getDirectMemoryLimit() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName("io.netty.util.internal.PlatformDependent");
        if (clazz == null) {
            return null;
        }

        Field directMemoryLimit = clazz.getDeclaredField("MAX_DIRECT_MEMORY");
        if (directMemoryLimit == null) {
            return null;
        }

        directMemoryLimit.setAccessible(true);

        // 取DIRECT_MEMORY_LIMIT的值
        return directMemoryLimit.getLong(clazz);
    }

    /**
     * 开启及服务线程
     */
    public void start() {
        // 从配置文件中(application.yml)获取服务端监听端口号
        //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);//ADVANCED
        int port = nettyConfig.getPort();


        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)//监听队列长度
                .option( ChannelOption.SO_REUSEADDR,true)
                .handler(new LoggingHandler(LogLevel.INFO));
        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                   // pipeline.addLast(new ObjectCodec());


                    pipeline.addLast(new IdleStateHandler(100, 0, 0));
                    pipeline.addLast("decoder",new YlcMsgDecoder());
                    pipeline.addLast("encoderAck",new YlcMsgEncoder());
                    pipeline.addLast("encoderCtrl",new YlcCtrlEncoder(businessGroup));
                    pipeline.addLast(new ServerChannelHandlerAdapter());
                    pipeline.addLast(businessGroup,new BusinessHandler(businessGroup));//在Netty业务线程池里执行耗时业务
                    //pipeline.addLast(new BusinessHandler());//在Io线程池里面执行耗时业务
                }
            });

            Long directMemoryLimit = getDirectMemoryLimit();

            System.out.println("directMemoryLimit: " + directMemoryLimit +" byte");

            System.out.println("netty服务器在[{}]端口启动监听"+ port);
            ChannelFuture channelFuture  = serverBootstrap.bind(port).sync();
            //channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e ) {
            System.out.println("[出现异常] 释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}