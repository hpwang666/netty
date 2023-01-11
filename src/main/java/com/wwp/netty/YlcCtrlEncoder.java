package com.wwp.netty;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.Session;
import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

//主动下发的数据进行编码
public class YlcCtrlEncoder extends MessageToByteEncoder<YlcCtrlMsg> {  //1

    private DefaultEventExecutorGroup eventExecutorGroup;

    public YlcCtrlEncoder( DefaultEventExecutorGroup eventExecutorGroup)
    {
        this.eventExecutorGroup = eventExecutorGroup;
    }
    @Override
    public void encode(ChannelHandlerContext ctx, YlcCtrlMsg ctrl, ByteBuf out)
            throws IllegalArgumentException {
        System.out.println("ctrl encoder");
        if(YlcDeviceMap.exist(ctrl.getSerialId())){
            Session s = YlcDeviceMap.getDEVICES().get(ctrl.getSerialId());
            s.setAckFuture(new DefaultPromise<YlcResult>(eventExecutorGroup.next()));
            System.out.println("配置下发的future");
        }
        try{
            ctx.channel().write(doEncode(ctx, ctrl));//交给write的buf会被release
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("没发送成功");
        }

    }

    static ByteBuf doEncode(ChannelHandlerContext ctx, YlcCtrlMsg ctrl) {
        switch(ctrl.getMsgType()) {
            case REMOTE_ON:
                return encodeRemoteOn(ctx, ctrl);
            case REMOTE_OFF:
                return encodeRemoteOff(ctx, ctrl);
            case CARD_UPDATE:
                return encodeRemoteAddPhys(ctx, ctrl);
            default:
                throw new IllegalArgumentException("Unknown message type: " );
        }
    }

    static ByteBuf encodeRemoteOn(ChannelHandlerContext ctx, YlcCtrlMsg ctrl) {
        int baseLen=52-4;//总长度-4
        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x30);

        //序列号 域
        propertiesBuf.put((byte)0x22);
        propertiesBuf.put((byte)0x33);


        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x34);


        propertiesBuf.put(YlcStringUtils.string2bcd(  YlcStringUtils.genBusinessId(ctrl.getSerialId(),ctrl.getPlugNo())  ));
        propertiesBuf.put(YlcStringUtils.string2bcd(ctrl.getSerialId()));
        propertiesBuf.put((byte)(ctrl.getPlugNo()&0xff));
        propertiesBuf.put(YlcStringUtils.string2bcd(ctrl.getLogicId()));
        propertiesBuf.put(Base64.getDecoder().decode(ctrl.getPhysId()));
        propertiesBuf.put(Base64.getDecoder().decode(ctrl.getAccount()));

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,baseLen+2);//15角标是不包含的
        int crc = YlcStringUtils.crc(forCRC,baseLen);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(baseLen+4);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;


        return var14;
    }

    static ByteBuf encodeRemoteOff(ChannelHandlerContext ctx, YlcCtrlMsg ctrl) {
        int baseLen=16-4;//总长度-4
        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x0c);

        //序列号 域
        propertiesBuf.put((byte)0x22);
        propertiesBuf.put((byte)0x33);


        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x36);


        propertiesBuf.put(YlcStringUtils.string2bcd(ctrl.getSerialId()));
        propertiesBuf.put((byte)(ctrl.getPlugNo()&0xff));


        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,baseLen+2);//15角标是不包含的
        int crc = YlcStringUtils.crc(forCRC,baseLen);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(baseLen+4);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;
        return var14;
    }

    static ByteBuf encodeRemoteAddPhys(ChannelHandlerContext ctx, YlcCtrlMsg ctrl) {
        int baseLen=32-4;//总长度-4
        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)28);

        //序列号 域
        propertiesBuf.put((byte)0x22);
        propertiesBuf.put((byte)0x33);


        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x44);


        propertiesBuf.put(YlcStringUtils.string2bcd(ctrl.getSerialId()));
        propertiesBuf.put((byte)(0x01));//卡的个数
        propertiesBuf.put(YlcStringUtils.string2bcd(ctrl.getLogicId()));
        propertiesBuf.put(Base64.getDecoder().decode(ctrl.getPhysId()));
        //propertiesBuf.put(Base64.getDecoder().decode(ctrl.getAccount()));

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,baseLen+2);//15角标是不包含的
        int crc = YlcStringUtils.crc(forCRC,baseLen);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(baseLen+4);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;
        for (int i=0;i<baseLen+4;i++) {
            System.out.print(" "+Integer.toHexString(buf.getByte(i)) );
        }
        return var14;
    }
}
