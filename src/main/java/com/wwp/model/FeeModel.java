package com.wwp.model;

import java.util.List;

public class FeeModel {

    //private Integer fee0;//尖
   // private Integer fee1;//峰
   // private Integer fee2;//平
   // private Integer fee3;//谷

    //计费模型编码
    private String modelCode;
    private List<Integer> fees;

    //里面存储有48个收费标准，按照半小时一条
    private List<Integer> feesByTime;

    public FeeModel(Integer fee0,Integer fee1,Integer fee2,Integer fee3)
    {
        fees.add(fee0);
        fees.add(fee1);
        fees.add(fee2);
        fees.add(fee3);
    }

    public FeeModel() {

    }

    public void setFeesByTime(Integer index,Integer fee)
    {
        feesByTime.add(index,fee);
    }
    public List<Integer> getFeesByTime() {
        return feesByTime;
    }

    public Integer getFee(Integer index)
    {
        return this.fees.get(index);
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelCode() {
        return modelCode;
    }
}
