package com.wwp.controller;

import com.wwp.devices.YlcDeviceMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/charger")
public class YlcCharger {

    @GetMapping("/list")
    public String queryChargerList() {

        return YlcDeviceMap.getDEVICES().size()+" ";
    }
}
