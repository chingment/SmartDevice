package com.lumos.smartdevice.activity.sm.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lumos.smartdevice.R;


public class DialogSmLoading extends Dialog {
	private static final String TAG = "DialogSmLoading";
	public DialogSmLoading(Context context) {
		super(context, R.style.dialog);
		this.context = context;
		initDialog(context);
	}

	private ImageView ivProgress;
	private TextView tvInfo;
	private Context context;


	private void initDialog(Context context) {
		setContentView(R.layout.dialog_loading);
		ivProgress = (ImageView)findViewById(R.id.img);
		tvInfo = (TextView)findViewById(R.id.tipTextView);
		// 显示文本
		tvInfo.setText("正在加载...");
	}

	@Override
	public void show(){
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.dialog_load_animation);
	// 显示动画
		ivProgress.startAnimation(animation);
		super.show();
	}

	@Override
	public void cancel(){
		ivProgress.clearAnimation();
		super.cancel();
	}
	
	public void setTipsText(String text){
		tvInfo.setText(text);
	}

}
