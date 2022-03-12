package com.lumos.smartdevice.devicectrl;

import com.lumos.smartdevice.utils.LogUtil;

public class LockerBoxInterface
{
    private static final String TAG = "LockerBoxInterface";

    public static ILockerBoxCtrl getInstance(String comId,int comBaud, String comPrl) {

        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud + ",comPrl:" + comPrl);

        ILockerBoxCtrl lockerBoxCtrl = null;

        switch (comPrl) {
            case "Prl_A31":
                lockerBoxCtrl = LockerBoxCtrlByDs.getInstance(comId, comBaud);
                break;
            default:
                break;
        }

        return lockerBoxCtrl;

    }
}
