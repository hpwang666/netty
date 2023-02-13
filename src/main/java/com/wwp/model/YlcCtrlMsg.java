package com.wwp.model;

import java.util.Date;

//用于平台主动下发指令数据
public class YlcCtrlMsg {
    private String serialNum;
    private Integer plugNo;
    private YlcMsgType msgType;
    private String orderNum;
    private String logicNum;//逻辑卡号用于显示在桩屏幕上便于用户核对卡信息
    private String physicalNum;//读卡器读取到的卡号为“物理卡号” 用于电桩与服务器交互
    private String userId;

    //账户余额 4字节bin码---->b64编码
    private String account;

    public void setMsgType(YlcMsgType msgType) {
        this.msgType = msgType;
    }

    public YlcMsgType getMsgType() {
        return msgType;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
    public String getSerialNum() {
        return serialNum;
    }

    public void setPlugNo(Integer plugNo) {
        this.plugNo = plugNo;
    }
    public Integer getPlugNo() {
        return plugNo;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setLogicNum(String logicNum) {
        this.logicNum = logicNum;
    }
    public String getLogicNum() {
        return logicNum;
    }

    public void setPhysicalNum(String physicalNum) {
        this.physicalNum = physicalNum;
    }
    public String getPhysicalNum() {
        return physicalNum;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getAccount() {
        return account;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
