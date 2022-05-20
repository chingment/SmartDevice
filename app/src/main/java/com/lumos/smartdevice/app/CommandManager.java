package com.lumos.smartdevice.app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.activity.sm.SmHomeActivity;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.service.UpdateAppService;
import com.lumos.smartdevice.utils.LogUtil;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chingment on 2019/3/20.
 */

public class CommandManager {

    private static Lock lock = new ReentrantLock();

    private static final String TAG = "CommandManager";

    public static void Execute(String id,String method, String params) {

        lock.lock();// 得到锁

        LogUtil.i(TAG, "id:" + id + ",method:" + method + ",params:" + params);

        try {
            switch (method) {
                case "reboot_sys":
                    reboot_sys();
                    break;
                case "shutdown_sys":
                    shutdown_sys();
                    break;
                case "update_app":
                    update_app();
                    break;
                case "logcat":
                    //logcat(params);
                    break;
            }
        } finally {
            lock.unlock();// 释放锁
        }
    }

    private static void reboot_sys() {
        OstCtrlInterface.getInstance().reboot(AppContext.getInstance().getApplicationContext());
    }

    private static void shutdown_sys() {
        OstCtrlInterface.getInstance().shutdown(AppContext.getInstance().getApplicationContext());
    }

    private static void update_app() {

        if (!AppUtil.deviceIsIdle()) {
            return;
        }

        Intent updateAppService= new Intent(AppContext.getInstance().getApplicationContext(), UpdateAppService.class);
        AppContext.getInstance().startService(updateAppService);

    }

}
