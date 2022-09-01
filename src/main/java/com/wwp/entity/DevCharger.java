package com.wwp.entity;

import com.wwp.common.annotation.Id;
import com.wwp.model.ChargerStatus;
import com.wwp.model.FeeModel;
import com.wwp.model.YlcMsgHeader;
import com.wwp.model.YlcRecord;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class DevCharger implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    //所属部门/车场
    private String departId;

    //序列号 7个字节
    private String serialNum;

    //充电桩类型
    private Integer type;

    //充电枪数量
    private Integer plugs;

    //计费模型编码
    private String modelCode;

    //0x00：离线   0x01：故障  0x02：空闲  0x03：充电
    private Integer plugStatus;

    private java.util.Date updateTime;

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }
    public String getDepartId() {
        return departId;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
    public String getSerialNum() {
        return this.serialNum;
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

    public void setPlugStatus(Integer plugStatus) {
        this.plugStatus = plugStatus;
    }
    public Integer getPlugStatus() {
        return plugStatus;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
    public String getModelCode() {
        return modelCode;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
}
