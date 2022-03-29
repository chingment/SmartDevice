package com.lumos.smartdevice.activity.booker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.HashMap;

public class BookerBorrowReturnFlowReceiver extends BroadcastReceiver {

    private static String TAG = "BookerBorrowReturnFlowReceiver";

    private final OnListener onListener;

    public BookerBorrowReturnFlowReceiver(OnListener onListener) {

        this.onListener = onListener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        LogUtil.d(TAG,"BookerBorrowReturnFlow");

        int actionCode=intent.getIntExtra("actionCode",0);
        HashMap<String, Object> actionData =( HashMap<String, Object> )intent.getSerializableExtra("actionData");
        String actionRemark= intent.getStringExtra("actionRemark");

        onListener.onReceive(actionCode,actionData,actionRemark);

    }

    public  interface OnListener{
        void onReceive(int actionCode, HashMap<String, Object> actionData, String actionRemark);
    }

    public void  register(final Context context) {
        IntentFilter filter = new IntentFilter();
        String ACTION = "action.booker.borrow.return.flow";
        filter.addAction(ACTION);
        context.registerReceiver(this, filter);
    }

    public void unRegister(final Context context) {
        context.unregisterReceiver(this);
    }
}
