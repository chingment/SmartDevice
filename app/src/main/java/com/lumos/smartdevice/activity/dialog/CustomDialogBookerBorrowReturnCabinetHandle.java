package com.lumos.smartdevice.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogBookerBorrowReturnCabinetHandle extends Dialog {

    private static final String TAG = "CustomDialogBookerBorrowReturnCabinetHandle";
    private final Dialog mThis;
    private final BaseFragmentActivity mContext;
    private final View mLayoutRes;

    private final View btn_Close;

    public CustomDialogBookerBorrowReturnCabinetHandle(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = (BaseFragmentActivity)context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_borrow_return_cabinet_handle, null);

        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }
}
