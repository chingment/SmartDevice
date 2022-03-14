package com.lumos.smartdevice.devicectrl;

import com.lumos.smartdevice.utils.LogUtil;

public class RfIdCtrlInterface {

    private static final String TAG = "RfIdCtrlInterface";

    public static IRfIdCtrl getInstance(String comId,int comBaud, String comPrl) {

        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud + ",comPrl:" + comPrl);

        return RfIdCtrlByDs.getInstance(comId, comBaud);

    }
}
