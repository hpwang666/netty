package com.wwp.model;

import java.util.Date;

public class YlcRecordMsg {
    private Date startTime;
    private Date endTime;
/*
    private long fee0Price;
    private long fee0Kwh;
    private long fee0LossKwh;
    private long fee0Money;

    private long fee1Price;
    private long fee1Kwh;
    private long fee1LossKwh;
    private long fee1Money;

    private long fee2Price;
    private long fee2Kwh;
    private long fee2LossKwh;
    private long fee2Money;

    private long fee3Price;
    private long fee3Kwh;
    private long fee3LossKwh;
    private long fee3Money;
 */
    //对尖  电量   电价  计损  金额 共16个字节进行base64编码
    private String fee0All;
    private String fee1All;
    private String fee2All;
    private String fee3All;


    //private String recordStartKwh; //这两个是5个字节
   // private String recordEndKwh;
    private Integer recordTotalKwh;
    private Integer lossTotalKwh;//计损总电量
    private Integer totalCost;//所有花费

    //交易方式 0x01:app    0x02:card  0x04:离线卡启东  0x05:vin码
    private Integer tradeType;

    private Date businessDate;//交易日期时间

    private Integer stopType; //停止原因


    public YlcRecordMsg()
    {

    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public Date getEndTime() {
        return endTime;
    }

    public void setFee0All(String fee0All) {
        this.fee0All = fee0All;
    }
    public String getFee0All() {
        return fee0All;
    }

    public void setFee1All(String fee1All) {
        this.fee1All = fee1All;
    }
    public String getFee1All() {
        return fee1All;
    }

    public void setFee2All(String fee2All) {
        this.fee2All = fee2All;
    }
    public String getFee2All() {
        return fee2All;
    }

    public void setFee3All(String fee3All) {
        this.fee3All = fee3All;
    }
    public String getFee3All() {
        return fee3All;
    }



    public void setRecordTotalKwh(Integer recordTotalKwh) {
        this.recordTotalKwh = recordTotalKwh;
    }
    public Integer getRecordTotalKwh() {
        return recordTotalKwh;
    }

    public void setLossTotalKwh(Integer lossTotalKwh) {
        this.lossTotalKwh = lossTotalKwh;
    }
    public Integer getLossTotalKwh() {
        return lossTotalKwh;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }
    public Integer getTotalCost() {
        return totalCost;
    }


    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
    public Integer getTradeType() {
        return tradeType;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }
    public Date getBusinessDate() {
        return businessDate;
    }

    public void setStopType(Integer stopType) {
        this.stopType = stopType;
    }

    public Integer getStopType() {
        return stopType;
    }
}
