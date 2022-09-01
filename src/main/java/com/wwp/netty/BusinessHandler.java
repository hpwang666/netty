package com.wwp.netty;

import com.wwp.entity.DevCharger;
import com.wwp.model.YlcMessage;
import com.wwp.service.IDevChargerService;
import com.wwp.service.impl.DevChargerServiceImpl;
import com.wwp.util.SpringBeanUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import io.netty.util.Signal;
import io.netty.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class BusinessHandler extends ChannelInboundHandlerAdapter {


   private IDevChargerService devChargerService;


    private DefaultEventExecutorGroup eventExecutorGroup;
    private  StringBuilder respone;

    BusinessHandler( DefaultEventExecutorGroup eventExecutorGroup)
    {
        this.eventExecutorGroup = eventExecutorGroup;
        this.devChargerService =(IDevChargerService) SpringBeanUtils.getApplicationContext().getBean(DevChargerServiceImpl.class);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {

        Promise<StringBuilder> businessFuture= new DefaultPromise<StringBuilder>(eventExecutorGroup.next());
        businessFuture.addListener(new FutureListener<StringBuilder>() {

            @Override
            public void operationComplete(Future<StringBuilder> future) throws Exception {
                if (future.isSuccess()) {
                    respone = (StringBuilder) future.get();
                    System.out.println("respone: " + respone);
                    //ctx.channel().writeAndFlush(Unpooled.copiedBuffer("12345 ", CharsetUtil.UTF_8));
                    YlcMessage msg = new YlcMessage();
                    ctx.writeAndFlush(msg);
                }
            }
        });


        eventExecutorGroup.execute(new Runnable() {
            @Override
            public void run() {
               try{
                   Thread.sleep(5000);
               }
                catch(InterruptedException e)
                {
                   e.printStackTrace();
               }
                DevCharger devCharger =  devChargerService.getDevChargerBySerialNum("32010203040506");

                if(devCharger !=null){
                    System.out.println("charger: " + devCharger.getDepartId());
                    businessFuture.setSuccess(new StringBuilder("good job"));
                }

            }});



       // super.channelRead(ctx,msg);
    }


}
