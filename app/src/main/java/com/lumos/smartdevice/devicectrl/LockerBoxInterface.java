package com.lumos.smartdevice.devicectrl;

public class LockerBoxInterface
{

    public static ILockerBoxCtrl getInstance(String com_id,int comBaud, String comPrl) {

        ILockerBoxCtrl lockerBoxCtrl = null;

        switch (comPrl) {
            case "":
                lockerBoxCtrl = new LockerBoxCtrlByDs();
                break;
        }

        return lockerBoxCtrl;

    }
}
