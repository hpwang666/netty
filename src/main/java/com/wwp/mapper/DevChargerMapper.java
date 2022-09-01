package com.wwp.mapper;

import com.wwp.entity.DevCharger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface DevChargerMapper {
    public void add(DevCharger devCharger);
    public DevCharger getDevCharger(@Param("serialNum") String serialNum);
    public void updateTime(@Param("serialNum")String serialNum,@Param("updateTime") Date updateTime);
}
