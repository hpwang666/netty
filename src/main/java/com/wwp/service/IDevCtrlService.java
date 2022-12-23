package com.wwp.service;

import com.wwp.model.YlcCtrlMsg;
import com.wwp.model.YlcResult;

public interface IDevCtrlService {

    YlcResult updateAccountByPhysId(YlcCtrlMsg ctrlMsg);
    YlcResult remoteDevOn(YlcCtrlMsg ctrlMsg);

    YlcResult remoteAddPhysCard(YlcCtrlMsg ctrlMsg);
}
