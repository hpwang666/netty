package com.wwp.service.impl;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.entity.YlcChargerStatus;
import com.wwp.entity.YlcOrder;
import com.wwp.entity.YlcUserOrder;
import com.wwp.mapper.YlcChargerStatusMapper;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.mapper.YlcUserOrderMapper;
import com.wwp.model.Session;
import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;
import com.wwp.service.IYlcCtrlService;
import com.wwp.service.IYlcOrderService;
import com.wwp.util.SpringBeanUtils;
import com.wwp.util.YlcStringUtils;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class YlcCtrlServiceImpl implements IYlcCtrlService {

    @Resource
    IYlcOrderService ylcOrderService;

    @Resource
    YlcChargerStatusMapper ylcChargerStatusMapper;

    @Resource
    YlcUserOrderMapper ylcUserOrderMapper;

    @Override
    public YlcResult updateAccountByPhysId(YlcCtrlMsg ctrlMsg)
    {
        return YlcResult.OK("updateAccountByPhysId");
    }

    @Override
    public YlcResult remoteDevOn(YlcCtrlMsg ctrlMsg)
    {
        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialNum());
        if(s == null) return YlcResult.error("设备不在线");

        YlcOrder ylcOrder = new YlcOrder();
        String orderNum = YlcStringUtils.genOrderNum(ctrlMsg.getSerialNum(),ctrlMsg.getPlugNo());
        ctrlMsg.setOrderNum(orderNum);

        ylcOrder.setOrderNum(orderNum);
        ylcOrder.setSerialNum(ctrlMsg.getSerialNum());
        ylcOrder.setPlugNo(ctrlMsg.getPlugNo());
        ylcOrder.setPhysicalNum(ctrlMsg.getPhysicalNum());
        ylcOrder.setSettleFlag(0);
        ylcOrderService.add(ylcOrder);

        //创建状态记录
        YlcChargerStatus ylcChargerStatus = new YlcChargerStatus();
        ylcChargerStatus.setOrderNum(orderNum);
        ylcChargerStatus.setSerialNum(ctrlMsg.getSerialNum());
        ylcChargerStatus.setPlugNo(ctrlMsg.getPlugNo());

        ylcChargerStatusMapper.add(ylcChargerStatus);

        YlcUserOrder ylcUserOrder = new YlcUserOrder(ctrlMsg.getUserId(),orderNum);

        ylcUserOrderMapper.add(ylcUserOrder);

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

        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialNum());
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
        Session s =(Session) YlcDeviceMap.getDEVICES().get(ctrlMsg.getSerialNum());
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
