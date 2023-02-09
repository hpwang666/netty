package com.wwp.mapper;

import com.wwp.entity.YlcOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface YlcOrderMapper {
     void add(YlcOrder ylcOrder);
     void update(YlcOrder ylcOrder);
}
