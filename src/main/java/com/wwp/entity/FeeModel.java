package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;
import java.util.List;

public class FeeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    //计费模型  bcd-->string
    private String modelCode;

    //将尖电费费率：9C 40 00 00尖服务费费率：E0 93 04 00 共八个字节进行base64编码
    private String fee0;//尖
    private String fee1;//峰
    private String fee2;//平
    private String fee3;//谷



    //计损比率
    private Integer lossRate;

    //里面存储有48个收费标准，按照半小时一条
    private String feesByModel;//将bin码直接转换成B64 0x01,0x02,0x03 --->  "AXzs0="

    public FeeModel(String fee0,String fee1,String fee2,String fee3)
    {
        this.fee0 = fee0;
        this.fee1 = fee1;
        this.fee2 = fee2;
        this.fee3 = fee3;
    }

    public FeeModel() {

    }


    public String getFee0() {
        return fee0;
    }

    public String getFee1() {
        return fee1;
    }

    public String getFee2() {
        return fee2;
    }

    public String getFee3() {
        return fee3;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
    public String getModelCode() {
        return modelCode;
    }

    public void setLossRate(Integer lossRate) {
        this.lossRate = lossRate;
    }
    public Integer getLossRate() {
        return lossRate;
    }

    public void setFeesByModel(String feesByModel) {
        this.feesByModel = feesByModel;
    }
    public String getFeesByModel() {
        return feesByModel;
    }
}
