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

        MessageByAction message =(MessageByAction)intent.getSerializableExtra("message");

        onListener.handleMessageByBorrowReturn(message);

    }

    public  interface OnListener{
        void handleMessageByBorrowReturn(MessageByAction flowResult);
    }

    public void  register(Context context) {
        LogUtil.d(TAG,"register");
        IntentFilter filter = new IntentFilter();
        String ACTION = "action.booker.borrow.return.handle.message";
        filter.addAction(ACTION);
        context.registerReceiver(this, filter);
    }

    public void unRegister(Context context) {
        LogUtil.d(TAG,"unRegister");
        context.unregisterReceiver(this);
    }
}
