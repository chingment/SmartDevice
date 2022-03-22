package com.lumos.smartdevice.activity.sm;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lumos.smartdevice.activity.BaseActivity;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmLoading;

public class SmBaseActivity extends BaseActivity {


    private DialogSmLoading dialog_Loading;
    private Handler dialog_Loading_Handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog_Loading_Handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (msg.obj != null) {
                            dialog_Loading = new DialogSmLoading((Context) msg.obj);
                            dialog_Loading.setProgressText("正在处理中");
                            dialog_Loading.show();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (dialog_Loading != null) {
                                        if (dialog_Loading.isShowing()) {
                                            dialog_Loading_Handler.sendEmptyMessage(2);
                                        }
                                    }
                                }
                            }, 6000);
                        }
                        break;
                    case 2:
                        if (dialog_Loading != null) {
                            dialog_Loading.cancel();
                            dialog_Loading = null;
                        }
                        break;
                }
                return false;
            }
        });


    }


    @Override
    protected void onDestroy() {

        if(dialog_Loading!=null) {
            dialog_Loading.cancel();
        }

        super.onDestroy();

    }


    public void showLoading(Context context) {
        Message m = new Message();
        m.what = 1;
        m.obj = context;
        dialog_Loading_Handler.sendMessage(m);
    }

    public void hideLoading(Context context) {
        Message m = new Message();
        m.what = 2;
        m.obj = context;
        dialog_Loading_Handler.sendMessage(m);
    }
}
