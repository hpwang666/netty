package com.wwp.mapper;

import com.wwp.entity.YlcChargerStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface YlcChargerStatusMapper {

    public void add(YlcChargerStatus ylcChargerStatus);
    public void update(YlcChargerStatus ylcChargerStatus);

}
