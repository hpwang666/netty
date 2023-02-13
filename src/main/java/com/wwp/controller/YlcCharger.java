package com.wwp.controller;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.entity.YlcUserLogical;
import com.wwp.entity.YlcUserOrder;
import com.wwp.mapper.YlcUserOrderMapper;
import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;
import com.wwp.service.IYlcCtrlService;
import com.wwp.service.IYlcUserLogicService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.wwp.model.YlcMsgType.*;

@RestController
@RequestMapping("/charger")
public class YlcCharger {
    @Resource
    IYlcCtrlService devCtrlService;

    @Resource
    IYlcUserLogicService ylcUserLogicService;



    @GetMapping("/channelList")
    public List<String> queryChannelList() {

        return YlcDeviceMap.listAllChannel();

    }

    @RequestMapping(value="/remoteOn",method= RequestMethod.GET)
    public String queryChargerList(@RequestParam String chargerId) {


        YlcCtrlMsg ctrlMsg = new YlcCtrlMsg();
        ctrlMsg.setMsgType(REMOTE_ON);
        ctrlMsg.setSerialNum(chargerId);
        ctrlMsg.setPlugNo(1);

        YlcUserLogical ylcUserLogical = ylcUserLogicService.queryByUserId("1111");

        ctrlMsg.setUserId("1111");
        ctrlMsg.setLogicNum(ylcUserLogical.getLogicalNum());//0000001000000573
        ctrlMsg.setPhysicalNum(ylcUserLogical.getLogicalNum());//00000000D14B0A54

        //金额太大了 就下发1000.00元
        if(ylcUserLogical.getAmount().toString().length()>6)
            ctrlMsg.setAccount("100000");//A0860100   1000.00元
        else{
            ctrlMsg.setAccount(ylcUserLogical.getAmount().toString());
        }


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
        ctrlMsg.setSerialNum("32010600213533");
        ctrlMsg.setPlugNo(1);
        ctrlMsg.setLogicNum("0000002022009092");
        ctrlMsg.setPhysicalNum("AAAAANFLClQ=");//00000000D1B2C3D4  AAAAANGyw9Q=
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
        ctrlMsg.setSerialNum("32010600213533");
        ctrlMsg.setPlugNo(1);

        YlcResult result =  devCtrlService.remoteDevOff(ctrlMsg);
        if(result.getSuccess()==true)
            return YlcDeviceMap.getDEVICES().size()+" ";
        else return result.getMessage();
    }

}
