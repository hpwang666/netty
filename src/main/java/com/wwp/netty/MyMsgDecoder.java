package com.wwp.netty;

import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyMsgDecoder extends ReplayingDecoder {

    int length = 10;
    //ByteBuf b= PooledByteBufAllocator.DEFAULT.directBuffer();

    MyMsgDecoder()
    {
        ByteBuf buf = Unpooled.buffer();
        System.out.println("create");
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println();
        System.out.println("MyMessageDecoder decode 被调用");
        //需要将得到二进制字节码-> MyMessageProtocol 数据包(对象)

        //System.out.println(in.readUnsignedByte());
        //System.out.println(in.readUnsignedByte());
        short bb;
        ByteBuf b = in.readBytes(4);
        System.out.println("获取4字节数据");
        System.out.println(b.capacity());
        System.out.println(b.readUnsignedByte());
        System.out.println(b.readUnsignedByte());
        System.out.println(b.readUnsignedByte());
        System.out.println(b.readUnsignedByte());


        b.release();
      //  b.release();
        //in.skipBytes(num);


        return;
    }


}
