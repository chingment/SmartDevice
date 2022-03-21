package com.lumos.smartdevice.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;


import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.DialogLoading;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.utils.UsbReaderUtil;
import com.lumos.smartdevice.utils.StringUtil;
import com.lumos.smartdevice.utils.ToastUtil;

/**
 * Created by chingment on 2017/8/23.
 */

public class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "BaseFragmentActivity";
    private AppContext appContext;
    public static boolean isForeground = false;
    private DeviceBean device;

    private Handler laodingUIHandler;
    private DialogLoading dialog_Loading;

    public AppContext getAppContext() {
        return appContext;
    }

    public DeviceBean getDevice() {

        if (device == null) {
            device = AppCacheManager.getDevice();
        }

        return device;
    }

    public UserBean getCurrentUser() {
        return AppCacheManager.getCurrentUser();
    }

    private UsbReaderUtil usbReaderUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            // Translucent status bar
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

        appContext = (AppContext) getApplication();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppManager.getAppManager().addActivity(this);


        laodingUIHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if(msg.obj!=null) {
                            dialog_Loading = new DialogLoading((Context) msg.obj);
                            dialog_Loading.setProgressText("正在处理中");
                            dialog_Loading.show();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (dialog_Loading != null) {
                                        if (dialog_Loading.isShowing()) {
                                            laodingUIHandler.sendEmptyMessage(2);
                                        }
                                    }
                                }
                            }, 6000);
                        }
                        break;
                    case 2:
                        if (dialog_Loading != null) {
                            dialog_Loading.cancel();
                            dialog_Loading=null;
                        }
                        break;
                }
                return false;
            }
        });


        usbReaderUtil = new UsbReaderUtil();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        AppManager.getAppManager().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isForeground = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        usbReaderUtil.removeListener();
        usbReaderUtil = null;

        AppManager.getAppManager().finishActivity(this);

    }


    public void openActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
    }

    @Override
    public void onClick(View v) {

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (UsbReaderUtil.isInputFromReader(this, event)) {
            if (usbReaderUtil != null){
                usbReaderUtil.resolveKeyEvent(event);
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public void setNavHeaderTtile(int id) {
        setNavHeaderTtile(this.getResources().getString(id));
    }

    public void setNavHeaderTtile(String title) {
        TextView tv = findViewById(R.id.tv_Nav_Header_Title);
        if (tv != null) {
            tv.setText(title);
        }
    }

    public void setNavHeaderBtnByGoBackIsVisible(boolean isVisible) {
        View view = findViewById(R.id.btn_Nav_Header_Goback);
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void setNavHeaderBtnByRightIsVisible(boolean isVisible) {
        View view = findViewById(R.id.btn_Nav_Header_Right);
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void showToast(String txt) {
        if (!StringUtil.isEmpty(txt)) {
            ToastUtil.showMessage(BaseFragmentActivity.this, txt, Toast.LENGTH_LONG);
        }
    }

    public void showToast(int id) {
        ToastUtil.showMessage(BaseFragmentActivity.this, getResources().getString(id), Toast.LENGTH_LONG);
    }

    public void setHideSysStatusBar(boolean ishidden) {
        OstCtrlInterface.getInstance().setHideStatusBar(appContext, ishidden);
    }

    public void showLoading(Context context) {
        Message m = new Message();
        m.what = 1;
        m.obj = context;
        laodingUIHandler.sendMessage(m);
    }

    public void hideLoading(Context context) {
        Message m = new Message();
        m.what = 2;
        m.obj = context;
        laodingUIHandler.sendMessage(m);
    }

    public void setUsbReaderListener(UsbReaderUtil.OnListener onListener) {
        usbReaderUtil.setListener(onListener);
    }

}
