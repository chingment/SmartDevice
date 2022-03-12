package com.lumos.smartdevice.devicectrl;


import android.VendingMachine.symvdio;

import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;

public class LockerBoxCtrlByDs implements  ILockerBoxCtrl{

    private static final String TAG = "LockerBoxCtrlByDs";

    private static LockerBoxCtrlByDs mLockerBoxCtrlByDs= null;

    private symvdio sym = null;

    private LockerBoxCtrlByDs(){

        try {
            sym = new symvdio();
        } catch (Exception ex) {
            sym = null;
        }
    }



    public static LockerBoxCtrlByDs getInstance(String comId,int comBaud) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mLockerBoxCtrlByDs == null) {
            synchronized (LockerBoxCtrlByDs.class) {
                if (mLockerBoxCtrlByDs == null) {
                    mLockerBoxCtrlByDs = new LockerBoxCtrlByDs();
                    mLockerBoxCtrlByDs.connect(comId,comBaud);
                }
            }
        }
        return mLockerBoxCtrlByDs;
    }


    public void connect(String comId,int comBaud) {

        File file = new File("/dev/" + comId);
        if (file.exists()) {
            int rc_status = sym.Connect(comId, comBaud);
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，状态为：" + rc_status);
        } else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败，串口ID不存在");
        }

    }



    public void  open(String id,OnOpenListener onOpenListener) {

        sym.SN_MV_MotorAction(1, 1, 0);
        sym.SN_MV_MotorAction(1, 2, 0);

        onOpenListener.onSuccess();
    }
}
