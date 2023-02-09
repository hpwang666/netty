package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class YlcChargerStatus  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    //交易流水号
    private String orderNum;

    //序列号 14个字节
    private String serialNum;

    private Integer plugNo;

    private Integer voltage;

    private Integer current;

    private Integer chargeMin;

    private Integer chargeKwh;

    private Integer lossKwh;

    private Integer chargeCost;

    //private Integer settleFlag;

    private java.util.Date updateTime;

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
    public String getOrderNum() {
        return orderNum;
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

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }
    public Integer getVoltage() {
        return voltage;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }
    public Integer getCurrent() {
        return current;
    }

    public void setChargeMin(Integer chargeMin) {
        this.chargeMin = chargeMin;
    }
    public Integer getChargeMin() {
        return chargeMin;
    }

    public void setChargeKwh(Integer chargeKwh) {
        this.chargeKwh = chargeKwh;
    }
    public Integer getChargeKwh() {
        return chargeKwh;
    }

    public void setLossKwh(Integer lossKwh) {
        this.lossKwh = lossKwh;
    }
    public Integer getLossKwh() {
        return lossKwh;
    }

    public void setChargeCost(Integer chargeCost) {
        this.chargeCost = chargeCost;
    }
    public Integer getChargeCost() {
        return chargeCost;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
}
