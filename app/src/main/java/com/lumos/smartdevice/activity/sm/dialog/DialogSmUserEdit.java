package com.lumos.smartdevice.activity.sm.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetUserGetDetail;
import com.lumos.smartdevice.api.rop.RetUserSave;
import com.lumos.smartdevice.api.rop.RopUserGetDetail;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.StringUtil;

public class DialogSmUserEdit extends Dialog {

    private static final String TAG = "DialogSmUserEdit";
    private final Dialog mThis;
    private final BaseFragmentActivity mContext;
    private final View mLayoutRes;

    private final View btn_Close;
    private final View btn_Save;

    private final ImageView iv_Avatar;
    private final EditText et_Username;
    private final TextView tv_Username;
    private final EditText et_Password;
    private final EditText et_Fullname;

    private final TextView tv_RenLian;
    private final TextView tv_ZhiWen;
    private final TextView tv_IcCard;

    private final TextView btn_OpenEditPassword;
    private final TextView btn_CloseEditPassword;

    private String userId="";
    private String avatar="";

    private boolean checkUserNameIsPhoneFormat=false;


    private int version_mode=AppVar.VERSION_MODE_0;

    public DialogSmUserEdit(Context context) {
        super(context, R.style.dialog);
        mThis = this;
        mContext = (BaseFragmentActivity)context;
        mLayoutRes = LayoutInflater.from(context).inflate(R.layout.dialog_sm_useredit, null);

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


                String userName = et_Username.getText().toString();
                String password = et_Password.getText().toString();
                String fullName = et_Fullname.getText().toString();

                if (StringUtil.isEmptyNotNull(userName)) {
                    mContext.showToast(R.string.tips_username_isnotnull);
                    return;
                }

                if(checkUserNameIsPhoneFormat) {
                    if (!CommonUtil.isPhone(userName)) {
                        mContext.showToast(R.string.tips_username_formatnoright);
                        return;
                    }
                }



                if (StringUtil.isEmptyNotNull(userId)) {
                    if (StringUtil.isEmptyNotNull(password)) {
                        mContext.showToast(R.string.tips_password_isnotnull);
                        return;
                    }
                    else
                    {
                        if(!CommonUtil.isPassword(password)){
                            mContext.showToast(R.string.tips_password_formatnoright);
                            return;
                        }
                    }
                }
                else
                {
                    if (!StringUtil.isEmptyNotNull(password)) {
                        if(!CommonUtil.isPassword(password)){
                            mContext.showToast(R.string.tips_password_formatnoright);
                            return;
                        }
                    }
                }

                if(StringUtil.isEmptyNotNull(fullName)) {
                    mContext.showToast(R.string.tips_fullname_isnotnull);
                    return;
                }


                RopUserSave rop = new RopUserSave();
                rop.setUserId(userId);
                rop.setUserName(userName);
                rop.setPassword(password);
                rop.setFullName(fullName);
                rop.setAvatar(avatar);

                ReqInterface.getInstance(version_mode).userSave(rop, new ReqHandler() {

                    @Override
                    public void onBeforeSend() {
                        super.onBeforeSend();
                        mContext.showLoading(mContext);
                    }

                    @Override
                    public void onAfterSend() {
                        super.onAfterSend();
                        mContext.hideLoading(mContext);
                    }

                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetUserSave> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetUserSave>>() {
                        });

                        if(onClickListener!=null){
                            onClickListener.onSaveResult(rt);
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                });
            }
        });

        tv_RenLian = ViewHolder.get(mLayoutRes, R.id.tv_RenLian);
        tv_ZhiWen = ViewHolder.get(mLayoutRes, R.id.tv_ZhiWen);
        tv_IcCard = ViewHolder.get(mLayoutRes, R.id.tv_IcCard);


        iv_Avatar = ViewHolder.get(mLayoutRes, R.id.iv_Avatar);
        et_Username = ViewHolder.get(mLayoutRes, R.id.et_Username);
        tv_Username = ViewHolder.get(mLayoutRes, R.id.tv_Username);
        et_Password = ViewHolder.get(mLayoutRes, R.id.et_Password);
        et_Fullname = ViewHolder.get(mLayoutRes, R.id.et_Fullname);

        btn_OpenEditPassword = ViewHolder.get(mLayoutRes, R.id.btn_OpenEditPassword);
        btn_CloseEditPassword = ViewHolder.get(mLayoutRes, R.id.btn_CloseEditPassword);

        btn_OpenEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Password.setVisibility(View.VISIBLE);
                btn_OpenEditPassword.setVisibility(View.GONE);
                btn_CloseEditPassword.setVisibility(View.VISIBLE);
                et_Password.requestFocus();
            }
        });

        btn_CloseEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Password.setText("");
                et_Password.setVisibility(View.GONE);
                btn_OpenEditPassword.setVisibility(View.VISIBLE);
                btn_CloseEditPassword.setVisibility(View.GONE);
            }
        });

    }

    private int editType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutRes);
    }

    public void checkUserNameIsPhoneFormat(boolean flag) {
        this.checkUserNameIsPhoneFormat = flag;
    }

    public int getEditType() {
        return editType;
    }

    public void show(String userId,int version_mode) {

        super.show();

        this.userId = userId;
        this.version_mode = version_mode;
        if (StringUtil.isEmptyNotNull(userId)) {
            editType = 1;
            et_Username.setVisibility(View.VISIBLE);
            et_Username.requestFocus();
            tv_Username.setVisibility(View.GONE);
            et_Password.setVisibility(View.VISIBLE);
            btn_OpenEditPassword.setVisibility(View.GONE);
            btn_CloseEditPassword.setVisibility(View.GONE);
            et_Username.setText("");
            tv_Username.setText("");
            et_Password.setText("");
            et_Fullname.setText("");
            avatar = "app://default_avatar";
            CommonUtil.loadImageFromUrl(mContext, iv_Avatar, avatar);
        } else {
            editType = 2;
            RopUserGetDetail rop = new RopUserGetDetail();
            rop.setUserId(userId);


            ReqInterface.getInstance(version_mode).userGetDetail(rop, new ReqHandler() {

                        @Override
                        public void onBeforeSend() {
                            super.onBeforeSend();
                            mContext.showLoading(mContext);
                        }

                        @Override
                        public void onAfterSend() {
                            super.onAfterSend();
                            mContext.hideLoading(mContext);
                        }

                        @Override
                        public void onSuccess(String response) {
                            super.onSuccess(response);

                            ResultBean<RetUserGetDetail> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetUserGetDetail>>() {
                            });

                            if (rt.getCode() == ResultCode.SUCCESS) {
                                RetUserGetDetail ret = rt.getData();

                                et_Username.setVisibility(View.GONE);
                                tv_Username.setVisibility(View.VISIBLE);
                                et_Password.setVisibility(View.GONE);
                                btn_OpenEditPassword.setVisibility(View.VISIBLE);
                                btn_CloseEditPassword.setVisibility(View.GONE);
                                et_Username.setText(ret.getUserName());
                                tv_Username.setText(ret.getUserName());
                                et_Password.setText("");
                                et_Fullname.setText(ret.getFullName());
                                et_Fullname.requestFocus();
                                avatar = ret.getAvatar();
                                CommonUtil.loadImageFromUrl(mContext, iv_Avatar, avatar);
                            }
                        }

                        @Override
                        public void onFailure(String msg, Exception e) {
                            super.onFailure(msg, e);
                        }
                    }
            );

        }
    }


    private OnClickListener onClickListener;

    public void  setOnClickListener(OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public  interface OnClickListener{
        void onSaveResult(ResultBean<RetUserSave> result);
    }


}
