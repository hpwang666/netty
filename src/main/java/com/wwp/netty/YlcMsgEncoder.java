package com.wwp.netty;

import com.wwp.entity.FeeModel;
import com.wwp.model.YlcResult;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

public class YlcMsgEncoder extends MessageToByteEncoder<YlcResult> {  //1
    @Override
    public void encode(ChannelHandlerContext ctx, YlcResult result, ByteBuf out)
            throws IllegalArgumentException {
        try{
            ctx.channel().write(doEncode(ctx, result));//交给write的buf会被release
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    static ByteBuf doEncode(ChannelHandlerContext ctx, YlcResult result) {
        switch(result.getYlcDevMsg().getHeader().getMsgType()) {
            case AUTH_ACK:
                return encodeAuthAck(ctx, result);
            case HEART_ACK:
                return encodeHeartAck(ctx, result);
            case MODEL_VERIFY_ACK:
                return encodeModelVerifyAck(ctx, result);
            case GET_MODEL_ACK:
                return encodeGetModelAck(ctx,result);
            default:
                throw new IllegalArgumentException("Unknown message type: " );
        }
    }

    private static ByteBuf encodeHeartAck(ChannelHandlerContext ctx, YlcResult result) {

        //int topicNameBytes = ByteBufUtil.utf8Bytes("fuck dan dan ");
        ByteBuffer propertiesBuf = ByteBuffer.allocate(17);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x0d);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x04);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        propertiesBuf.put((byte)(result.getYlcDevMsg().getPlugNo()&0xff));
        propertiesBuf.put((byte)0x00);

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,15);//15角标是不包含的

        int crc = YlcStringUtils.crc(forCRC,13);

        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(17);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;

        return var14;
    }

    private static ByteBuf encodeAuthAck(ChannelHandlerContext ctx, YlcResult result) {

        ByteBuffer propertiesBuf = ByteBuffer.allocate(16);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x0c);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x02);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        propertiesBuf.put((byte)0x00);

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,14);//15角标是不包含的

        int crc = YlcStringUtils.crc(forCRC,12);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(16);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;
        for (int i=0;i<16;i++
             ) {
           System.out.print(" "+Integer.toHexString(buf.getByte(i)) );
        }

        return var14;
    }

    private static ByteBuf encodeModelVerifyAck(ChannelHandlerContext ctx, YlcResult result) {

        //int topicNameBytes = ByteBufUtil.utf8Bytes("fuck dan dan ");
       // ByteBuf propertiesBuf = ctx.alloc().buffer(339);

        ByteBuffer propertiesBuf = ByteBuffer.allocate(18);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x0E);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x06);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getModelCode()));

        propertiesBuf.put((byte)0x01);//0x01验证总是不一致

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,16);//15角标是不包含的

        int crc = YlcStringUtils.crc(forCRC,14);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));


        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(18);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;

        return var14;
    }

    private static ByteBuf encodeGetModelAck(ChannelHandlerContext ctx, YlcResult result) {

        //int topicNameBytes = ByteBufUtil.utf8Bytes("fuck dan dan ");
        //ByteBuf propertiesBuf = ctx.alloc().buffer(339);

        int baseLen=98-4;//总长度-4
        FeeModel feeModel =(FeeModel) result.getResult();

        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x5E);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x0A);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
      //  propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcMessage().getModelCode()));
        propertiesBuf.put(YlcStringUtils.string2bcd(feeModel.getModelCode()));
        propertiesBuf.put(Base64.getDecoder().decode(feeModel.getFee0()));
        propertiesBuf.put(Base64.getDecoder().decode(feeModel.getFee1()));
        propertiesBuf.put(Base64.getDecoder().decode(feeModel.getFee2()));
        propertiesBuf.put(Base64.getDecoder().decode(feeModel.getFee3()));

        propertiesBuf.put((byte)(feeModel.getLossRate()&0xff));

        propertiesBuf.put(Base64.getDecoder().decode(feeModel.getFeesByModel()));

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
}

