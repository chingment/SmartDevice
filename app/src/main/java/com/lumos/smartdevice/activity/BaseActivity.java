package com.lumos.smartdevice.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.DeviceVo;
import com.lumos.smartdevice.model.UserVo;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.utils.StringUtil;
import com.lumos.smartdevice.utils.ToastUtil;
import com.lumos.smartdevice.utils.UsbReaderUtil;

/**
 * Created by chingment on 2017/8/23.
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";
    private AppContext appContext;
    public static boolean isForeground = false;
    private DeviceVo device;
    private UsbReaderUtil usbReaderUtil;

    public AppContext getAppContext() {
        return appContext;
    }

    public DeviceVo getDevice() {
        if (device == null) {
            device = AppCacheManager.getDevice();
        }
        return device;
    }

    public UserVo getCurrentUser() {
        return AppCacheManager.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = (AppContext) getApplication();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppManager.getAppManager().addActivity(this);


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
        AppManager.getAppManager().setCurrentActivity(this);
        super.onResume();
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

        usbReaderUtil.removeListener();
        usbReaderUtil = null;

        AppManager.getAppManager().finishActivity(this);

        super.onDestroy();

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
            ToastUtil.showMessage(BaseActivity.this, txt, Toast.LENGTH_LONG);
        }
    }

    public void showToast(int id) {
        ToastUtil.showMessage(BaseActivity.this, getResources().getString(id), Toast.LENGTH_LONG);
    }

    public void setHideSysStatusBar(boolean ishidden) {
        OstCtrlInterface.getInstance().setHideStatusBar(appContext, ishidden);
    }


    public void setUsbReaderListener(UsbReaderUtil.OnListener onListener) {
        usbReaderUtil.setListener(onListener);
    }

    public abstract void hideLoading(Context context);
    public abstract void showLoading(Context context);

}
