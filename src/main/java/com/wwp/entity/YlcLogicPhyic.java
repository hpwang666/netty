package com.wwp.entity;

import com.wwp.common.annotation.Id;

import java.io.Serializable;

public class YlcLogicPhyic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String logical_num;

    private String physical_num;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogical_num() {
        return logical_num;
    }

    public void setLogical_num(String logical_num) {
        this.logical_num = logical_num;
    }

    public String getPhysical_num() {
        return physical_num;
    }

    public void setPhysical_num(String physical_num) {
        this.physical_num = physical_num;
    }
}
