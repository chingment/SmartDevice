package com.lumos.smartdevice.utils;


import android.content.Context;
import android.os.CountDownTimer;


public class TimerByActivityFinish extends CountDownTimer {

    private Context context;

    /**
     * 参数 millisInFuture       倒计时总时间（如60S，120s等）
     * 参数 countDownInterval    渐变时间（每次倒计1s）
     */
    public TimerByActivityFinish(Context context, long seconds) {
        super(seconds*1000, 1000);
        this.context=context;
    }

    public TimerByActivityFinish(Context context, long seconds, OnTimerLinster onTimerLinster) {
        super(seconds*1000, 1000);
        this.context=context;
        this.onTimerLinster=onTimerLinster;
    }


    // 计时完毕时触发
    @Override
    public void onFinish() {
        if(onTimerLinster!=null) {
            onTimerLinster.onFinsh();
        }
    }

    // 计时过程显示
    @Override
    public void onTick(long millisUntilFinished) {
        long second = (millisUntilFinished / 1000);
        if(onTimerLinster!=null) {
            onTimerLinster.onTick(second);
        }
        //LogUtil.i("主页倒计时:" + seconds);
    }


    public OnTimerLinster onTimerLinster = null;

    public interface OnTimerLinster {
        void onTick(long second);
        void onFinsh();
    }

}