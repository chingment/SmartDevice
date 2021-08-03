package com.lumos.smartdevice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.ui.ViewHolder;

import java.util.HashMap;

public class CustomDialogUserEdit extends Dialog {

    private static final String TAG = "CustomDialogUserEdit";
    private Dialog mThis;
    private Context mContext;
    private View mLayoutRes;

    private View btn_Close;
    private View btn_Save;

    private EditText et_Username;
    private TextView tv_Username;
    private EditText et_Password;
    private EditText et_Fullname;

    private TextView tv_RenLian;
    private TextView tv_ZhiWen;
    private TextView tv_IcCard;
    private TextView tv_BoxName;


    public CustomDialogUserEdit(Context context) {
        super(context, R.style.custom_dialog);
        mThis = this;
        mContext = context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.custom_dialog_useredit, null);

        btn_Close = ViewHolder.get(mLayoutRes, R.id.dialog_Btn_Close);
        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThis.hide();
            }
        });

        btn_Save = ViewHolder.get(mLayoutRes, R.id.btn_Save);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null){
                    HashMap<String, String> form=new HashMap<>();


                    String username=et_Username.getText().toString();
                    String password=et_Password.getText().toString();
                    String fullname=et_Fullname.getText().toString();


                    form.put("username",username);
                    form.put("password",password);
                    form.put("fullname",fullname);
                    form.put("avatar","app://default_avatar");

                    onClickListener.onSave(form);
                }
            }
        });

        tv_RenLian = ViewHolder.get(mLayoutRes, R.id.tv_RenLian);
        tv_ZhiWen = ViewHolder.get(mLayoutRes, R.id.tv_ZhiWen);
        tv_IcCard = ViewHolder.get(mLayoutRes, R.id.tv_IcCard);
        tv_BoxName= ViewHolder.get(mLayoutRes, R.id.tv_BoxName);

        et_Username = ViewHolder.get(mLayoutRes, R.id.et_Username);
        tv_Username = ViewHolder.get(mLayoutRes, R.id.tv_Username);
        et_Password = ViewHolder.get(mLayoutRes, R.id.et_Password);
        et_Fullname = ViewHolder.get(mLayoutRes, R.id.et_Fullname);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }


    public void setMode(int mode) {
        if (mode == 1) {
            et_Username.setVisibility(View.VISIBLE);
            tv_Username.setVisibility(View.GONE);
        } else if (mode == 2) {
            et_Username.setVisibility(View.GONE);
            tv_Username.setVisibility(View.VISIBLE);
        }
    }

    public void setData(UserBean user) {

        et_Username.setText(user.getUserName());
        tv_Username.setText(user.getUserName());
        et_Password.setText("");
        et_Fullname.setText(user.getFullName());
    }


    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void onSave(HashMap<String, String> form);
    }


}
