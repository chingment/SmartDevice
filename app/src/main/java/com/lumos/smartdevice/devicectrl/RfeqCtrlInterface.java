package com.lumos.smartdevice.devicectrl;

import com.lumos.smartdevice.utils.LogUtil;

public class RfeqCtrlInterface {

    private static final String TAG = "RfIdCtrlInterface";

    public static IRfeqCtrl getInstance(String comId, int comBaud, String comPrl) {

        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud + ",comPrl:" + comPrl);

        return RfeqCtrlByDs.getInstance(comId, comBaud,comPrl);

    }
}
