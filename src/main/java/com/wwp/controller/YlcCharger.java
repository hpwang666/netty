package com.wwp.controller;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.model.Session;
import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;
import com.wwp.service.IDevCtrlService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.netty.channel.Channel;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.wwp.model.YlcMsgType.CARD_UPDATE;
import static com.wwp.model.YlcMsgType.REMOTE_ON;

@RestController
@RequestMapping("/charger")
public class YlcCharger {
    @Resource
    IDevCtrlService devCtrlService;

    @GetMapping("/list")
    public String queryChargerList() {


        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(REMOTE_ON);
        ctrlMsg.setSerialId("32010203040506");
        ctrlMsg.setPlugNo(1);
        ctrlMsg.setLogicId("0000001000000573");
        ctrlMsg.setPhysId("AAAAANFLClQ=");//00000000D14B0A54
        ctrlMsg.setAccount("oIYBAA==");//A0860100   1000.00元

        YlcResult result =  devCtrlService.remoteDevOn(ctrlMsg);
        if(result.getSuccess()==true)
        return YlcDeviceMap.getDEVICES().size()+" ";
        else
            return result.getMessage();
    }

    @GetMapping("/remoteAddPhysCard")
    public String remoteAddPhys() {
        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(CARD_UPDATE);
        ctrlMsg.setSerialId("32010203040506");
        ctrlMsg.setPlugNo(1);
        ctrlMsg.setLogicId("0000001000000573");
        ctrlMsg.setPhysId("AAAAANFLClQ=");//00000000D14B0A54
        ctrlMsg.setAccount("oIYBAA==");//A0860100   1000.00元

        YlcResult result =  devCtrlService.remoteAddPhysCard(ctrlMsg);
        System.out.println(result.getMessage());
        if(result.getSuccess()==true)
            return YlcDeviceMap.getDEVICES().size()+" ";
        else
            return result.getMessage();
    }
}
