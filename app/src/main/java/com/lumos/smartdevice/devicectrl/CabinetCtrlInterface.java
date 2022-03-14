package com.lumos.smartdevice.devicectrl;

import com.lumos.smartdevice.utils.LogUtil;

public class CabinetCtrlInterface
{
    private static final String TAG = "CabinetCtrlInterface";

    public static ICabinetCtrl getInstance(String comId, int comBaud, String comPrl) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud + ",comPrl:" + comPrl);
        return CabinetCtrlByDs.getInstance(comId, comBaud);
    }
}
