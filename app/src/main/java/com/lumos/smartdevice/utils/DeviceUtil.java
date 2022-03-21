package com.lumos.smartdevice.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeviceUtil {

    public static String getDeviceId() {
        //todo 获取方式必须跟statinterface里获取的设备号一致

        String deviceId= "";

        try {

            deviceId=getImeiId();

            if(StringUtil.isEmptyNotNull(deviceId))
            {
                deviceId=getMacAddr();
            }
        }
        catch (Exception ex)
        {
            deviceId="";
        }


        return  "202012110204";

//        if(Config.IS_BUILD_DEBUG) {
//            return "202012110204;
//        }
//
//        return Build.SERIAL;
    }

    public static String getImeiId() {
        String imeiId = "";
        try {
            TelephonyManager tm = (TelephonyManager) AppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            if(tm!=null) {
                imeiId = tm.getDeviceId();
            }
        } catch (Exception ex) {
            imeiId="";
        }

        return imeiId;
    }


    public static String getMacAddr() {
//        String mac = "02:00:00:00:00:00";
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            mac = getMacDefault(app.getApplicationContext());
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            mac = getMacFromFile();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        String    mac = getMacFromHardware();
        // }
        return mac;
    }

    private static String getMacFromFile() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    private static String getMacFromHardware() {

        if(Config.IS_BUILD_DEBUG) {
            return "02:00:00:00:00:00";
        }

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    public static String getCtrlVerName(){
        return "";
    }
}
