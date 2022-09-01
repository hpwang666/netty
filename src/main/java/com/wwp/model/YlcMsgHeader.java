package com.wwp.model;

public class YlcMsgHeader {
    private int length;
    private  int seq;
    private int marker;
    private YlcMsgType type;

    public YlcMsgHeader(int length,int seq,int marker,YlcMsgType type)
    {
        this.length = length;
        this.seq = seq;
        this.marker = marker;
        this.type = type;
    }



    public int getLength() {
        return this.length;
    }

    public int getSeq() {
        return this.seq;
    }

    public int getMarker() {
        return this.marker;
    }

    public YlcMsgType getType() {
        return this.type;
    }
}
