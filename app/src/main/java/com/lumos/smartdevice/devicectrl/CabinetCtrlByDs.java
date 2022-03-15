package com.lumos.smartdevice.devicectrl;


import android.VendingMachine.symvdio;

import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;

public class CabinetCtrlByDs implements ICabinetCtrl {

    private static final String TAG = "CabinetCtrlByDs";

    private static CabinetCtrlByDs mThis= null;
    private symvdio sym;

    private CabinetCtrlByDs(){
        try {
            sym = new symvdio();
        } catch (Exception ex) {
            sym = null;
        }
    }



    public static CabinetCtrlByDs getInstance(String comId, int comBaud) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mThis == null) {
            synchronized (CabinetCtrlByDs.class) {
                if (mThis == null) {
                    mThis = new CabinetCtrlByDs();
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


    public void  open(String id,OnListener onOpenListener) {

        int var0 = sym.SN_MV_MotorAction(1, 1, 0);

        int[] var1 = sym.SN_MV_Get_ColData(1);

        if (var0 == 2||var0 == 4) {
            onOpenListener.onSendCommandSuccess();

        } else {
            onOpenListener.onSendCommnadFailure();
        }


        if (var1[0] == 2||var1[0] == 4) {
            onOpenListener.onOpenSuccess();
        } else {
            onOpenListener.onOpenFailure();
        }

    }
}
