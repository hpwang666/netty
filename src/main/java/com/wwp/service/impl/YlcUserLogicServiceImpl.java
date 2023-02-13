package com.wwp.service.impl;

import com.wwp.entity.YlcUserLogical;
import com.wwp.mapper.YlcUserLogicalMapper;
import com.wwp.service.IYlcUserLogicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
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
        BigDecimal userAmount = ylcUserLogicalMapper.queryUserAmount(userId);
        ylcUserLogicalMapper.updateUserAmount(userId,userAmount.add(amount));


    }

    @Override
    public void decreaseUserAmount(String userId, BigDecimal amount)
    {
        BigDecimal userAmount = ylcUserLogicalMapper.queryUserAmount(userId);

        if(userAmount.compareTo(amount)>=0)
            ylcUserLogicalMapper.updateUserAmount(userId,userAmount.subtract(amount));
        else
            ylcUserLogicalMapper.updateUserAmount(userId,new BigDecimal(0).setScale(0));
        
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
