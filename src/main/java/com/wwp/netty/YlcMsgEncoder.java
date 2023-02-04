package com.wwp.netty;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import com.wwp.entity.YlcFeeModel;
import com.wwp.model.YlcDevMsg;
import com.wwp.model.YlcResult;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.wwp.util.YlcStringUtils.Date2cp56Time;
import static com.wwp.util.YlcStringUtils.parseByte2HexStr;

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
            case RECORD_ACK:
                return encodeRecordAck(ctx,result);
            case UP_CHARGE_ACK:
                return encodeUpChargeAck(ctx,result);
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

        ByteBuffer timeBuf = ByteBuffer.allocate(22);
        timeBuf.put((byte)0x68);
        timeBuf.put((byte)0x12);
        timeBuf.put((byte)0x00);
        timeBuf.put((byte)0x02);
        timeBuf.put((byte)0x00);
        timeBuf.put((byte)0x56);
        timeBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        timeBuf.put(Date2cp56Time());
        forCRC =  Arrays.copyOfRange(timeBuf.array(),2,20);//15角标是不包含的
        crc = YlcStringUtils.crc(forCRC,18);
        timeBuf.put((byte)((crc>>8)&0xff));
        timeBuf.put((byte)(crc&0xff));

        buf.writeBytes(timeBuf.array());
        var14 = buf;


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

        System.out.println("计费模型编码： "+result.getYlcDevMsg().getModelCode());
        if(result.getYlcDevMsg().getModelCode().equals("0000"))
        propertiesBuf.put((byte)0x01);//0x01验证不一致
        else propertiesBuf.put((byte)0x01);//0x00验证一致

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
        YlcFeeModel ylcFeeModel =(YlcFeeModel) result.getResult();

        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x5E);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x0A);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        //propertiesBuf.put(YlcStringUtils.string2bcd(feeModel.getModelCode()));
        propertiesBuf.put((byte)0x01);
        propertiesBuf.put((byte)0x00);

        //截取4个字节，转换成byte 然后逆序
        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee0().substring(0,4))));
        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee0().substring(4))));

        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee1().substring(0,4))));
        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee1().substring(4))));

        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee2().substring(0,4))));
        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee2().substring(4))));

        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee3().substring(0,4))));
        propertiesBuf.put(ArrayUtil.reverse(HexUtil.decodeHex(ylcFeeModel.getFee3().substring(4))));


        propertiesBuf.put((byte)(ylcFeeModel.getLossRate()&0xff));

        propertiesBuf.put(HexUtil.decodeHex(ylcFeeModel.getFeesByModel()));


        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,baseLen+2);//15角标是不包含的
        int crc = YlcStringUtils.crc(forCRC,baseLen);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));



        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(baseLen+4);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;

        System.out.println("");
        return var14;
    }


    private static ByteBuf encodeUpChargeAck(ChannelHandlerContext ctx, YlcResult result) {

        //int topicNameBytes = ByteBufUtil.utf8Bytes("fuck dan dan ");
        //ByteBuf propertiesBuf = ctx.alloc().buffer(339);

        int baseLen=46-4;//总长度-4

        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x2A);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x32);

        propertiesBuf.put(YlcStringUtils.string2bcd(  YlcStringUtils.genBusinessId(result.getYlcDevMsg().getSerialId(),result.getYlcDevMsg().getPlugNo())  ));
        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getSerialId()));
        propertiesBuf.put((byte)(result.getYlcDevMsg().getPlugNo()&0xff));


        propertiesBuf.put(YlcStringUtils.string2bcd("0000001122334455"));//逻辑卡号
        propertiesBuf.put(YlcStringUtils.parseHexStr2Byte("0f000000"));//一元余额

        propertiesBuf.put((byte)0x01);//鉴权成功0x01
        propertiesBuf.put((byte)0x00);//错误码
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
    private static ByteBuf encodeRecordAck(ChannelHandlerContext ctx, YlcResult result) {

        //int topicNameBytes = ByteBufUtil.utf8Bytes("fuck dan dan ");
        // ByteBuf propertiesBuf = ctx.alloc().buffer(339);
        int baseLen=25-4;//总长度-4
        ByteBuffer propertiesBuf = ByteBuffer.allocate(baseLen+4);
        propertiesBuf.put((byte)0x68);
        propertiesBuf.put((byte)0x15);

        propertiesBuf.put((byte)(result.getYlcDevMsg().getHeader().getSeq()&0xff));
        propertiesBuf.put((byte)((result.getYlcDevMsg().getHeader().getSeq()>>8)&0xff));

        propertiesBuf.put((byte)0x00);
        propertiesBuf.put((byte)0x40);

        propertiesBuf.put(YlcStringUtils.string2bcd(result.getYlcDevMsg().getBusinessId()));

        propertiesBuf.put((byte)0x00);//0x01验证总是不一致

        byte[] forCRC =  Arrays.copyOfRange(propertiesBuf.array(),2,baseLen+2);//15角标是不包含的

        int crc = YlcStringUtils.crc(forCRC,baseLen);
        propertiesBuf.put((byte)((crc>>8)&0xff));
        propertiesBuf.put((byte)(crc&0xff));

        showRecord(result.getYlcDevMsg());
        ByteBuf var14;
        ByteBuf buf = ctx.alloc().buffer(baseLen+4);
        buf.writeBytes(propertiesBuf.array());
        var14 = buf;

        return var14;
    }

    private static void showRecord(YlcDevMsg msg){


        System.out.println(" ");
        System.out.println("交易流水号："+msg.getBusinessId());

        System.out.println("总电量："+ msg.getYlcRecordMsg().getRecordTotalKwh());

        System.out.println("计损电量："+ msg.getYlcRecordMsg().getLossTotalKwh());

        System.out.println("花费金额："+ msg.getYlcRecordMsg().getTotalCost());

        System.out.println("停止原因："+ msg.getYlcRecordMsg().getOverType());

    }
}

