package com.lumos.smartdevice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.ViewHolder;


/**
 * <p>
 * Title: CustomDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * @author archie
 * @version 1.0
 */
public class CustomDialogConfirm extends Dialog {
    private static final String TAG = "CustomDialogConfirm";
    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private Button btn_Sure;
    private LinearLayout lyt_Sure;
    private Button btn_Cancle;
    private LinearLayout lyt_Cancle;
    private LinearLayout lyt_Buttons;
    private View btn_Close;
    private TextView txt_Tips;
    private ImageView img_Tips;

    public Button getBtnSure() {
        return this.btn_Sure;
    }

    public Button getBtnCancle() {
        return this.btn_Cancle;
    }

    public TextView getTipsText() {
        return this.txt_Tips;
    }

    public ImageView getTipsImage() {
        return this.img_Tips;
    }

    public LinearLayout getBtnArea() {
        return this.lyt_Buttons;
    }

    public CustomDialogConfirm(Context context, String tips, boolean isCancle) {
        super(context, R.style.custom_dialog_confirm);

        mThis=this;

        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_confirm, null);

        txt_Tips = ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Tips_Txt);
        img_Tips = ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Tips_Img);
        btn_Sure = ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Btn_Sure);
        lyt_Sure=ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Lyt_Sure);
        btn_Cancle = ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Btn_Cancle);
        lyt_Cancle= ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Lyt_Cancle);
        btn_Close =ViewHolder.get(mLayoutRes,R.id.dialog_Btn_Close);
        lyt_Buttons=ViewHolder.get(mLayoutRes,R.id.dialog_Confirm_Lyt_Buttons);
        txt_Tips.setText(tips);

        if (!isCancle) {
            btn_Cancle.setVisibility(View.GONE);
        }

        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });
    }

    public void setBtnCloseVisibility(int visibility) {
        btn_Close.setVisibility(visibility);
        lyt_Cancle.setVisibility(visibility);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (backListener != null) {
            backListener.onBackClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public OnDialogBackKeyDown backListener;

    public void setOnBackListener(OnDialogBackKeyDown backListener) {
        this.backListener = backListener;
    }

    public interface OnDialogBackKeyDown {
        void onBackClick();
    }

}