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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@ComponentScan(basePackages = {"com.wwp.common.annotation"})
//@SpringBootTest(classes= MainApplication.class)
public class MainApplicationTests {
   // @Resource
   // IDevChargerService devChargerService;


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

    @Test
    public void testString()
    {
        short[] src={0x22,0,0x5};
        String s = YlcStringUtils.bcd2string(src);
        System.out.println(s.length()+" " +s.toString());

        byte crcL,crcH;

        byte[] src3 = {0x00,0x02,0x00,0x33,0x32,0x01,0x02,0x00,0x00,0x00,0x00,0x11,0x15,0x11,0x16,0x15,0x55,0x35,0x02,0x60,0x32,0x01,0x02,0x00,0x00,0x00,0x01,0x01,0x01,0x00};//0FE2};
        byte[] src0={  0x00,0x01,0x00,0x03,0x32,0x01,0x02,0x00,0x00,0x00,0x01,0x01,0x00};//0x68,0x90};
        short[] src1={  0x00,0x00,0x00,0x02,0x55,0x03,0x14,0x12,0x78,0x23,0x05,0x00};//0xda,0x4c}; 19674
       System.out.println(" "+YlcStringUtils.crc(src0,src0.length));
       crcL = (byte)(YlcStringUtils.crc(src0,src0.length)&0xff);
        crcH = (byte)(YlcStringUtils.crc(src0,src0.length)>>8&0xff);
        System.out.println("crc: "+crcH+" "+crcL);



        byte[] ss= YlcStringUtils.string2bcd("9345");
        System.out.println("ss: "+(ss[0]&0xff)+" "+(ss[1]&0xff));

        int[] src2={0xA0,0x86,0x01,0x00};
        int[] src4={0x80,0x1a,0x06,0x00,0x9c,0x40,0x00,0x00};
        int[] src5={0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00,0x01,0x00,0x02,0x03,0x03,0x00};
        ByteBuffer buf = ByteBuffer.allocate(src2.length);
        IntStream.of(src2).forEach(i -> buf.put((byte)i));
        String base64encodedString = Base64.getEncoder().encodeToString(buf.array());
        System.out.println("base64 encode: "+base64encodedString);
        byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
        System.out.println("base64 decode: "+((base64decodedBytes[0]&0xff)|( base64decodedBytes[1]<<8)));

    }

    public void testTime()
    {
        short[] t ={0x98,0xB7,0x0E,0x11,0x10,0x03,0x14};

       try{
           Date d = YlcStringUtils.cp56Time2Date(t);
           System.out.println(d);
       }
       catch(ParseException e){
           e.printStackTrace();
       }


        String d = YlcStringUtils.genBusinessId("32010203040506",1);
        System.out.println(d);
        d = YlcStringUtils.genBusinessId("32010203040506",1);
        System.out.println(d);
        d = YlcStringUtils.genBusinessId("32010203040506",1);
        System.out.println(d);
        d = YlcStringUtils.genBusinessId("32010203040506",1);
        System.out.println(d);

    }


    public void testThreads()
    {
        DefaultEventExecutorGroup eventLoop = new DefaultEventExecutorGroup(3);

        DefaultPromise<Integer> promise = new DefaultPromise(eventLoop.next());
        DefaultPromise<Integer> promise1 = new DefaultPromise(eventLoop.next());
        DefaultPromise<Integer> promise2 = new DefaultPromise(eventLoop.next());
        DefaultPromise<Integer> promise3 = new DefaultPromise(eventLoop.next());
        DefaultPromise<Integer> promise4 = new DefaultPromise(eventLoop.next());
        DefaultPromise<Integer> promise5 = new DefaultPromise(eventLoop.next());

      // System.out.println(promise.s()) ;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(8);
            System.out.println(Thread.currentThread());
            //promise.setFailure(new IllegalStateException()); 也可以设置失败
        }, "t1").start();

        new Thread(() -> {
//            promise.addListener(future -> {//这个里面的内容是在线程池里面执行的
//                Object futureNow = future.getNow();
//                System.out.println(Thread.currentThread());
//                System.out.println("future result: "+futureNow);
//            });
            try{
                Integer i =  (Integer) promise.get(2000, TimeUnit.MILLISECONDS);

                System.out.println(Thread.currentThread()+"i: "+i);
            }
           catch(TimeoutException e)
           {
               e.printStackTrace();
           }
            catch(InterruptedException | ExecutionException e2)
            {
                e2.printStackTrace();
            }
        }, "t2").start();



        System.out.println("start");
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void  testFuture()
    {
        DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(5);
        Future<StringBuilder> future = eventExecutorGroup.next().submit(new Callable<StringBuilder>() {

            @Override
            public StringBuilder call() {
                System.out.println("thread: " + Thread.currentThread().getName());
                try{
                    Thread.sleep(4000);
                    return new StringBuilder("work");
                }
                catch(InterruptedException e)
                {
                    return new StringBuilder(e.toString());
                }

            }
        });

        future.addListener(new FutureListener<StringBuilder>() {

            @Override
            public void operationComplete(Future<StringBuilder> future) throws Exception {
                if (future.isSuccess()) {
                    StringBuilder respone = ((StringBuilder) future.get());
                    System.out.println("respone: " + respone+" "+Thread.currentThread().getName());
                }
            }
        });


        System.out.println("start: "+Thread.currentThread().getName());
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

