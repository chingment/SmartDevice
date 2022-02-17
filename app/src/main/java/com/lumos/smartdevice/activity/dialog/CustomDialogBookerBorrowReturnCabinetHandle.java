package com.lumos.smartdevice.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.scenebooker.BookerBorrowReturnOverviewActivity;
import com.lumos.smartdevice.activity.scenebooker.BookerMainActivity;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogBookerBorrowReturnCabinetHandle extends Dialog {

    private static final String TAG = "CustomDialogBookerBorrowReturnCabinetHandle";
    private final Dialog mThis;
    private final BaseFragmentActivity mContext;
    private final View mLayoutRes;

    public CustomDialogBookerBorrowReturnCabinetHandle(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = (BaseFragmentActivity) context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_borrow_return_cabinet_handle, null);

        View iv_TipsImage = ViewHolder.get(mLayoutRes, R.id.iv_TipsImage);

        iv_TipsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookerBorrowReturnOverviewActivity.class);
                mContext.startActivity(intent);
                mContext.finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }
}
