package com.wwp.service.impl;

import com.wwp.entity.YlcCharger;
import com.wwp.mapper.YlcChargerMapper;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.service.IYlcChargerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class YlcChargerServiceImpl implements IYlcChargerService {

    @Resource
    YlcChargerMapper ylcChargerMapper;



    @Override
    public void add(YlcCharger ylcCharger)
    {
        ylcChargerMapper.add(ylcCharger);
    }

    @Override
    public YlcCharger getYlcChargerBySerialNum(String serialNum)
    {
        return ylcChargerMapper.getDevCharger(serialNum);
    }

    @Override
    public void updateTime(String serialNum, Date date)
    {
        ylcChargerMapper.updateTime(serialNum,date);
    }

    @Override
    public void updateStatus(String serialNum, Integer plugStatus,
                             Integer plugHoming, Integer slotIn)
    {
        ylcChargerMapper.updateStatus(serialNum,plugStatus,plugHoming,slotIn);
    }
}
