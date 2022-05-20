package com.lumos.smartdevice.app;

import android.app.Activity;

import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.utils.StringUtil;


public class AppUtil {

    public  static  String getDeviceStatus() {
        String status = "unknow";

        DeviceVo device = AppCacheManager.getDevice();

        if (device == null)
            return status;

        if(StringUtil.isEmptyNotNull(device.getDeviceId()))
            return status;

        Activity activity = AppManager.getAppManager().currentActivity();
        if (activity != null) {
            String activityName = activity.getLocalClassName();
            if (activityName.contains(".Sm")) {
                status = "setting";
            } else {
                if (device.getExIsHas()) {
                    status = "exception";
                } else {
                    status = "running";
                }
            }
        }
        return status;
    }

    public  static Boolean deviceIsIdle() {

        Activity activity = AppManager.getAppManager().currentActivity();

        if (activity == null)
            return true;

        String activityName = activity.getLocalClassName();

        if (activityName.contains(".BookerBorrowReturnInspectActivity"))
            return false;
        if (activityName.contains(".BookerBorrowReturnOverviewActivity"))
            return false;
        if (activityName.contains(".BookerIdentityVerifyActivity"))
            return false;

        return true;
    }
}
