package com.wwp.service.impl;

import com.wwp.entity.YlcFeeModel;
import com.wwp.mapper.YlcFeeModelMapper;
import com.wwp.service.IYlcFeeModelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class YlcFeeModelServiceImpl implements IYlcFeeModelService {

    @Resource
    YlcFeeModelMapper ylcFeeModelMapper;

    @Override
    public YlcFeeModel getFeeModel(String modelCode)
    {
        return ylcFeeModelMapper.getFeeModelByCode(modelCode);
    }

}
