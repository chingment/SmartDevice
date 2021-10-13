package com.lumos.smartdevice.own;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;


import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        DbManager.getInstance().init();
        OstCtrlInterface.init(context);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
