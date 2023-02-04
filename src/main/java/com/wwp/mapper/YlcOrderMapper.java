package com.wwp.mapper;

import com.wwp.entity.YlcCharger;
import com.wwp.entity.YlcOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface YlcOrderMapper {
    public void add(YlcOrder ylcOrder);
}
