package com.wwp.model;

import java.util.Date;

public class YlcRecord {
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
    private long recordStartKwh;
    private long recordEndKwh;
    private long recordTotalKwh;
    private long lossTotalKwh;
    private long totalCost;//所有花费

    //交易方式 0x01:app    0x02:card  0x04:离线卡启东  0x05:vin码
    private Integer tradeType;



    private String recordFee0;
    private String recordFee1;
    private String recordFee2;
    private String recordFee3;

    public YlcRecord()
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

    public void setRecordStartKwh(long recordStartKwh) {
        this.recordStartKwh = recordStartKwh;
    }
    public long getRecordStartKwh() {
        return recordStartKwh;
    }

    public void setRecordEndKwh(long recordEndKwh) {
        this.recordEndKwh = recordEndKwh;
    }
    public long getRecordEndKwh() {
        return recordEndKwh;
    }

    public void setRecordTotalKwh(long recordTotalKwh) {
        this.recordTotalKwh = recordTotalKwh;
    }
    public long getRecordTotalKwh() {
        return recordTotalKwh;
    }

    public void setLossTotalKwh(long lossTotalKwh) {
        this.lossTotalKwh = lossTotalKwh;
    }
    public long getLossTotalKwh() {
        return lossTotalKwh;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }
    public long getTotalCost() {
        return totalCost;
    }

    public void setRecordFee0(String recordFee0) {
        this.recordFee0 = recordFee0;
    }
    public String getRecordFee0() {
        return recordFee0;
    }

    public void setRecordFee1(String recordFee1) {
        this.recordFee1 = recordFee1;
    }
    public String getRecordFee1() {
        return recordFee1;
    }

    public void setRecordFee2(String recordFee2) {
        this.recordFee2 = recordFee2;
    }
    public String getRecordFee2() {
        return recordFee2;
    }

    public void setRecordFee3(String recordFee3) {
        this.recordFee3 = recordFee3;
    }

    public String getRecordFee3() {
        return recordFee3;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
    public Integer getTradeType() {
        return tradeType;
    }
}
