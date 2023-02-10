package com.wwp.service;

import com.wwp.entity.YlcCharger;

import java.util.Date;

public interface IYlcChargerService {
     void add(YlcCharger ylcCharger);
     YlcCharger getDevChargerBySerialNum(String serialNum);
     void updateTime(String serialNum, Date date);
     void updateStatus(String serialNum,);
}
