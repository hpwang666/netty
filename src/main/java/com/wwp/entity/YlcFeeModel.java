package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;
import java.util.List;

public class YlcFeeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    //计费模型  bcd-->string
    private String modelCode;

    //将尖电费费率：40 0D 03 00 尖服务费费率：9C 40 00 00 转换成大端字节序存储为字符串(方便阅读查询)
    // "00030D40" "0000409C"
    private String fee0;//尖

    private String fee1;//峰

    private String fee2;//平

    private String fee3;//谷



    //计损比率  暂不支持 为 0
    private Integer lossRate;

    //里面存储有48个收费标准，按照半小时一条
    private String feesByModel;//将bin码直接转换成String 0x01,0x02,0x03 --->  "010203"

    public YlcFeeModel(String fee0, String fee1, String fee2, String fee3)
    {
        this.fee0 = fee0;
        this.fee1 = fee1;
        this.fee2 = fee2;
        this.fee3 = fee3;
    }

    public YlcFeeModel() {

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
