package com.wwp.mapper;

import com.wwp.entity.FeeModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeeModelMapper {
    FeeModel getFeeModelByCode(String modelCode);
}
