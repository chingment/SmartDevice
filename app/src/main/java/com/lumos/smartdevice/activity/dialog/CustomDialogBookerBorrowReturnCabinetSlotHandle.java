package com.lumos.smartdevice.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetSlotBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogBookerBorrowReturnCabinetSlotHandle extends Dialog {

    private static final String TAG = "CustomDialogBookerBorrowReturnCabinetSlotHandle";
    private final Dialog mThis;
    private final BaseFragmentActivity mContext;
    private final View mLayoutRes;

    private String flowId;
    private CabinetSlotBean cabinetSlot;

    public CabinetSlotBean getCabinetSlot() {
        return cabinetSlot;
    }

    public void setCabinetSlot(CabinetSlotBean cabinetSlot) {
        this.cabinetSlot = cabinetSlot;
    }

    public CustomDialogBookerBorrowReturnCabinetSlotHandle(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = (BaseFragmentActivity) context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_booker_borrow_return_cabinet_slot_handle, null);

        View iv_TipsImage = ViewHolder.get(mLayoutRes, R.id.iv_TipsImage);
        View tv_TipsText = ViewHolder.get(mLayoutRes, R.id.tv_TipsText);

        iv_TipsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, BookerBorrowReturnOverviewActivity.class);
//                mContext.startActivity(intent);
//                mContext.finish();

                if(onClickListener!=null) {
                    onClickListener.testOpen(flowId);
                }

            }
        });

        tv_TipsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onClickListener!=null) {
                    onClickListener.testClose(flowId);
                }

            }
        });

    }


    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
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
        void testOpen(String flowId);
        void testClose(String flowId);
    }

}
