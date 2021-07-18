package com.lumos.smartdevice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogCabinetConfig extends Dialog {
    private static final String TAG = "CustomDialogCabinetConfig";
    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private View btn_Close;

    private TextView tv_CabinetId;
    private TextView tv_CabinetName;
    private TextView tv_ComId;
    private TextView tv_ComBaud;
    private TextView tv_ComPrl;
    private TextView tv_Layout;

    public CustomDialogCabinetConfig(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_cabinetconfig, null);

        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

        tv_CabinetId = ViewHolder.get(mLayoutRes, R.id.tv_CabinetId);
        tv_CabinetName = ViewHolder.get(mLayoutRes, R.id.tv_CabinetName);
        tv_ComId = ViewHolder.get(mLayoutRes, R.id.tv_ComId);
        tv_ComBaud = ViewHolder.get(mLayoutRes, R.id.tv_ComBaud);
        tv_ComPrl = ViewHolder.get(mLayoutRes, R.id.tv_ComPrl);
        tv_Layout = ViewHolder.get(mLayoutRes, R.id.tv_Layout);
        tv_Layout.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public void setCofing(CabinetBean cabinet) {
        tv_CabinetId.setText(cabinet.getCabinetId());
        tv_CabinetName.setText(cabinet.getName());
        tv_ComId.setText(cabinet.getComId());
        tv_ComBaud.setText(cabinet.getComBaud() + "");
        tv_ComPrl.setText(cabinet.getComPrl());
        String layout=prettyJson(cabinet.getLayout()) ;
        tv_Layout.setText(layout);
    }

    private String prettyJson(String json) {


        JSONArray jsonObject = null;
        try {
            jsonObject = JSONObject.parseArray(json);
        } catch (Exception ex) {
        }

         return JSONArray.toJSONString(jsonObject,true);
    }

}