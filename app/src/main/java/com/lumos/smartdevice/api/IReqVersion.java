package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLoginByAccount;

import java.util.HashMap;
import java.util.Map;

public interface IReqVersion {

    void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler);
    void loginByAccount(RopLoginByAccount rop, final ReqHandler reqHandler);
}
