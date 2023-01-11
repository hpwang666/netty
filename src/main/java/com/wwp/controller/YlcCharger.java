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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.wwp.model.YlcMsgType.*;

@RestController
@RequestMapping("/charger")
public class YlcCharger {
    @Resource
    IDevCtrlService devCtrlService;


    @GetMapping("/channelList")
    public List<String> queryChannelList() {

        return YlcDeviceMap.listAllChannel();

    }

    @GetMapping("/remoteOn")
    public String queryChargerList() {


        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(REMOTE_ON);
        ctrlMsg.setSerialId("32010600213533");
        ctrlMsg.setPlugNo(1);
        ctrlMsg.setLogicId("0000002022009090");//0000001000000573
        ctrlMsg.setPhysId("AAAAANFLClQ=");//00000000D14B0A54
        ctrlMsg.setAccount("oIYBAA==");//A0860100   1000.00元


        YlcResult result =  devCtrlService.remoteDevOn(ctrlMsg);
        if(result.getSuccess()==true)
        return YlcDeviceMap.getDEVICES().size()+" ";
        else return result.getMessage();
    }

    //这个卡的物理卡号  00000000A5FBCA5B
    @GetMapping("/remoteAddPhysCard")
    public String remoteAddPhys() {
        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(CARD_UPDATE);
        ctrlMsg.setSerialId("32010600213533");
        ctrlMsg.setPlugNo(1);
        ctrlMsg.setLogicId("0000002022009092");
        ctrlMsg.setPhysId("AAAAANFLClQ=");//00000000D1B2C3D4  AAAAANGyw9Q=
        ctrlMsg.setAccount("oIYBAA==");//A0860100   1000.00元

        YlcResult result =  devCtrlService.remoteAddPhysCard(ctrlMsg);
        System.out.println(result.getMessage());
        if(result.getSuccess()==true)
            return YlcDeviceMap.getDEVICES().size()+" ";
        else
            return result.getMessage();
    }

    @GetMapping("/remoteOff")
    public String remoteOff() {


        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(REMOTE_OFF);
        ctrlMsg.setSerialId("32010600213533");
        ctrlMsg.setPlugNo(1);

        YlcResult result =  devCtrlService.remoteDevOff(ctrlMsg);
        if(result.getSuccess()==true)
            return YlcDeviceMap.getDEVICES().size()+" ";
        else return result.getMessage();
    }

}
