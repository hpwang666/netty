package com.wwp.mapper;

import com.wwp.entity.YlcFeeModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface YlcFeeModelMapper {
    YlcFeeModel getFeeModelByCode(String modelCode);
}
