package com.wwp.devices;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.PlatformDependent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * WebSocket用户集
 */
public class YlcDeviceMap {

    /**
     * 设备集
     */
    private static final ConcurrentMap<String, Channel> DEVICES = PlatformDependent.newConcurrentHashMap();


    private static YlcDeviceMap ourInstance = new YlcDeviceMap();

    private YlcDeviceMap() {
    }

    public static YlcDeviceMap getInstance() {
        return ourInstance;
    }

    /**
     * 存储通道
     *
     * @param key     唯一键
     * @param channel 通道
     */
    public static void put(String key, Channel channel) {
        DEVICES.put(key, channel);
    }

    public static boolean exist(String key){return DEVICES.containsKey(key);}
    /**
     * 移除通道
     *
     * @param channel 通道
     *
     * @return 移除结果
     */
    public static boolean remove(Channel channel) {
        String key = null;
        boolean b = DEVICES.containsValue(channel);
        if (b) {
            Set<Map.Entry<String, Channel>> entries = DEVICES.entrySet();
            for (Map.Entry<String, Channel> entry : entries) {
                Channel value = entry.getValue();
                if (value.equals(channel)) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            return true;
        }
        return remove(key);
    }

    /**
     * 移出通道
     *
     * @param key 键
     */
    public static boolean remove(String key) {
        Channel remove = DEVICES.remove(key);
        boolean containsValue = DEVICES.containsValue(remove);
      
        return containsValue;
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public static ConcurrentMap<String, Channel> getDEVICES() {
        return DEVICES;
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void sendMessageToUsers(String message) {
        Collection<Channel> values = DEVICES.values();
        for (Channel value : values) {
            value.write(new TextWebSocketFrame(message));
            value.flush();
        }
    }

    /**
     * 给某个人发送消息
     *
     * @param userName key
     * @param message  消息
     */
    public static void sendMessageToUser(String userName, String message) {
        Channel channel = DEVICES.get(userName);
        channel.write(new TextWebSocketFrame(message));
        channel.flush();
    }
}