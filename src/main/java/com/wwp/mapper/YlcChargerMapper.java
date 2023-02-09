package com.wwp.mapper;

import com.wwp.entity.YlcCharger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface YlcChargerMapper {
     void add(YlcCharger ylcCharger);
     YlcCharger getDevCharger(@Param("serialNum") String serialNum);
     void updateTime(@Param("serialNum")String serialNum,@Param("updateTime") Date updateTime);

     //更新充电桩的状态  以及是否插枪
     void updateStatus(@Param("serialNum")String serialNum,@Param("updateTime") Integer updateTime);
}
