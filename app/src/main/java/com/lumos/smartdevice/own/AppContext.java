package com.lumos.smartdevice.own;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by chingment on 2017/8/23.
 */

public class AppContext extends Application {
    private static final String TAG = "AppContext";
    private static AppContext app;

    public AppContext() {
        app = this;
    }

    public static synchronized AppContext getInstance() {
        if (app == null) {
            app = new AppContext();
        }
        return app;
    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        DbManager.getInstance().initConfig();
        DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,"0");
        DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE,"0");
       // DbManager.getInstance().getConfig("scene_mode");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
