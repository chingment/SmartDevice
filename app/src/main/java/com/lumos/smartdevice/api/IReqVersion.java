package com.lumos.smartdevice.api;

import java.util.HashMap;
import java.util.Map;

public interface IReqVersion {

    void initDeviceData(Map<String, Object> d,final ReqHandler reqHandler);
    void loginByAccount(String username,String password,final ReqHandler reqHandler);
}
