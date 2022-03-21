package com.lumos.smartdevice.activity.booker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerBaseActivity;
import com.lumos.smartdevice.ui.ViewHolder;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class DialogBookerLoading extends Dialog {
	private static final String TAG = "DialogBookerFlowHandling";
	private final Dialog mThis;
	private final BookerBaseActivity mContext;
	private final View mLayoutRes;

	private ImageView iv_TipsLoading;
	private TextView tv_TipsText;
	private Context context;


	public DialogBookerLoading(Context context) {
		super(context, R.style.dialog);
		mThis = this;
		mContext = (BookerBaseActivity) context;
		mLayoutRes = LayoutInflater.from(context).inflate(R.layout.dialog_booker_loading, null);

		tv_TipsText = ViewHolder.get(mLayoutRes, R.id.tv_TipsText);
		tv_TipsText.setText("正在加载...");

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

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(mLayoutRes);
	}

	@Override
	public void show(){
		super.show();
	}

	@Override
	public void cancel(){
		super.cancel();
	}

	public void setProgressText(String text) {
		tv_TipsText.setText(text);
	}

}
