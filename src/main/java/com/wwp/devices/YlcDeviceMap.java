package com.wwp.devices;

import com.wwp.model.Session;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


public class YlcDeviceMap {

    /**
     * 设备集
     */
    private static final ConcurrentMap<String, Session> DEVICES = PlatformDependent.newConcurrentHashMap();


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
     * @param session 通道
     */
    public static void put(String key, Session session) {
        DEVICES.put(key, session);
    }
    public static Session get(String key) {
        return DEVICES.get(key);
    }

    public static boolean exist(String key){return DEVICES.containsKey(key);}
    /**
     * 移除通道
     *
     * @param session 通道
     *
     * @return 移除结果
     */
    public static boolean remove(Session session) {
        String key = null;
        boolean b = DEVICES.containsValue(session);
        if (b) {
            Set<Map.Entry<String, Session>> entries = DEVICES.entrySet();
            for (Map.Entry<String, Session> entry : entries) {
                Session value = entry.getValue();
                if (value.equals(session)) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            return true;
        }
        return remove(key);
    }


    public static boolean remove(Channel channel) {
        String key = null;

        Set<Map.Entry<String, Session>> entries = DEVICES.entrySet();
        for (Map.Entry<String, Session> entry : entries) {
            Session value = entry.getValue();
            if (value.getChannel().equals(channel)) {
                key = entry.getKey();
                break;
            }
        }
        if(key!=null)
         return remove(key);
        else return true;
    }
    /**
     * 移出通道
     *
     * @param key 键
     */
    public static boolean remove(String key) {
        Session remove = DEVICES.remove(key);
        boolean containsValue = DEVICES.containsValue(remove);
      
        return containsValue;
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public static ConcurrentMap<String, Session> getDEVICES() {
        return DEVICES;
    }


    public static List<String> listAllChannel() {

        List<String> listAllChannel =new ArrayList<String>();
        Set<Map.Entry<String, Session>> entries = DEVICES.entrySet();
        for (Map.Entry<String, Session> entry : entries) {
            String serialId = entry.getKey();
            Session value = entry.getValue();
            listAllChannel.add(serialId+": "+value.getChannel().remoteAddress());
        }


        return listAllChannel;
    }


}