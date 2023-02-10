package com.wwp.service.impl;

import com.wwp.entity.YlcUserLogical;
import com.wwp.mapper.YlcUserLogicalMapper;
import com.wwp.service.IYlcUserLogicService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

public class YlcUserLogicServiceImpl implements IYlcUserLogicService {

    @Resource
    YlcUserLogicalMapper ylcUserLogicalMapper;

    @Override
    public void add(YlcUserLogical ylcUserLogical)
    {
        ylcUserLogicalMapper.add(ylcUserLogical);
    }

    @Override
    public void increaseUserAmount(String userId, BigDecimal amount)
    {
        ylcUserLogicalMapper.updateUserAmount(userId,amount);
    }

    @Override
    public void decreaseUserAmount(String userId, BigDecimal amount)
    {
        ylcUserLogicalMapper.updateUserAmount(userId,amount);
    }

    @Override
    public BigDecimal queryUserAmount(String userId)
    {
        return ylcUserLogicalMapper.queryUserAmount(userId);
    }

    @Override
    public     String queryUserPhysicalNum(String userId)
    {
        return ylcUserLogicalMapper.queryUserPhysicalNum(userId);
    }

    @Override
    public YlcUserLogical queryByUserId(String userId)
    {
        return ylcUserLogicalMapper.queryByUserId(userId);
    }

}
