package com.lumos.smartdevice.devicectrl;


public class LockerBoxCtrlByDs implements  ILockerBoxCtrl{

    private static final String TAG = "LockerBoxCtrlByDs";

    private static LockerBoxCtrlByDs mLockerBoxCtrlByDs= null;

    public static LockerBoxCtrlByDs getInstance(String comId,int comBaud) {
        if (mLockerBoxCtrlByDs == null) {
            synchronized (LockerBoxCtrlByDs.class) {
                if (mLockerBoxCtrlByDs == null) {
                    mLockerBoxCtrlByDs = new LockerBoxCtrlByDs();
                }
            }
        }
        return mLockerBoxCtrlByDs;
    }


    public void  open(String id,OnOpenListener onOpenListener){


    }
}
