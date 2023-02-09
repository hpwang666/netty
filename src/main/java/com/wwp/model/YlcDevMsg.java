package com.wwp.model;

import com.wwp.common.annotation.Id;

public class YlcDevMsg {

    @Id
    private String id;

    private YlcMsgHeader header;
    private YlcRecordMsg ylcRecordMsg;
    //充电枪状态
    private YlcStatusMsg ylcStatusMsg;

    private Object payload;
    private String error;
    private boolean success;

    //计费模型编码
    private String modelCode;

    //交易流水号
    private String orderNum;

    //序列号 7个字节
    private String serialNum;

    //充电桩类型
    private Integer type;

    //充电枪数量
    private Integer plugs;

    //充电枪号 01 开始
    private Integer plugNo;

    //0x00：离线   0x01：故障  0x02：空闲  0x03：充电
    private Integer plugStatus;



    //启动方式
    private Integer reqType;

    //充电桩是否启东成功
    private Integer startOk;

    //充电桩是否停止成功
    private Integer stopOk;

    //远程启停机错误码
    private Integer ctrlError;

    //卡号
    private String physicalNum;

    //通信协议版本
    private String ver;

    //程序版本
    private String swVer;

    //网络连接类型
    private Integer netType;

    //sim卡
    private String sim;

    //运营商
    private Integer service;

    //




    private byte crcL;
    private byte crcH;

    public YlcDevMsg(boolean success, YlcMsgHeader header, Object payload, String error)
    {
        this.success = success;
        this.header = header;
        this.payload = payload;
        this.error = error;
    }

    public YlcDevMsg() {

    }

    public void setYlcRecordMsg(YlcRecordMsg ylcRecordMsg) {
        this.ylcRecordMsg = ylcRecordMsg;
    }
    public YlcRecordMsg getYlcRecordMsg() {
        return ylcRecordMsg;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess()
    {return this.success;}

    public void setHeader(YlcMsgHeader header)
    {
        this.header = header;
    }
    public YlcMsgHeader getHeader() {
        return header;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
    public Object getPayload() {
        return payload;
    }

    public void setError(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return this.orderNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
    public String getSerialNum() {
        return serialNum;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getType() {
        return type;
    }

    public void setPlugs(Integer plugs) {
        this.plugs = plugs;
    }
    public Integer getPlugs() {
        return plugs;
    }

    public void setPlugNo(Integer plugNo) {
        this.plugNo = plugNo;
    }
    public Integer getPlugNo() {
        return plugNo;
    }

    public void setPlugStatus(Integer plugStatus) {
        this.plugStatus = plugStatus;
    }
    public Integer getPlugStatus() {
        return plugStatus;
    }

    public void setYlcStatusMsg(YlcStatusMsg ylcStatusMsg) {
        this.ylcStatusMsg = ylcStatusMsg;
    }
    public YlcStatusMsg getYlcStatusMsg() {
        return ylcStatusMsg;
    }

    public void setReqType(Integer reqType) {
        this.reqType = reqType;
    }
    public Integer getReqType() {
        return reqType;
    }

    public void setStartOk(Integer startOk) {
        this.startOk = startOk;
    }
    public Integer getStartOk() {
        return startOk;
    }

    public void setStopOk(Integer stopOk) {
        this.stopOk = stopOk;
    }

    public Integer getStopOk() {
        return stopOk;
    }

    public void setCtrlError(Integer ctrlError) {
        this.ctrlError = ctrlError;
    }
    public Integer getCtrlError() {
        return ctrlError;
    }

    public void setPhysicalNum(String physicalNum) {
        this.physicalNum = physicalNum;
    }
    public String getPhysicalNum() {
        return physicalNum;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
    public String getVer() {
        return ver;
    }

    public void setSwVer(String swVer) {
        this.swVer = swVer;
    }
    public String getSwVer() {
        return swVer;
    }

    public void setNetType(Integer netType) {
        this.netType = netType;
    }

    public Integer getNetType() {
        return netType;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getSim() {
        return sim;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    public Integer getService() {
        return service;
    }


}
