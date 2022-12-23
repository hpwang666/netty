package com.wwp.service.impl;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import com.wwp.entity.FeeModel;
import com.wwp.mapper.FeeModelMapper;
import com.wwp.service.IFeeModelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FeeModelServiceImpl implements IFeeModelService {

    @Resource
    FeeModelMapper feeModelMapper;

    @Override
    public FeeModel getFeeModel(String modelCode)
    {
        return feeModelMapper.getFeeModelByCode(modelCode);
    }

}
