package com.lumos.smartdevice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lumos.smartdevice.utils.LogUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.v(TAG, "开机自动服务自动启动.....");
    }

}
