package com.lumos.smartdevice.activity.booker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.BaseActivity;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerLoading;
import com.lumos.smartdevice.own.AppContext;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.TimerByActivityFinish;

import org.w3c.dom.Text;

public abstract class BookerBaseActivity  extends BaseActivity {
    private static final String TAG = "BookerBaseActivity";

    private DialogBookerLoading dialog_Loading;
    private Handler dialog_Loading_Handler;

    private TimerByActivityFinish timerByActivityFinish;


    private TextView tv_ActivityFinshTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dialog_Loading_Handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (msg.obj != null) {
                            dialog_Loading = new DialogBookerLoading((Context) msg.obj);
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

    public void setTimerByActivityFinish(long seconds){

        timerByActivityFinish=new TimerByActivityFinish(getAppContext(), seconds, new TimerByActivityFinish.OnTimerLinster() {
            @Override
            public void onTick(long second) {
                //LogUtil.d(TAG,String.valueOf(second));

                tv_ActivityFinshTick=findViewById(R.id.tv_ActivityFinshTick);

                if(tv_ActivityFinshTick!=null) {
                    tv_ActivityFinshTick.setVisibility(View.VISIBLE);
                    tv_ActivityFinshTick.setText(String.valueOf(second));
                }
            }

            @Override
            public void onFinsh() {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                getAppContext().startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onResume() {

        if(timerByActivityFinish!=null) {
            timerByActivityFinish.start();
        }

        super.onResume();
    }

    @Override
    protected void onStop() {

        if(timerByActivityFinish!=null) {
            timerByActivityFinish.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(timerByActivityFinish!=null) {
            timerByActivityFinish.cancel();
        }
        super.onPause();
    }

    @Override
    public void finish() {
        if(timerByActivityFinish!=null) {
            timerByActivityFinish.cancel();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {

        if(dialog_Loading!=null) {
            dialog_Loading.cancel();
        }

        if(timerByActivityFinish!=null) {
            timerByActivityFinish.cancel();
        }

        super.onDestroy();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                if(timerByActivityFinish!=null) {
                    timerByActivityFinish.start();
                }
                break;
            //否则其他动作计时取消
            default:
                if(timerByActivityFinish!=null) {
                    timerByActivityFinish.cancel();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
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
