package com.wwp.netty;

import com.wwp.model.YlcMsgType;
import com.wwp.model.*;
import com.wwp.util.YlcStringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;


import java.util.List;


public class YlcMsgDecoder  extends ReplayingDecoder<YlcMsgDecoder.DecoderState> {

    private YlcMsgHeader ylcMsgHeader;
    private YlcMessage ylcMessage;
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
    //心跳  680d000100033201020304050601000708
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        System.out.println("state: "+this.state());
        if (this.actualReadableBytes() ==0) {
            //ctx.close();
            System.out.println("closed? ");
            return;
        }

        switch((YlcMsgDecoder.DecoderState)this.state()) {
            case READ_HEADER:
                try {
                    this.ylcMsgHeader = decodeHeader(ctx, buffer);
                    this.bytesRemainingInVariablePart = this.ylcMsgHeader.getLength();
                    this.checkpoint(YlcMsgDecoder.DecoderState.READ_PAYLOAD);
                    break;
                } catch (Exception e) {
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    System.out.println(e.toString());
                    out.add(new YlcMessage(false,null,null,e.toString()));
                    return;
                }

            case READ_PAYLOAD:
                try {
                    if (this.actualReadableBytes() < (this.ylcMsgHeader.getLength()-4+2)) {
                        throw new DecoderException("too short "+ (this.ylcMsgHeader.getLength()-4+2));
                    }

                    System.out.println("type: "+ this.ylcMsgHeader.getType()+" length: "+this.ylcMsgHeader.getLength()+" seq: "+this.ylcMsgHeader.getSeq());
                    this.ylcMessage =decodePayload(ctx, buffer, this.ylcMsgHeader, 12, 32);

                    this.ylcMsgHeader = null;
                    this.checkpoint(DecoderState.READ_CRC);


                    //out.add(ylcMessage);
                    break;
                } catch (Exception e) {
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    out.add(new YlcMessage(false,this.ylcMsgHeader,null,e.toString()));
                    return;
                }

            case READ_CRC:

               try{
                   if(this.actualReadableBytes()<2)
                       throw new DecoderException("no crc bytes ");
                   System.out.println("crcL: "+buffer.readUnsignedByte()+"  crcH: "+buffer.readUnsignedByte());
                   out.add(ylcMessage);
                   this.checkpoint(DecoderState.READ_HEADER);
                   break;
               }
                catch(Exception e){
                    this.checkpoint(YlcMsgDecoder.DecoderState.BAD_MESSAGE);
                    out.add(new YlcMessage(false,this.ylcMsgHeader,null,e.toString()));
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
        super.channelInactive(ctx);
    }

    private  YlcMsgHeader decodeHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (this.actualReadableBytes() < 6) {
            throw new DecoderException("too short no header");
        }
        
        short b0 = buffer.readUnsignedByte();
        if(b0 != 0x68)  throw new DecoderException("wrong header "+ b0);

        int length = buffer.readUnsignedByte()&0xff;
        int seq =  buffer.readUnsignedByte()<<8;
        seq += buffer.readUnsignedByte();
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

    private   YlcMessage decodePayload(ChannelHandlerContext ctx, ByteBuf buffer, YlcMsgHeader ylcMsgHeader, int bytesRemainingInVariablePart, int maxClientIdLength)throws Exception {

        switch(ylcMsgHeader.getType()) {
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

            case RECORD:
                return decodeRecord(ctx,buffer,ylcMsgHeader);

            default:
                return new YlcMessage(true,ylcMsgHeader,"no decode","null");

        }
    }


    private YlcMessage decodeAuth(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
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


    private YlcMessage decodeHeart(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
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

    private YlcMessage decodeModelVerify(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        FeeModel feeModel = new FeeModel();
        int index=7;
        short[] serialId=new short[8];
        short[] model = new short[2];
        for(index=0;index<7;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        model[0] = buffer.readUnsignedByte();
        model[1] = buffer.readUnsignedByte();

        feeModel.setModelCode(YlcStringUtils.bcd2string(model));
        msg.setFeeModel(feeModel);

        msg.setSuccess(true);

        return msg;
    }



    private YlcMessage decodeGetModel(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId=new short[8];
        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        buffer.skipBytes(this.actualReadableBytes());

        msg.setSuccess(true);

        return msg;
    }

    private YlcMessage decodeUpRtStatus(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId = new short[8];
        short[] businessId = new short[16];
        short[] wireId = new short[8];



        ChargerStatus chargerStatus = new ChargerStatus();

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));


        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setPlugStatus((int)buffer.readUnsignedByte());//枪状态

        chargerStatus.setPlugHoming((int)buffer.readUnsignedByte());//是否归位
        chargerStatus.setSlotIn((int)buffer.readUnsignedByte()); //是否插枪
        chargerStatus.setVoltage((int)buffer.readUnsignedByte()+( (int)buffer.readUnsignedByte()<<8&0xff00));//
        chargerStatus.setCurrent((int)buffer.readUnsignedByte()+( (int)buffer.readUnsignedByte()<<8&0xff00));;
        chargerStatus.setWireTmp((int)buffer.readUnsignedByte());

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

        buffer.skipBytes(this.actualReadableBytes());

        msg.setSuccess(true);
        msg.setChargerStatus(chargerStatus);
        return msg;
    }

    private YlcMessage decodeUpChargeEnd(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId = new short[8];
        short[] businessId = new short[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        buffer.skipBytes(7);

        //TODO
        //剩余的字节


        return msg;

    }

    private YlcMessage decodeUpChargeReq(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId = new short[8];
        short[] cardId = new short[8];

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        msg.setReqType((int)buffer.readUnsignedByte()); //启动方式
        buffer.skipBytes(1);

        for(index=0;index<8;index++)
            cardId[index] =  buffer.readUnsignedByte();

        msg.setCardId(YlcStringUtils.bcd2string(cardId));

        buffer.skipBytes(this.actualReadableBytes());
        return msg;
    }

    private YlcMessage decodeRemoteOnAck(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId = new short[8];
        short[] businessId = new short[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setStartOk((int)buffer.readUnsignedByte());


        return msg;
    }

    private YlcMessage decodeRemoteOffAck(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader)
    {
        YlcMessage msg = new YlcMessage();
        int index=8;
        short[] serialId = new short[8];

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号
        msg.setStopOk((int)buffer.readUnsignedByte());


        return msg;
    }

    private YlcMessage decodeRecord(ChannelHandlerContext ctx, ByteBuf buffer,YlcMsgHeader ylcMsgHeader) throws Exception
    {
        YlcMessage msg = new YlcMessage();

        int index=8;
        short[] serialId = new short[8];
        short[] businessId = new short[16];
        short[] cp56Time = new short[7];
        short[] fee = new short[16];

        for(index=0;index<16;index++)
            businessId[index] =  buffer.readUnsignedByte();
        msg.setBusinessId(YlcStringUtils.bcd2string(businessId));

        for(index=0;index<8;index++)
            serialId[index] =  buffer.readUnsignedByte();
        msg.setSerialId(YlcStringUtils.bcd2string(serialId));

        msg.setPlugNo((int)buffer.readUnsignedByte()); //枪号

        YlcRecord record = new YlcRecord();


        for(index=0;index<7;index++)
            cp56Time[index] =  buffer.readUnsignedByte();
        record.setStartTime(YlcStringUtils.CP56Time2Data(cp56Time));

        for(index=0;index<7;index++)
            cp56Time[index] =  buffer.readUnsignedByte();
        record.setEndTime(YlcStringUtils.CP56Time2Data(cp56Time));

        for(index=0;index<16;index++)//尖
            fee[index] =  buffer.readUnsignedByte();
        record.setRecordFee0(YlcStringUtils.bcd2string(fee));

        for(index=0;index<16;index++)//峰
            fee[index] =  buffer.readUnsignedByte();
        record.setRecordFee1(YlcStringUtils.bcd2string(fee));

        for(index=0;index<16;index++)//平
            fee[index] =  buffer.readUnsignedByte();
        record.setRecordFee2(YlcStringUtils.bcd2string(fee));

        for(index=0;index<16;index++)//谷
            fee[index] =  buffer.readUnsignedByte();
        record.setRecordFee3(YlcStringUtils.bcd2string(fee));

        long recordStartKwh;
        long recordEndKwh;
        long recordTotalKwh;
        long lossTotalKwh;
        long totalCost;//所有花费

        recordStartKwh =buffer.readUnsignedByte()| (buffer.readUnsignedByte()<<8) |
                (buffer.readUnsignedByte()<<16) |(buffer.readUnsignedByte()<<24) |(buffer.readUnsignedByte()<<32);
        recordEndKwh = buffer.readUnsignedByte()| (buffer.readUnsignedByte()<<8) |
                (buffer.readUnsignedByte()<<16) |(buffer.readUnsignedByte()<<24) |(buffer.readUnsignedByte()<<32);

        recordTotalKwh = buffer.readUnsignedByte()| (buffer.readUnsignedByte()<<8) |
                (buffer.readUnsignedByte()<<16) |(buffer.readUnsignedByte()<<24);

        lossTotalKwh = buffer.readUnsignedByte()| (buffer.readUnsignedByte()<<8) |
                (buffer.readUnsignedByte()<<16) |(buffer.readUnsignedByte()<<24);

        totalCost= buffer.readUnsignedByte()| (buffer.readUnsignedByte()<<8) |
                (buffer.readUnsignedByte()<<16) |(buffer.readUnsignedByte()<<24);

        record.setRecordStartKwh(recordStartKwh);
        record.setRecordEndKwh(recordEndKwh);
        record.setRecordTotalKwh(recordTotalKwh);
        record.setLossTotalKwh(lossTotalKwh);
        record.setTotalCost(totalCost);

        buffer.skipBytes(17);//电动汽车唯一标识

        record.setTradeType(buffer.readUnsignedByte()&0xff);

        buffer.skipBytes(this.actualReadableBytes());
        msg.setYlcRecord(record);


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
