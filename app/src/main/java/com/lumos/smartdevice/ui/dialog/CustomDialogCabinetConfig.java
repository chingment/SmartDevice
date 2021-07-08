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
import com.lumos.smartdevice.model.CabinetBean;
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
        super(context, R.style.custom_dialog_confirm);
        mThis=this;
        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_cabinetconfig, null);

        btn_Close =ViewHolder.get(mLayoutRes,R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

        tv_CabinetId =ViewHolder.get(mLayoutRes,R.id.tv_CabinetId);
        tv_CabinetName =ViewHolder.get(mLayoutRes,R.id.tv_CabinetName);
        tv_ComId =ViewHolder.get(mLayoutRes,R.id.tv_ComId);
        tv_ComBaud =ViewHolder.get(mLayoutRes,R.id.tv_ComBaud);
        tv_ComPrl =ViewHolder.get(mLayoutRes,R.id.tv_ComPrl);
        tv_Layout =ViewHolder.get(mLayoutRes,R.id.tv_Layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public void setCofing(CabinetBean cabinet){
        tv_CabinetId.setText(cabinet.getCabinetId());
        tv_CabinetName.setText(cabinet.getName());
        tv_ComId.setText(cabinet.getComId());
        tv_ComBaud.setText(cabinet.getComBaud()+"");
        tv_ComPrl.setText(cabinet.getComPrl());
        tv_Layout.setText(cabinet.getLayout());
    }

}
