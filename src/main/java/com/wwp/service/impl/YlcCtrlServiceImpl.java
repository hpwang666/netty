package com.wwp.service.impl;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.Session;
import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;
import com.wwp.service.IYlcCtrlService;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class YlcCtrlServiceImpl implements IYlcCtrlService {

    @Override
    public YlcResult updateAccountByPhysId(YlcCtrlMsg ctrlMsg)
    {
        return YlcResult.OK("updateAccountByPhysId");
    }

    @Override
    public YlcResult remoteDevOn(YlcCtrlMsg ctrlMsg)
    {

        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialId());
        if(s == null) return YlcResult.error("设备不在线");

        Future f = s.getChannel().writeAndFlush(ctrlMsg);
        f.addListener(future-> {
            System.out.println("已经写成功啦");
        });

        try{
            Thread.sleep(10);
            YlcResult result =  (YlcResult) s.getAckFuture().get(2000, TimeUnit.MILLISECONDS);
            System.out.println(Thread.currentThread()+"i: "+result);
        }
        catch(TimeoutException e)
        {
            // e.printStackTrace();
            return YlcResult.error("设备没响应");
        }
        catch(InterruptedException| ExecutionException e)
        {
            return YlcResult.error("错误"+e.toString());
        }
        return YlcResult.OK("remoteDevOn");
    }

    @Override
    public YlcResult remoteDevOff(YlcCtrlMsg ctrlMsg)
    {

        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialId());
        if(s == null) return YlcResult.error("设备不在线");

        Future f = s.getChannel().writeAndFlush(ctrlMsg);
        f.addListener(future-> {
            System.out.println("已经写成功啦");
        });

        try{
            Thread.sleep(10);
            YlcResult result =  (YlcResult) s.getAckFuture().get(5000, TimeUnit.MILLISECONDS);
            System.out.println(Thread.currentThread()+"i: "+result);
        }
        catch(TimeoutException e)
        {
            // e.printStackTrace();
            return YlcResult.error("设备没响应");
        }
        catch(InterruptedException| ExecutionException e)
        {
            return YlcResult.error("错误"+e.toString());
        }
        return YlcResult.OK("remoteDevOff");
    }

    @Override
    public YlcResult remoteAddPhysCard(YlcCtrlMsg ctrlMsg)
    {
        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialId());
        if(s == null) return YlcResult.error("设备不在线");

        Future f = s.getChannel().writeAndFlush(ctrlMsg);
        f.addListener(future-> {
            System.out.println("已经写成功啦");
        });

        try{
            Thread.sleep(10);
            YlcResult result =  (YlcResult) s.getAckFuture().get(2000, TimeUnit.MILLISECONDS);
           // System.out.println(Thread.currentThread()+"i: "+result.getMessage());
            return result;
        }
        catch(TimeoutException e)
        {
            // e.printStackTrace();
            return YlcResult.error("下发卡号失败,设备没响应");
        }
        catch(InterruptedException| ExecutionException e)
        {
            return YlcResult.error("错误"+e.toString());
        }

    }

}
