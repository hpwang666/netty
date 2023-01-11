package com.wwp.netty;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.YlcMsgType;
import com.wwp.model.*;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;


import java.util.Base64;
import java.util.List;

import static com.wwp.model.YlcMsgType.UP_RT_STATUS;


public class YlcMsgDecoder  extends ReplayingDecoder<YlcMsgDecoder.DecoderState> {

    private YlcMsgHeader ylcMsgHeader;
    private YlcDevMsg ylcDevMsg;
    private int bytesRemainingInVariablePart;
    private final int maxBytesInMessage;
    private final int maxClientIdLength;

    public YlcMsgDecoder() {
        this(8092, 23);
    }

    public YlcMsgDecoder(int maxBytesInMessage) {
        this(maxBytesInMessage, 23);
    }

    public YlcMsgDecoder(int maxBytesInMessage, int maxClientIdLength) {
        super(YlcMsgDecoder.DecoderState.READ_HEADER);
        this.maxBytesInMessage = maxBytesInMessage;
        this.maxClientIdLength = maxClientIdLength;
    }


    // readUnsignedByte
    // readByte();
    // skipBytes
    // readRetainedSlice
    //68f011220044aa
    //心跳  680d050000033201060021353301005718
    //认证  6822250000013201060021353301010F6C796B2D56323900001920168043024800000000C554
    //计费模型验证请求0x05 680D01000005320106002135331324B538
    //计费模型请求   0x09  680B0200000932010600213533506B
    //上传实时状态   0x13  6840040000130000000000000000000000000000000032010600213533010202010000000000000000000000000000000000000000000000000000000000000000003213
    //                   68409A0000133201060021353301202301081639000232010600213533010302015209000000000000000000000000000B00000040060000400600002C1A00000000B73E
    //                   684025000013320106002135330120230108163900023201060021353301030201280928000000000000000000000000020000009001000090010000A4060000000045C1
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        //System.out.println("state: "+this.state());
        short crcH=0;
        short crcL=0;
        if (this.actualReadableBytes() ==0) {
            //ctx.close();
            System.out.println("客户端要关闭？ ");
            return;
        }

        switch((YlcMsgDecoder.DecoderState)this.state()) {
            case READ_HEADER:
                try {
                    this.ylcMsgHeader = decodeHeader(ctx, buffer);
                    this.bytesRemainingInVariablePart = this.ylcMsgHeader.getLength();

                    System.out.println("type: "+ this.ylcMsgHeader.getMsgType()+" length: "+this.ylcMsgHeader.getLength()+" seq: "+this.ylcMsgHeader.getSeq());

                    this.checkpoint(YlcMsgDecoder.DecoderState.READ_PAYLOAD);
                    break;
                } catch (Exception e) {
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    System.out.println(e.toString());
                    out.add(YlcResult.error(e.toString()));
                    return;
                }

            case READ_PAYLOAD:
                try {
                    if (this.actualReadableBytes() < (this.ylcMsgHeader.getLength()-4+2)) {
                        throw new DecoderException("too short "+ (this.ylcMsgHeader.getLength()-4+2));
                    }

                    this.ylcDevMsg =decodePayload(ctx, buffer, this.ylcMsgHeader, 12, 32);
                    this.ylcDevMsg.setHeader(this.ylcMsgHeader);
                    this.ylcMsgHeader = null;
                    this.checkpoint(DecoderState.READ_CRC);


                    //out.add(ylcMessage);
                    break;
                } catch (Exception e) {
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    out.add(YlcResult.error(e.toString()));
                    return;
                }

            case READ_CRC:

               try{
                   if(this.actualReadableBytes()<2)
                       throw new DecoderException("no crc bytes ");
                   crcL = buffer.readUnsignedByte();
                   crcH= buffer.readUnsignedByte();
                   //System.out.println("crcL: "+crcL+"  crcH: "+crcH);
                   out.add(new YlcResult(ylcDevMsg));
                   this.checkpoint(DecoderState.READ_HEADER);
                   break;
               }
                catch(Exception e){
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    out.add(YlcResult.error(e.toString()));
                    return;
                }

            case BAD_MESSAGE:
                this.checkpoint(DecoderState.READ_HEADER);
                buffer.skipBytes(this.actualReadableBytes());
                break;
            default:
                throw new Error();
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("decoder  channelInactive");
        YlcDeviceMap.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    private  YlcMsgHeader decodeHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (this.actualReadableBytes() < 6) {

            throw new DecoderException("too short no header："+this.actualReadableBytes());
        }
        
        short b0 = buffer.readUnsignedByte();
        if(b0 != 0x68)  throw new DecoderException("wrong header "+ b0);

        int length = buffer.readUnsignedByte()&0xff;
        int seq =  buffer.readUnsignedByte();
        seq += buffer.readUnsignedByte()<<8;
        int marker =  buffer.readUnsignedByte();
        int b5 =    buffer.readUnsignedByte();
        YlcMsgType type = YlcMsgType.valueOf(b5);

        return new YlcMsgHeader(length,seq,marker,type);

/*

        int remainingLength = 0;
        int multiplier = 1;
        int loops = 0;

        short digit;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            ++loops;
        } while((digit & 128) != 0 && loops < 4);

        if (loops == 4 && (digit & 128) != 0) {
            throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
        } else {
            MqttFixedHeader decodedFixedHeader = new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength);
            return MqttCodecUtil.validateFixedHeader(ctx, MqttCodecUtil.resetUnusedFields(decodedFixedHeader));
        }

 */
    }

    private YlcDevMsg decodePayload(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader, int bytesRemainingInVariablePart, int maxClientIdLength)throws Exception {

        switch(ylcMsgHeader.getMsgType()) {
            case AUTH:
                return decodeAuth(ctx,buffer,ylcMsgHeader);
            case HEART:
                return decodeHeart(ctx,buffer,ylcMsgHeader);
            case MODEL_VERIFY:
                return decodeModelVerify(ctx,buffer,ylcMsgHeader);
            case GET_MODEL:
               return decodeGetModel(ctx,buffer,ylcMsgHeader);
            case UP_RT_STATUS:
                return decodeUpRtStatus(ctx,buffer,ylcMsgHeader);
            case UP_CHARGE_END:
                return decodeUpChargeEnd(ctx,buffer,ylcMsgHeader);



            case UP_CHARGE_REQ:
                return decodeUpChargeReq(ctx,buffer,ylcMsgHeader);

            case REMOTE_ON_ACK:
                return decodeRemoteOnAck(ctx,buffer,ylcMsgHeader);
            case REMOTE_OFF_ACK:
                return decodeRemoteOffAck(ctx,buffer,ylcMsgHeader);
            case TIME_CFG_ACK:
                return decodeTimeCfgAck(ctx,buffer,ylcMsgHeader);

            case CARD_UPDATE_ACK:
                return decodeCardUpdateAck(ctx,buffer,ylcMsgHeader);

            case RECORD:
                return decodeRecord(ctx,buffer,ylcMsgHeader);

            default:
                return new YlcDevMsg(true,ylcMsgHeader,"no decode","null");

        }
    }


    private YlcDevMsg decodeAuth(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId=new short[7];
        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setType((int)buffer.readUnsignedByte());
        msg.setPlugs((int)buffer.readUnsignedByte());
        buffer.skipBytes(21);//剩下的协议版本运营商就不要了

        msg.setSuccess(true);

        return msg;
    }


    private YlcDevMsg decodeHeart(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId=new short[7];
        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setPlugNo((int)buffer.readUnsignedByte());
        msg.setPlugStatus((int)buffer.readUnsignedByte());


        msg.setSuccess(true);


        return msg;
    }

    private YlcDevMsg decodeModelVerify(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        //FeeModel feeModel = new FeeModel();
        int index=7;
        short[] serialId=new short[7];
        short[] model = new short[2];
        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        model[0] = buffer.readUnsignedByte();
        model[1] = buffer.readUnsignedByte();

        msg.setModelCode(YlcStringUtils.bcd2string(model));

        msg.setSuccess(true);

        return msg;
    }



    private YlcDevMsg decodeGetModel(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId=new short[7];
        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));


        msg.setSuccess(true);

        return msg;
    }

    private YlcDevMsg decodeUpRtStatus(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];
        short[] businessId = new short[16];
        short[] wireId = new short[8];



        ChargerStatus chargerStatus = new ChargerStatus();

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));


        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setPlugStatus((int)buffer.readUnsignedByte());//枪状态
        chargerStatus.setPlugHoming((int)buffer.readUnsignedByte());//是否归位
        chargerStatus.setSlotIn((int)buffer.readUnsignedByte()); //是否插枪


        chargerStatus.setVoltage((int)buffer.readUnsignedByte()+( (int)buffer.readUnsignedByte()<<8&0xff00));//
        chargerStatus.setCurrent((int)buffer.readUnsignedByte()+( (int)buffer.readUnsignedByte()<<8&0xff00));;
        chargerStatus.setWireTmp((int)buffer.readUnsignedByte());

        System.out.println("枪状态 "+msg.getPlugStatus()+"  归位: "+chargerStatus.getPlugHoming()+"  插枪："+chargerStatus.getSlotIn());
        System.out.println("电压： "+chargerStatus.getVoltage());

        for(index=0;index<8;index++)
            wireId[index] =  buffer.readUnsignedByte();

        chargerStatus.setSOC((int)buffer.readUnsignedByte());
        chargerStatus.setBatteryTmp((int)buffer.readUnsignedByte());
        chargerStatus.setTotalChargeTime((int)buffer.readUnsignedByte()+((int) buffer.readUnsignedByte()<<8&0xff00));
        chargerStatus.setRemainChargeTime((int)buffer.readUnsignedByte()+( (int)buffer.readUnsignedByte()<<8&0xff00));

        long usedKwh=0;
        for(index=0;index<4;index++)
            usedKwh |=  buffer.readUnsignedByte()<<(index*8);
        chargerStatus.setUsedKwh(usedKwh);

        long lossKwh=0;
        for(index=0;index<4;index++)
            lossKwh |=  buffer.readUnsignedByte()<<(index*8);
        chargerStatus.setLossKwh(lossKwh);

        long usedMoney=0;
        for(index=0;index<4;index++)
            usedMoney |=  buffer.readUnsignedByte()<<(index*8);
        chargerStatus.setUsedMoney(usedMoney);

        System.out.println("充电度数： "+chargerStatus.getUsedKwh()+" 已充金额："+chargerStatus.getUsedMoney());
        buffer.skipBytes(2);

        msg.setSuccess(true);
        msg.setChargerStatus(chargerStatus);
        return msg;
    }

    private YlcDevMsg decodeUpChargeEnd(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[8];
        short[] businessId = new short[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));
        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        buffer.skipBytes(7);

        //TODO
        //剩余的字节

        msg.setSuccess(true);
        return msg;

    }

    private YlcDevMsg decodeUpChargeReq(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];
        short[] cardId = new short[8];

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));
        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        msg.setReqType((int)buffer.readUnsignedByte()); //启动方式
        buffer.skipBytes(1);

        for(index=0;index<8;index++)
            cardId[index] =  buffer.readUnsignedByte();
        System.out.println("物理卡号: "+YlcStringUtils.parseByte2HexStr(cardId));
        msg.setCardId(YlcStringUtils.bcd2string(cardId));

        buffer.skipBytes(33);

        msg.setSuccess(true);

        return msg;
    }

    private YlcDevMsg decodeRemoteOnAck(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];
        short[] businessId = new short[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setStartOk((int)buffer.readUnsignedByte());
        msg.setCtrlError((int)buffer.readUnsignedByte());
        System.out.println("错误码: "+msg.getCtrlError());
        msg.setSuccess(true);
        return msg;
    }

    private YlcDevMsg decodeTimeCfgAck(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];
        short[] timeAck = new short[7];



        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        for(index=0;index<7;index++)
            timeAck[index] =  buffer.readUnsignedByte();
        //msg.setSerialId(YlcStringUtils.bcd2string(timeAck));

        msg.setSuccess(true);
        return msg;
    }
    private YlcDevMsg decodeRemoteOffAck(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setStopOk((int)buffer.readUnsignedByte());
        msg.setCtrlError((int)buffer.readUnsignedByte());
        msg.setSuccess(true);
        return msg;
    }

    private YlcDevMsg decodeRecord(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader) throws Exception
    {
        YlcDevMsg msg = new YlcDevMsg();

        int index=8;
        short[] serialId = new short[7];
        short[] businessId = new short[16];
        short[] cp56Time = new short[7];
        byte[] fee = new byte[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        YlcRecord record = new YlcRecord();


        for(index=0;index<7;index++)
            cp56Time[index] =  buffer.readUnsignedByte();
        record.setStartTime(YlcStringUtils.cp56Time2Date(cp56Time));

        for(index=0;index<7;index++)
            cp56Time[index] =  buffer.readUnsignedByte();
        record.setEndTime(YlcStringUtils.cp56Time2Date(cp56Time));

        for(index=0;index<16;index++)//尖
            fee[index] =  buffer.readByte();
        record.setFee0All(Base64.getEncoder().encodeToString(fee));

        for(index=0;index<16;index++)//峰
            fee[index] =  buffer.readByte();
        record.setFee1All(Base64.getEncoder().encodeToString(fee));

        for(index=0;index<16;index++)//平
            fee[index] =  buffer.readByte();
        record.setFee2All(Base64.getEncoder().encodeToString(fee));

        for(index=0;index<16;index++)//谷
            fee[index] =  buffer.readByte();
        record.setFee3All(Base64.getEncoder().encodeToString(fee));

        byte[] recordStartKwh = new byte[5];
        byte[] recordEndKwh =new byte[5];
        byte[] recordTotalKwh= new byte[4];
        byte[] lossTotalKwh= new byte[4];
        byte[] totalCost= new byte[4];//所有花费

        for(index=0;index<5;index++)
            recordStartKwh[index] =  buffer.readByte();
        record.setRecordStartKwh(Base64.getEncoder().encodeToString(recordStartKwh));

        for(index=0;index<5;index++)
            recordEndKwh[index] =  buffer.readByte();
        record.setRecordEndKwh(Base64.getEncoder().encodeToString(recordEndKwh));

        for(index=0;index<4;index++)
            recordTotalKwh[index] =  buffer.readByte();
        record.setRecordTotalKwh(Base64.getEncoder().encodeToString(recordTotalKwh));

        for(index=0;index<4;index++)
            lossTotalKwh[index] =  buffer.readByte();
        record.setLossTotalKwh(Base64.getEncoder().encodeToString(lossTotalKwh));

        for(index=0;index<4;index++)
            totalCost[index] =  buffer.readByte();
        record.setTotalCost(Base64.getEncoder().encodeToString(totalCost));

        buffer.skipBytes(17);//电动汽车唯一标识

        record.setTradeType(buffer.readUnsignedByte()&0xff);

        for(index=0;index<7;index++)
            cp56Time[index] =  buffer.readUnsignedByte();
        record.setBusinessDate(YlcStringUtils.cp56Time2Date(cp56Time));

        record.setOverType((int)buffer.readUnsignedByte());

        byte[] physId = new byte[8];
        for(index=0;index<8;index++)
            physId[index] =  buffer.readByte();
        record.setPhysId(Base64.getEncoder().encodeToString(physId));

        msg.setYlcRecord(record);
        msg.setSuccess(true);

        return msg;
    }


    private YlcDevMsg decodeCardUpdateAck(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader)
    {
        YlcDevMsg msg = new YlcDevMsg();
        int index=8;
        short[] serialId = new short[7];

        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));
        msg.setSuccess( buffer.readUnsignedByte()==0x01);
        msg.setError(String.format("%d",buffer.readUnsignedByte()));//失败原因

        return msg;
    }

    static enum DecoderState {
        READ_HEADER,
        READ_PAYLOAD,
        READ_CRC,
        BAD_MESSAGE;

        private DecoderState() {
        }
    }
}
