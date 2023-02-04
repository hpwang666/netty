package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;

public class YlcUserLogical  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String userId;

    private String logicalNum;

    //账户余额
    private java.math.BigInteger amount;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setLogicalNum(String logicalNum) {
        this.logicalNum = logicalNum;
    }
    public String getLogicalNum() {
        return logicalNum;
    }
    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
    public BigInteger getAmount() {
        return amount;
    }
}
