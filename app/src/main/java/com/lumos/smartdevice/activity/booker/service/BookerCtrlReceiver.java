package com.lumos.smartdevice.activity.booker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lumos.smartdevice.utils.LogUtil;

public class BookerCtrlReceiver extends BroadcastReceiver {

    private static final String TAG = "BookerCtrlReceiver";

    private final OnListener onListener;

    public BookerCtrlReceiver(OnListener onListener) {

        this.onListener = onListener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        LogUtil.d(TAG,"onReceive");

        BorrowReturnFlowResult flowResult =(BorrowReturnFlowResult)intent.getSerializableExtra("result");

        onListener.onBorrowReturnFlowReceive(flowResult);

    }

    public  interface OnListener{
        void onBorrowReturnFlowReceive(BorrowReturnFlowResult flowResult);
    }

    public void  register(final Context context) {
        LogUtil.d(TAG,"register");
        IntentFilter filter = new IntentFilter();
        String ACTION = "action.booker.borrow.return.flow.result";
        filter.addAction(ACTION);
        context.registerReceiver(this, filter);
    }

    public void unRegister(final Context context) {
        LogUtil.d(TAG,"unRegister");
        context.unregisterReceiver(this);
    }
}
