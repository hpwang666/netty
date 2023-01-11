package com.wwp.model;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

public class Session {
    private Channel channel;
    private Promise<YlcResult> ackFuture;

   public Session() {
    }

    public Session(Channel channel)
    {
        this.channel = channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setAckFuture(Promise<YlcResult> ackFuture) {
        this.ackFuture = ackFuture;
    }
    public Promise<YlcResult> getAckFuture() {
        return ackFuture;
    }

    public <T> void setAttribute(String name, T value) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        channel.attr(sessionIdKey).set(value);
    }

    public <T> T getAttribute(String name) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        return channel.attr(sessionIdKey).get();
    }

    @Override
    public boolean equals(Object obj){
       if(obj instanceof Session){
           Session s1 =(Session)obj;
           return (this.channel.remoteAddress().equals(s1.getChannel().remoteAddress()) );
       }
       else return  false;
    }

}
