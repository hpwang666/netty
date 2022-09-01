package com.wwp.service;

import com.wwp.entity.DevCharger;

import java.util.Date;

public interface IDevChargerService {
     void add(DevCharger devCharger);
     DevCharger getDevChargerBySerialNum(String serialNum);
     void updateTime(String serialNum, Date date);
}
