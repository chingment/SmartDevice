package com.lumos.smartdevice.devicectrl;


import android.VendingMachine.symvdio;

import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;

public class LockeqCtrlByDs implements ILockeqCtrl {

    private static final String TAG = "CabinetCtrlByDs";

    private static LockeqCtrlByDs mThis= null;

    private symvdio sym;

    private LockeqCtrlByDs(){
        try {
            sym = new symvdio();
        } catch (Exception ex) {
            sym = null;
        }
    }



    public static LockeqCtrlByDs getInstance(String comId, int comBaud) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mThis == null) {
            synchronized (LockeqCtrlByDs.class) {
                if (mThis == null) {
                    mThis = new LockeqCtrlByDs();
                    mThis.connect(comId,comBaud);
                }
            }
        }
        return mThis;
    }


    public void connect(String comId,int comBaud) {

        File file = new File("/dev/" + comId);
        if (file.exists()) {
            int rc_status = sym.Connect(comId, comBaud);
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，状态为：" + rc_status);
        } else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败");
        }

    }


    public boolean  sendOpenSlot(String id) {

        int var0 = sym.SN_MV_MotorAction(1, 1, 0);


        return true;

    }

    public int  getSlotStatus(String id) {

        int[] var1 = sym.SN_MV_Get_ColData(1);

        return 0;
    }
}
