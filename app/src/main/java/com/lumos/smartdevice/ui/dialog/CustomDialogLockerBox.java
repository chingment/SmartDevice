package com.lumos.smartdevice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogLockerBox extends Dialog {

    private static final String TAG = "CustomDialogLockerBox";
    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private View btn_Close;

    private TextView tv_BoxName;

    private TextView btn_SelectUser;
    public CustomDialogLockerBox(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_lockerbox, null);

        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

        tv_BoxName = ViewHolder.get(mLayoutRes, R.id.tv_BoxName);
        btn_SelectUser = ViewHolder.get(mLayoutRes, R.id.btn_SelectUser);
        btn_SelectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener!=null) {
                    onClickListener.onSelectUser();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public void setLockerBox(CabinetBean cabinet,String boxId) {
        String[] box_Prams=boxId.split("-");
        
        tv_BoxName.setText(box_Prams[2]);

    }

    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void onSelectUser();
    }


}
