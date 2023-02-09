package com.wwp.service.impl;

import com.wwp.entity.YlcOrder;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.service.IYlcOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class YlcOrderServiceImpl implements IYlcOrderService {

    @Resource
    YlcOrderMapper ylcOrderMapper;

    @Override
    public void add (YlcOrder ylcOrder)
    {
        ylcOrderMapper.add(ylcOrder);
    }
}
