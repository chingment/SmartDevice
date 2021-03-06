package com.lumos.smartdevice.activity.booker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.BaseActivity;
import com.lumos.smartdevice.activity.booker.BookerBaseActivity;
import com.lumos.smartdevice.ui.ViewHolder;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DialogBookerFlowHandling extends Dialog {

    private static final String TAG = "DialogBookerFlowHandling";
    private final Dialog mThis;
    private final BaseActivity mContext;
    private final View mLayoutRes;


    private TextView tv_TipsText;
    private View btn_TryAgainOpen;
    private View btn_Cancle;
    private TextView tv_CancleCountDownSeconds;

    private OnClickListener onClickListener;

    private CountDownTimer cancleCountDownTimer;

    public DialogBookerFlowHandling(Context context) {
        super(context, R.style.dialog);
        mThis = this;
        mContext = (BaseActivity) context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.dialog_booker_flow_handling, null);



        GifImageView iv_TipsLoading = ViewHolder.get(mLayoutRes, R.id.iv_TipsLoading);
        iv_TipsLoading.setImageResource(R.drawable.test2);
        iv_TipsLoading.setScaleType(ImageView.ScaleType.FIT_XY);
        GifDrawable gifDrawable = (GifDrawable) iv_TipsLoading.getDrawable();
        gifDrawable.start(); //开始播放
//        gifDrawable.stop(); //停止播放
//        gifDrawable.reset(); //复位，重新开始播放
//        gifDrawable.isRunning(); //是否正在播放
        gifDrawable.setLoopCount(60000); //设置播放的次数，播放完了就自动停止
//        gifDrawable.getCurrentLoop();  //获取正在播放的次数
//        gifDrawable.getCurrentPosition(); //获取现在到从开始播放所经历的时间
//        gifDrawable.getDuration() ; //获取播放一次所需要的时间


        tv_TipsText = ViewHolder.get(mLayoutRes, R.id.tv_TipsText);
        btn_TryAgainOpen = ViewHolder.get(mLayoutRes, R.id.btn_TryAgainOpen);
        btn_Cancle = ViewHolder.get(mLayoutRes, R.id.btn_Cancle);
        tv_CancleCountDownSeconds=ViewHolder.get(mLayoutRes, R.id.tv_CancleCountDownSeconds);

        iv_TipsLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        tv_TipsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        btn_TryAgainOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onClickListener!=null){
                    onClickListener.onTryAgainOpen();
                }


            }
        });

        btn_Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null) {
                    onClickListener.onCancle();
                }
            }
        });

        cancleCountDownTimer= new CountDownTimer(60*1000,1000) {

            @Override

            public void onTick(long millisUntilFinished) {//倒计时的过程
                tv_CancleCountDownSeconds.setText(millisUntilFinished / 1000 + "秒");
            }

            @Override

            public void onFinish() {//倒计时结束
                if(onClickListener!=null){
                    onClickListener.onCancle();
                }
            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

//    @Override
//    public void show() {
//        tv_CancleCountDownSeconds.setText("");
//        super.show();
//    }

    @Override
    public void cancel(){

        if(cancleCountDownTimer!=null){
            cancleCountDownTimer.cancel();
        }

        super.cancel();
    }

    public void setTipsText(String tips) {
        tv_TipsText.setText(tips);
    }

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public void startCancleCountDownTimer(){
        if(cancleCountDownTimer!=null){
            cancleCountDownTimer.start();
        }
    }

    public void stopCancleCountDownTimer(){
        if(cancleCountDownTimer!=null){
            cancleCountDownTimer.cancel();
        }
    }

    public  interface OnClickListener {
        void onTryAgainOpen();
        void onCancle();
    }


}
