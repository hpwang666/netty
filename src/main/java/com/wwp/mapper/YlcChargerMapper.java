package com.wwp.mapper;

import com.wwp.entity.YlcCharger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface YlcChargerMapper {
    public void add(YlcCharger ylcCharger);
    public YlcCharger getDevCharger(@Param("serialNum") String serialNum);
    public void updateTime(@Param("serialNum")String serialNum,@Param("updateTime") Date updateTime);
}
