package com.wwp.service;

import com.wwp.entity.YlcUserLogical;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IYlcUserLogicService {
    void add(YlcUserLogical ylcUserLogical);

    YlcUserLogical queryByUserId(String userId);

    void increaseUserAmount(String userId, BigDecimal amount);

    void decreaseUserAmount(String userId, BigDecimal amount);

    BigDecimal queryUserAmount(String userId);

    String queryUserPhysicalNum(String userId);

}
