package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;

public class YlcUserOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String userId;
    private String orderId;

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

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
