package com.lumos.smartdevice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.ViewHolder;

public class CustomDialogLockerBox extends Dialog {

    private static final String TAG = "CustomDialogLockerBox";
    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private View btn_Close;

    private TextView tv_BoxName;
    private TextView tv_UserName;
    private TextView tv_FullName;
    private TextView btn_SelectUser;
    private ImageView btn_DeleteUser;

    private String cabinetId;
    private String slotId;
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
        tv_UserName=ViewHolder.get(mLayoutRes, R.id.tv_UserName);
        tv_FullName=ViewHolder.get(mLayoutRes, R.id.tv_FullName);
        btn_DeleteUser=ViewHolder.get(mLayoutRes, R.id.btn_DeleteUser);
        btn_SelectUser = ViewHolder.get(mLayoutRes, R.id.btn_SelectUser);
        btn_SelectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener!=null) {
                    onClickListener.onSelectUser();
                }
            }
        });

        btn_DeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener!=null) {
                    onClickListener.onDeleteUser();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public String getCabinetId(){
        return cabinetId;
    }

    public String getSlotId(){
        return slotId;
    }


    public void setLockerBox(CabinetBean cabinet,String slotId) {

        this.cabinetId = cabinet.getCabinetId();
        this.slotId = slotId;

        String[] box_Prams = slotId.split("-");

        tv_BoxName.setText(box_Prams[2]);

    }

    public void setLockerBoxUser(UserBean user) {

        if (user == null) {
            btn_SelectUser.setVisibility(View.VISIBLE);
            btn_DeleteUser.setVisibility(View.GONE);
            tv_FullName.setVisibility(View.GONE);
            tv_UserName.setVisibility(View.GONE);
        } else {
            btn_SelectUser.setVisibility(View.GONE);
            btn_DeleteUser.setVisibility(View.VISIBLE);
            tv_FullName.setVisibility(View.VISIBLE);
            tv_UserName.setVisibility(View.VISIBLE);
            tv_FullName.setText(user.getFullName());
            tv_UserName.setText("["+user.getUserName()+"]");
        }
    }

    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void onSelectUser();
        void onDeleteUser();
    }


}
