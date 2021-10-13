package com.lumos.smartdevice.devicectrl;

public class LockerBoxInterface
{
    private static final String TAG = "LockerBoxInterface";

    public static ILockerBoxCtrl getInstance(String comId,int comBaud, String comPrl) {

        ILockerBoxCtrl lockerBoxCtrl = null;

        switch (comPrl) {
            case "":
                lockerBoxCtrl = LockerBoxCtrlByDs.getInstance(comId,comBaud);
                break;
        }

        return lockerBoxCtrl;

    }
}
