package com.wwp.service.impl;

import com.wwp.entity.DevCharger;
import com.wwp.mapper.DevChargerMapper;
import com.wwp.service.IDevChargerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class DevChargerServiceImpl implements IDevChargerService {

    @Resource
    DevChargerMapper devChargerMapper;

    @Override
    public void add(DevCharger devCharger)
    {
        devChargerMapper.add(devCharger);
    }

    @Override
    public DevCharger getDevChargerBySerialNum(String serialNum)
    {
        return devChargerMapper.getDevCharger(serialNum);
    }

    @Override
    public void updateTime(String serialNum, Date date)
    {
        devChargerMapper.updateTime(serialNum,date);
    }
}
