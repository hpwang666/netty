package com.wwp.common.property;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator   {

    public Serializable generator() {
        // 自行修改此处生成id方案
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
