package com.wwp.netty;

// 记录调用方法的元信息的类
import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.Session;
import com.wwp.model.YlcDevMsg;
import com.wwp.model.YlcResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.nio.channels.SeekableByteChannel;


/**
 * 多线程共享
 */


public class ServerChannelHandlerAdapter extends ChannelInboundHandlerAdapter {


    /**
     * 注入请求分排器
     */


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyServerInHandler channelInactive");
        YlcDeviceMap.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //获取客户端发送过来的消息
       // ByteBuf byteBuf = (ByteBuf) msg;
        //System.out.println("收到客户端" + ctx.channel().remoteAddress() + "发送的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
     //   System.out.println("len: "+byteBuf.readableBytes());

        System.out.println("收到客户端" + ctx.channel().remoteAddress()+" "+Thread.currentThread());
        if(msg instanceof YlcResult){
            YlcResult result = (YlcResult) msg;
            YlcDevMsg ylcDevMsg = result.getYlcDevMsg();

            if(result.getSuccess()){
                System.out.println(" id: " + ylcDevMsg.getSerialId());

                if(!YlcDeviceMap.exist(ylcDevMsg.getSerialId())){
                    YlcDeviceMap.put(ylcDevMsg.getSerialId(),new Session(ctx.channel()));

                    //AttributeKey<Session> sessionIdKey = AttributeKey.valueOf(ylcDevMsg.getSerialId());
                    //ctx.channel().attr(sessionIdKey).set(new Session(ctx.channel()));
                }
                ctx.fireChannelRead(msg);
               // ctx.pipeline(). businessGroup.
            }
            else  System.out.println(" error: " + result.getMessage());
        }

    }

   // @Override
   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       // 入站的消息就是 IdleStateEvent 具体的事件
       IdleStateEvent event = (IdleStateEvent) evt;

       String eventType = null;
       // 我们在 IdleStateHandler 中也看到了，它有读超时，写超时，读写超时等
       switch (event.state()) {
           case READER_IDLE:
               eventType = "读空闲";
               ctx.channel().close(); // 手动断开连接
               YlcDeviceMap.remove(ctx.channel());
               break;
           case WRITER_IDLE:
               eventType = "写空闲";
               break; // 不处理
           case ALL_IDLE:
               eventType = "读写空闲";
               break; // 不处理
       }

       // 打印触发了一次超时警告
       System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);

   }

}