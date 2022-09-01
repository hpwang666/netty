package com.wwp;

import com.wwp.entity.DevCharger;
import com.wwp.service.IDevChargerService;
import com.wwp.service.impl.DevChargerServiceImpl;
import com.wwp.util.SpringBeanUtils;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

@ComponentScan(basePackages = {"com.wwp.common.annotation"})
@SpringBootTest(classes= MainApplication.class)
public class MainApplicationTests {
   // @Resource
   // IDevChargerService devChargerService;

    @Test
    public void testBean()
    {

        IDevChargerService devChargerService =(IDevChargerService) SpringBeanUtils.getApplicationContext().getBean(DevChargerServiceImpl.class);
        DevCharger devCharger= devChargerService.getDevChargerBySerialNum("32010203040506");
        System.out.println("charger: " + devCharger.getDepartId());
    }

    public  void testServer()
    {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                // 继承 ByteToMessageDecoder 抽象类的自定义解码器
                // channel.pipeline().addLast(new MyIntegerDecoder()).addLast(new IntegerProcessorHandler());
                // 继承 ReplayingDecoder 类的自定义解码器
               // channel.pipeline().addLast(new MyIntegerDecoder2()).addLast(new IntegerProcessorHandler());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);
        for (int j = 0;j < 20;j++){
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(j);
            channel.writeInbound(byteBuf);
        }
        try{
            Thread.sleep(2000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

    }


    public void testString()
    {
        short[] src={0x99,0,0x5};
        String s = YlcStringUtils.bcd2string(src);
        System.out.println(s.length()+" " +s.toString());

        byte crcL,crcH;

        short[] src3 = {0x00,0x02,0x00,0x33,0x32,0x01,0x02,0x00,0x00,0x00,0x00,0x11,0x15,0x11,0x16,0x15,0x55,0x35,0x02,0x60,0x32,0x01,0x02,0x00,0x00,0x00,0x01,0x01,0x01,0x00};//0FE2};
        short[] src0={  0x00,0x01,0x00,0x03,0x32,0x01,0x02,0x00,0x00,0x00,0x01,0x01,0x00};//0x68,0x90};
        short[] src1={  0x00,0x00,0x00,0x02,0x55,0x03,0x14,0x12,0x78,0x23,0x05,0x00};//0xda,0x4c}; 19674
       System.out.println(" "+YlcStringUtils.crc(src3));
        crcL = (byte)(YlcStringUtils.crc(src3)&0xff);
        crcH = (byte)(YlcStringUtils.crc(src3)>>8&0xff);
        System.out.println(crcH+" "+crcL);
    }

    public void testTime()
    {
        short[] t ={0x98,0xB7,0x0E,0x11,0x10,0x03,0x14};

       try{
           Date d = YlcStringUtils.CP56Time2Data(t);
           System.out.println(d);
       }
       catch(ParseException e){
           e.printStackTrace();
       }
    }


    public void testThreads()
    {
        DefaultEventExecutorGroup eventLoop = new DefaultEventExecutorGroup(5);


        DefaultPromise<Integer> promise = new DefaultPromise(eventLoop.next());

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(8);
            //promise.setFailure(new IllegalStateException()); 也可以设置失败
        }, "t1").start();

        new Thread(() -> {
            promise.addListener(future -> {
                Object futureNow = future.getNow();
                System.out.println("future result: "+futureNow);
            });
        }, "t2").start();



        System.out.println("start");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

