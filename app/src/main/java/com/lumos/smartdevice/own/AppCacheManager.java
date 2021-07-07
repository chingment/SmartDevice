package com.lumos.smartdevice.own;


import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.utils.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by chingment on 2018/1/16.
 */

public class AppCacheManager {


    private static String Key_Device = "Key_Device";



    private static ACache getCache() {

        return ACache.get(AppContext.getInstance());
    }


    public static DeviceBean getDevice() {

        DeviceBean device = (DeviceBean) AppCacheManager.getCache().getAsObject(Key_Device);

        if(device==null) {
            device=new DeviceBean();
            device.setDeviceId("");
            return device;
        }

        return device;

    }

    public static void setDevice(DeviceBean bean) {
        AppCacheManager.getCache().remove(Key_Device);
        AppCacheManager.getCache().put(Key_Device, bean);
    }


}
