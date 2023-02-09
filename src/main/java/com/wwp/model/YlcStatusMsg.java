package com.wwp.model;

public class YlcStatusMsg {


    // 0x00归位  0x01：未归位  0x02 未知
    private Integer plugHoming;

    //是否插枪 0x00 no   0x01 yes
    private Integer  slotIn;

    private Integer voltage;

    private Integer current;

    private Integer wireTmp;

    private Integer wireId;

    private Integer SOC;

    private Integer batteryTmp;

    private Integer totalChargeTime;

    private Integer remainChargeTime;

    private Integer chargeKwh;

    private Integer lossKwh;

    private Integer chargeCost;

    private Integer failure;



    public void setPlugHoming(Integer plugHoming) {
        this.plugHoming = plugHoming;
    }
    public Integer getPlugHoming() {
        return plugHoming;
    }

    public void setSlotIn(Integer slotIn) {
        this.slotIn = slotIn;
    }
    public Integer getSlotIn() {
        return slotIn;
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

    public void setWireId(Integer wireId) {
        this.wireId = wireId;
    }
    public Integer getWireId() {
        return wireId;
    }

    public void setSOC(Integer SOC) {
        this.SOC = SOC;
    }
    public Integer getSOC() {
        return SOC;
    }

    public void setWireTmp(Integer wireTmp) {
        this.wireTmp = wireTmp;
    }

    public Integer getWireTmp() {
        return wireTmp;
    }

    public void setBatteryTmp(Integer batteryTmp) {
        this.batteryTmp = batteryTmp;
    }
    public Integer getBatteryTmp() {
        return batteryTmp;
    }

    public void setTotalChargeTime(Integer totalChargeTime) {
        this.totalChargeTime = totalChargeTime;
    }
    public Integer getTotalChargeTime() {
        return totalChargeTime;
    }

    public void setRemainChargeTime(Integer remainChargeTime) {
        this.remainChargeTime = remainChargeTime;
    }
    public Integer getRemainChargeTime() {
        return remainChargeTime;
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

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public Integer getFailure() {
        return failure;
    }
}
