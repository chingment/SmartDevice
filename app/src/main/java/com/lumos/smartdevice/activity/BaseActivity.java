package com.lumos.smartdevice.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.sm.SmHomeActivity;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.UserVo;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.app.AppContext;
import com.lumos.smartdevice.app.AppManager;
import com.lumos.smartdevice.receiver.UpdateAppServiceReceiver;
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

    private TextToSpeech textToSpeech = null;

    private UpdateAppServiceReceiver updateAppServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = (AppContext) getApplication();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppManager.getAppManager().addActivity(this);


        usbReaderUtil = new UsbReaderUtil();
//
//        //???????????????????????????
//        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    // Toast.makeText(MainActivity.this,"??????????????????",
//                    // Toast.LENGTH_SHORT).show();
//                    // Locale loc1=new Locale("us");
//                    // Locale loc2=new Locale("china");
//
//                    textToSpeech.setPitch(1.0f);//????????????????????????
//                    textToSpeech.setSpeechRate(1.0f);//??????????????????
//
//                    //????????????????????????????????????
//                    int result1 = textToSpeech.setLanguage(Locale.US);
//                    int result2 = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
//                    boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
//                    boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);
//
//                    LogUtil.i(TAG,"US????????????--???" + a + "???zh-CN????????????--???" + b);
//
//                }
//
//            }
//        });


        updateAppServiceReceiver= new UpdateAppServiceReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", 0);
                String message = intent.getStringExtra("message");

                if (status == 1) {
                    showLoading(BaseActivity.this);
                } else if (status == 2) {
                    hideLoading(BaseActivity.this);
                    showToast(message);
                } else if (status == 3) {
                    setLoadingTips(BaseActivity.this,"???????????????.....");
                    //setTimerPauseByActivityFinish();
                }
            }
        };


        registerReceiver(updateAppServiceReceiver, new IntentFilter("action.smartdevice.app.update"));

    }

    public void toSpeech(String data) {
        if(textToSpeech!=null) {
            // ???????????????????????????????????????????????????????????????????????????,1.0?????????
            textToSpeech.setPitch(1f);
            // ????????????
            textToSpeech.setSpeechRate(1.4f);
            textToSpeech.speak(data,//??????????????????????????????????????????????????????
                    TextToSpeech.QUEUE_FLUSH, null);
        }

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

        if(usbReaderUtil!=null) {
            usbReaderUtil.removeListener();
            usbReaderUtil = null;
        }

        if(textToSpeech!=null) {
            textToSpeech.stop(); // ????????????????????????TTS????????????
            textToSpeech.shutdown(); // ?????????????????????
        }

        if(updateAppServiceReceiver!=null) {
            unregisterReceiver(updateAppServiceReceiver);
        }

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
        if(usbReaderUtil!=null) {
            usbReaderUtil.setListener(onListener);
        }
    }

    public abstract void hideLoading(Context context);
    public abstract void showLoading(Context context);
    public abstract void setLoadingTips(Context context,String tips);

}
