package com.wwp.model;

import java.util.Date;

//用于平台主动下发指令数据
public class YlcCtrlMsg {
    private String serialId;
    private Integer plugNo;
    private YlcMsgType msgType;
    private String businessId;
    private String logicId;//逻辑卡号用于显示在桩屏幕上便于用户核对卡信息
    private String physId;//读卡器读取到的卡号为“物理卡号” 用于电桩与服务器交互
    private Date date;

    //账户余额 4字节bin码---->b64编码
    private String account;

    public void setMsgType(YlcMsgType msgType) {
        this.msgType = msgType;
    }

    public YlcMsgType getMsgType() {
        return msgType;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }
    public String getSerialId() {
        return serialId;
    }

    public void setPlugNo(Integer plugNo) {
        this.plugNo = plugNo;
    }
    public Integer getPlugNo() {
        return plugNo;
    }

    public void setLogicId(String logicId) {
        this.logicId = logicId;
    }
    public String getLogicId() {
        return logicId;
    }

    public void setPhysId(String physId) {
        this.physId = physId;
    }
    public String getPhysId() {
        return physId;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getAccount() {
        return account;
    }
}
