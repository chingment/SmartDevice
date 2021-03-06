package com.lumos.smartdevice.activity.booker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerBaseActivity;
import com.lumos.smartdevice.ui.ViewHolder;

public class DialogBookerIdentityVerifyByIcCard extends Dialog {

    private static final String TAG = "DialogBookerIdentityVerifyByIcCard";
    private final Dialog mThis;
    private final BookerBaseActivity mContext;
    private final View mLayoutRes;

    private final View btn_Close;

    private final View btn_GoInspect;


    public DialogBookerIdentityVerifyByIcCard(Context context) {
        super(context, R.style.dialog);
        mThis = this;

        mThis.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        mContext = (BookerBaseActivity) context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.dialog_booker_identity_verify_by_iccard, null);
        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });


        btn_GoInspect = ViewHolder.get(mLayoutRes, R.id.btn_GoInspect);
        btn_GoInspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClickListener != null) {
                    onClickListener.test();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void test();
    }

}
