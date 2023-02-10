package com.wwp;

import cn.hutool.core.io.checksum.crc16.CRC16XModem;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import com.wwp.entity.YlcCharger;
import com.wwp.entity.YlcChargerStatus;
import com.wwp.entity.YlcOrder;
import com.wwp.mapper.YlcChargerStatusMapper;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.mapper.YlcUserLogicalMapper;
import com.wwp.service.IYlcChargerService;
import com.wwp.service.impl.YlcChargerServiceImpl;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.wwp.util.YlcStringUtils.parseByte2HexStr;



@ComponentScan(basePackages = {"com.wwp.common.annotation"})
@SpringBootTest(classes= MainApplication.class)
public class MainApplicationTests {
    @Resource
    YlcUserLogicalMapper ylcUserLogicalMapper;
//
//    @Resource
//    YlcChargerStatusMapper ylcChargerStatusMapper;

    public void testBean()
    {
        IYlcChargerService devChargerService =(IYlcChargerService) SpringBeanUtils.getApplicationContext().getBean(YlcChargerServiceImpl.class);
        YlcCharger ylcCharger = devChargerService.getDevChargerBySerialNum("32010203040506");
        System.out.println("charger: " + ylcCharger.getDepartId());

        YlcOrder ylcOrder = new YlcOrder();
        ylcOrder.setOrderNum(YlcStringUtils.genOrderNum("32010203040506",1));
        ylcOrder.setSerialNum("32010203040506");
        ylcOrder.setPhysicalNum("32010203040506");
        ylcOrder.setPlugNo(1);

        ((YlcOrderMapper)SpringBeanUtils.getApplicationContext().getBean(YlcOrderMapper.class)).add(ylcOrder);
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



    public void testBUffer()
    {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        // position: 0, limit: 6, capacity: 6

        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        // position: 3, limit: 6, capacity: 6

        buffer.mark();  // 写入三个字节数据后进行标记
        // position: 3, limit: 6, capacity: 6

        buffer.put((byte) 4); // 再次写入一个字节数据
        // position: 4, limit: 6, capacity: 6

        buffer.reset(); // 对buffer进行重置，此时将恢复到Mark时的状态
        // position: 3, limit: 6, capacity: 6

        buffer.flip();  // 切换为读取模式，此时有三个数据可供读取
        // position: 0, limit: 3, capacity: 6

        System.out.println(buffer.get()+" "+buffer.get());  // 读取一个字节数据之后进行标记
        buffer.mark();
        // position: 1, limit: 3, capacity: 6

        buffer.get(); // 继续读取一个字节数据
        // position: 2, limit: 3, capacity: 6

        buffer.reset(); // 进行重置之后，将会恢复到mark的状态
    }


    public void testString()
    {
        short[] src={0x22,0,0x5};
        String s = YlcStringUtils.bcd2string(src);
        System.out.println(s.length()+" " +s.toString());

        short crcL,crcH;

        short[] src0={0x92,0x01,0x00,0x13,0x32,0x01,0x06,0x00,0x21,0x35,0x33,0x01,0x20,0x23,0x01,0x15,0x21,0x14,0x00,0x01,0x32,0x01,0x06,0x00,0x21,0x35,0x33,0x01,0x03,0x02,0x01,0x43,0x09,0x28,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x33,0x00,0x00,0x00,0xb8,0x24,0x00,0x00,0xb8,0x24,0x00,0x00,0x28,0x55,0x00,0x00,0x00,0x00};//0x68,0x90};
        //crcL: 117  crcH: 132 166 87

        byte[] src1={ 0x29,0x00,0x00,0x03,0x32,0x01,0x06,0x00,0x21,0x35,0x33,0x01,0x00};
        // crcL: 57  crcH: 180 180 65
        //       39  B4  B4 41
        //AUTH:  6822250000013201060021353301010F6C796B2D56323900001920168043024800000000C554
        //HEART: 680d050000033201060021353301005718

        short[] src_101={0x25,0x00,0x00,0x01,0x32,0x01,0x06,0x00,0x21,0x35,0x33,0x01,0x01,0x0F,0x6C,0x79,0x6B,0x2D,0x56,0x32,0x39,0x00,0x00,0x19,0x20,0x16,0x80,0x43,0x02,0x48,0x00,0x00,0x00,0x00};//,0xC5,0x54
      //68 22 40 00 00 01 32 01 06 00 21 35 33 01 01 0F 6C 79 6B 2D 56 32 39 00 00 19 20 16 80 43 02 48 00 00 00 00 D2 6D

        CRC16XModem crc16 = new CRC16XModem();
        crc16.update(src1);
        String hexValue = crc16.getHexValue(true);
        System.out.println(hexValue);

        //CRC16Checksum.getHexValue(true);
        System.out.println(" "+YlcStringUtils.crc(src0,src0.length));
       crcL = (short)(YlcStringUtils.crc(src1,src1.length)&0xff);
        crcH = (short)(YlcStringUtils.crc(src1,src1.length)>>8&0xff);
        System.out.printf(" crcH: %02x  crcL: %02x ",crcH,crcL);




        byte[] ss= YlcStringUtils.string2bcd("9345");
        System.out.println("ss: "+(ss[0]&0xff)+" "+(ss[1]&0xff));

        int[] src2={0xA0,0x86,0x01,0x00};
        int[] src4={0X00,0X00,0X00,0X00,0XD1,0XB2,0XC3,0XD4};
        String s1= "gBoGAJxAAAA=";
        ByteBuffer buf = ByteBuffer.allocate(src4.length);
        IntStream.of(src4).forEach(i -> buf.put((byte)i));
        String base64encodedString = Base64.getEncoder().encodeToString(buf.array());
        System.out.println("base64 encode: "+base64encodedString);
        byte[] base64decodedBytes = Base64.getDecoder().decode(s1);
        System.out.println("base64 decode: "+parseByte2HexStr(base64decodedBytes));

    }

    @Test
    public void testHutool()
    {
        byte[] src2={0x50,0x36,0x01,0x00};
        Integer I1=100000;
        String str3 = "A0860100";

        //字节数组转换为字符串
        String str2 = HexUtil.encodeHexStr(src2);
        System.out.println(str2);

        //字符串转换成字节数组
        byte[] b2 = HexUtil.decodeHex(str3);

        //这个转换不好用，int 你的数组必须至少4个字节长，也只转换前4个字节
        //但是不用反转 直接小端模式
        System.out.println(ByteUtil.bytesToInt(b2));

        System.out.println(HexUtil.encodeHexStr(ByteUtil.intToBytes(I1)));//转换出来一定是4个字节


        //当字节数组是小端序  可以转换成大端方便阅读
        String str4 = HexUtil.encodeHexStr(ArrayUtil.reverse(b2));
        System.out.println("b2 reverse: "+str4+" "+ HexUtil.hexToLong(str4));



    }

    public void testTime()
    {
        short[] t ={0x98,0xB7,0x0E,0x11,0x10,0x03,0x14};


           Date d1 = YlcStringUtils.cp56Time2Date(t);
           System.out.println(d1);

        YlcChargerStatus ylcChargerStatus=new YlcChargerStatus();
        ylcChargerStatus.setUpdateTime(new Date());
        ylcChargerStatus.setOrderNum("32010600213533012023020819120001");

        //ylcChargerStatusMapper.update(ylcChargerStatus);
        ylcUserLogicalMapper.updateUserAmount("1111",new BigDecimal("3410045").multiply(new BigDecimal("100")));
        BigDecimal b1= ylcUserLogicalMapper.queryUserAmount("1111").divide(new BigDecimal("100")).setScale(2);

        System.out.println(b1.toString());




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

