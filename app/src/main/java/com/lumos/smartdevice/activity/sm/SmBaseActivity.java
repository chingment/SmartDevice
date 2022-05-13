package com.lumos.smartdevice.activity.sm;

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
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.activity.booker.BookerMainActivity;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmLoading;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.TimerByActivityFinish;

public class SmBaseActivity extends BaseActivity  {
    private static final String TAG = "SmBaseActivity";

    private DialogSmLoading dialog_Loading;
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

                            int seconds=msg.getData().getInt("seconds",6);

                            dialog_Loading = new DialogSmLoading((Context) msg.obj);
                            dialog_Loading.setTipsText("正在处理中");
                            dialog_Loading.show();

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (dialog_Loading != null) {
                                        if (dialog_Loading.isShowing()) {
                                            dialog_Loading_Handler.sendEmptyMessage(2);
                                        }
                                    }
                                }
                            }, seconds*1000);


                        }
                        break;
                    case 2:
                        if (dialog_Loading != null) {
                            dialog_Loading.cancel();
                            dialog_Loading = null;
                        }
                        break;
                    case  3:
                        if (dialog_Loading != null) {
                            String tips=msg.getData().getString("tips");
                            dialog_Loading.setTipsText(tips);
                        }
                        break;
                }
                return false;
            }
        });


        setTimerByActivityFinish(120);

    }

    public void setTimerByActivityFinish(long seconds){


        timerByActivityFinish=new TimerByActivityFinish(getAppContext(), seconds, new TimerByActivityFinish.OnTimerLinster() {
            @Override
            public void onTick(long second) {
                //LogUtil.d(TAG,String.valueOf(second));

                tv_ActivityFinshTick=findViewById(R.id.tv_ActivityFinshTick);

                if(tv_ActivityFinshTick!=null) {
                    tv_ActivityFinshTick.setText(String.valueOf(second));
                    tv_ActivityFinshTick.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinsh() {
                Intent intent = new Intent(getAppContext(), InitDataActivity.class);
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

    public void setTimerPauseByActivityFinish(){
        if(timerByActivityFinish!=null) {
            timerByActivityFinish.cancel();
        }
    }


    public void showLoading(Context context) {
        showLoading(context,6);
    }

    public void showLoading(Context context,int delaySeconds) {
        Message m = new Message();
        m.what = 1;
        m.obj = context;
        Bundle bundle=new Bundle();
        bundle.putInt("seconds",delaySeconds);
        m.setData(bundle);
        dialog_Loading_Handler.sendMessage(m);
    }

    public void setLoadingTips(Context context,String tips) {
        Message m = new Message();
        m.what = 3;
        m.obj = context;
        Bundle bundle = new Bundle();
        bundle.putString("tips", tips);
        m.setData(bundle);
        dialog_Loading_Handler.sendMessage(m);
    }

    public void hideLoading(Context context) {
        Message m = new Message();
        m.what = 2;
        m.obj = context;
        dialog_Loading_Handler.sendMessage(m);
    }

}
