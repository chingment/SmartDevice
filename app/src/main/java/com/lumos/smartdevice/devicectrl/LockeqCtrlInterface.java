package com.lumos.smartdevice.devicectrl;

import com.lumos.smartdevice.utils.LogUtil;

public class LockeqCtrlInterface
{
    private static final String TAG = "CabinetCtrlInterface";

    public static ILockeqCtrl getInstance(String comId, int comBaud, String comPrl) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud + ",comPrl:" + comPrl);
        return LockeqCtrlByDs.getInstance(comId, comBaud);
    }
}
