package com.lumos.smartdevice.own;


import com.lumos.smartdevice.model.BookerCustomDataVo;
import com.lumos.smartdevice.model.DeviceVo;
import com.lumos.smartdevice.model.UserVo;
import com.lumos.smartdevice.utils.ACache;
import com.lumos.smartdevice.utils.StringUtil;

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

    public static DeviceVo getDevice() {

        DeviceVo device = (DeviceVo) AppCacheManager.getCache().getAsObject(Key_Device);

        if(device==null) {
            device=new DeviceVo();
            device.setDeviceId("");
            return device;
        }

        return device;

    }

    public static void setDevice(DeviceVo bean) {
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

    public static void setCurrentUser(UserVo user) {
        if(user!=null) {
            AppCacheManager.getCache().put(Key_CurrentUser, user);
        }
    }

    public static UserVo getCurrentUser() {

        UserVo user = (UserVo)AppCacheManager.getCache().getAsObject(Key_CurrentUser);

        return user;

    }

    public static void setBookerCustomData(BookerCustomDataVo bean) {
        AppCacheManager.getCache().remove(Key_BookerCustomData);
        AppCacheManager.getCache().put(Key_BookerCustomData, bean);
    }

    public static BookerCustomDataVo getBookerCustomData() {

        BookerCustomDataVo bean = (BookerCustomDataVo)AppCacheManager.getCache().getAsObject(Key_BookerCustomData);

        return bean;

    }

}
