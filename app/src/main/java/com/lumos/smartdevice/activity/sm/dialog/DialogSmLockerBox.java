package com.lumos.smartdevice.activity.sm.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmLockerBoxUsageAdapter;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.LockerBoxBean;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.ui.my.MyListView;

import java.util.List;

public class DialogSmLockerBox extends Dialog {

    private static final String TAG = "DialogSmLockerBox";

    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private View btn_Close;

    private TextView tv_Name;

    private LinearLayout ll_OpenStatus;

    private TextView btn_DistUser;
    private TextView btn_OpenBox;
    private MyListView lv_Usages;
    private DeviceBean device;
    private String cabinetId;
    private String slotId;
    private LinearLayout ll_UsagesEmpty;


    public DialogSmLockerBox(Context context) {
        super(context, R.style.dialog);
        mThis = this;
        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.dialog_sm_locker_box, null);


        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

        tv_Name = ViewHolder.get(mLayoutRes, R.id.tv_Name);
        ll_OpenStatus = ViewHolder.get(mLayoutRes, R.id.ll_OpenStatus);


        lv_Usages = ViewHolder.get(mLayoutRes, R.id.lv_Usages);

        lv_Usages.setFocusable(false);
        lv_Usages.setClickable(false);
        lv_Usages.setPressed(false);
        lv_Usages.setEnabled(false);
        ll_UsagesEmpty= ViewHolder.get(mLayoutRes, R.id.ll_UsagesEmpty);
        btn_DistUser = ViewHolder.get(mLayoutRes, R.id.btn_DistUser);
        btn_DistUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onGoSelectUser(device.getDeviceId(), cabinetId, slotId);
                }
            }
        });
        btn_OpenBox = ViewHolder.get(mLayoutRes, R.id.btn_OpenBox);
        btn_OpenBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onOpenBox(device.getDeviceId(), cabinetId, slotId);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public String getDeviceId() {
        return device == null ? null : device.getDeviceId();
    }

    public String getCabinetId(){
        return cabinetId;
    }

    public String getSlotId(){
        return slotId;
    }

    public void setConfig(DeviceBean device, CabinetBean cabinet, LockerBoxBean lockerBox) {

        this.device = device;
        this.cabinetId = cabinet.getCabinetId();
        this.slotId = lockerBox.getSlotId();

        String[] box_Prams = slotId.split("-");

        tv_Name.setText(box_Prams[2]);

    }

    public void loadData(){

    }

    public void refeshData(){

    }

    public void setLockerBox(LockerBoxBean lockerBox) {

        //String isUsed=lockerBox.isUsed();

        List<LockerBoxUsageBean> usages=lockerBox.getUsages();

        if (lockerBox.isOpen()) {
            ll_OpenStatus.setBackgroundResource(R.drawable.locker_box_door_status_2);
        } else {
            ll_OpenStatus.setBackgroundResource(R.drawable.locker_box_door_status_1);
        }

        if (lockerBox.isUsed()) {
            tv_Name.setTextColor(mContext.getResources().getColor(R.color.locker_box_use_status_2));
        } else {
            tv_Name.setTextColor(mContext.getResources().getColor(R.color.locker_box_use_status_1));
        }

        if(usages.size()<=0){
            ll_UsagesEmpty.setVisibility(View.VISIBLE);
            lv_Usages.setVisibility(View.GONE);
        }
        else {
            ll_UsagesEmpty.setVisibility(View.GONE);
            lv_Usages.setVisibility(View.VISIBLE);
        }

        SmLockerBoxUsageAdapter  smLockerBoxUsageAdapter=new SmLockerBoxUsageAdapter(mContext,usages);
        smLockerBoxUsageAdapter.setOnClickListener(new SmLockerBoxUsageAdapter.OnClickListener() {
            @Override
            public void onDeleteClick(LockerBoxUsageBean usage) {
                if(onClickListener!=null) {
                    onClickListener.onDeleteUsage(usage);
                }
            }
        });
        lv_Usages.setAdapter(smLockerBoxUsageAdapter);

    }

    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void onGoSelectUser(String deviceId, String cabinetId,String slotId);
        void onDeleteUsage(LockerBoxUsageBean usage);
        void onOpenBox(String deviceId, String cabinetId,String slotId);
    }


}