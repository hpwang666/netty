package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class YlcOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String orderNum;

    private String serialNum;

    private Integer plugNo;

    //生成订单下发充电桩时的下发金额
    private Integer startAmount;


    //充电用电量
    private Integer totalKwh;

    //充电花费的金额
    private Integer totalCost;

    //交易日期
    private Date orderTime;

    //物理卡号
    private String physicalNum;

   //停止原因
    private Integer stopType;

    //结算标志
    private Integer settleFlag;

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

    public void setStartAmount(Integer startAmount) {
        this.startAmount = startAmount;
    }
    public Integer getStartAmount() {
        return startAmount;
    }

    public void setTotalKwh(Integer totalKwh) {
        this.totalKwh = totalKwh;
    }
    public Integer getTotalKwh() {
        return totalKwh;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }
    public Integer getTotalCost() {
        return totalCost;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
    public Date getOrderTime() {
        return orderTime;
    }

    public void setPhysicalNum(String physicalNum) {
        this.physicalNum = physicalNum;
    }
    public String getPhysicalNum() {
        return physicalNum;
    }

    public void setStopType(Integer stopType) {
        this.stopType = stopType;
    }
    public Integer getStopType() {
        return stopType;
    }

    public void setSettleFlag(Integer settleFlag) {
        this.settleFlag = settleFlag;
    }
    public Integer getSettleFlag() {
        return settleFlag;
    }
}
