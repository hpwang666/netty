package com.wwp.netty;

import com.wwp.model.YlcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

public class YlcMsgEncoder extends MessageToByteEncoder<YlcMessage> {  //1
    @Override
    public void encode(ChannelHandlerContext ctx, YlcMessage msg, ByteBuf out)
            throws Exception {
        System.out.println("msg: ");
        out.writeShort(0x30);  //2
        //ctx.channel().writeAndFlush("12345");
    }
}

