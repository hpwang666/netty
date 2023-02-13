package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;

public class YlcUserOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String userId;
    private String orderNum;

    public YlcUserOrder(){
        super();
    }

    public YlcUserOrder(String userId,String orderNum){

        this.userId = userId;
        this.orderNum = orderNum;
    }

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

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }
}
