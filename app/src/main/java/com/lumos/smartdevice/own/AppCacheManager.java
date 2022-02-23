package com.lumos.smartdevice.own;


import com.lumos.smartdevice.model.BookerCustomDataBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.utils.ACache;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by chingment on 2018/1/16.
 */

public class AppCacheManager {

    private static String Key_Device = "Key_Device";
    private static String Key_LastUserName="Key_LastUserName";
    private static String Key_CurrentUser="Key_CurrentUser";
    private static String Key_BookerCustomData="Key_BookerCustomData";

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

    public static void setLastUserName(String scene, String userName) {
        if(!StringUtil.isEmptyNotNull(userName)) {
            AppCacheManager.getCache().put(Key_LastUserName+"_"+scene, userName);
        }
    }

    public static String getLastUserName(String scene) {

        String userName = AppCacheManager.getCache().getAsString(Key_LastUserName+"_"+scene);

        if(StringUtil.isEmptyNotNull(userName))
        {
            userName="";
        }

        return userName;

    }

    public static void setCurrentUser(UserBean user) {
        if(user!=null) {
            AppCacheManager.getCache().put(Key_CurrentUser, user);
        }
    }

    public static UserBean getCurrentUser() {

        UserBean user = (UserBean)AppCacheManager.getCache().getAsObject(Key_CurrentUser);

        return user;

    }

    public static void setBookerCustomData(BookerCustomDataBean bean) {
        AppCacheManager.getCache().remove(Key_BookerCustomData);
        AppCacheManager.getCache().put(Key_BookerCustomData, bean);
    }

}
