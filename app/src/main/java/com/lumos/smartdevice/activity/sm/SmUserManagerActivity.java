package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.model.api.UserSaveResultBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogUserEdit;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.HashMap;

public class SmUserManagerActivity extends BaseFragmentActivity {

    private TextView btn_NewUser;
    private CustomDialogUserEdit dialog_UserEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smusermanager);

        setNavHeaderTtile(R.string.aty_smusermanager_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_NewUser = findViewById(R.id.btn_NewUser);
        dialog_UserEdit=new CustomDialogUserEdit(SmUserManagerActivity.this);
        dialog_UserEdit.setOnDialogListener(new CustomDialogUserEdit.OnDialogHandle() {
            @Override
            public void onSave(HashMap<String, String> form) {

                String userid = form.get("userid");
                String username = form.get("username");
                String password = form.get("password");
                String fullname = form.get("fullname");

                if(StringUtil.isEmptyNotNull(username)) {
                    showToast(R.string.tips_username_isnotnull);
                    return;
                }

                if(StringUtil.isEmptyNotNull(password)) {
                    showToast(R.string.tips_password_isnotnull);
                    return;
                }

                if(StringUtil.isEmptyNotNull(fullname)) {
                    showToast(R.string.tips_fullname_isnotnull);
                    return;
                }


                RopUserSave rop=new RopUserSave();
                rop.setUserId(userid);
                rop.setUserName(username);
                rop.setPassword(password);
                rop.setFullName(fullname);

                ReqInterface.getInstance().userSave(rop, new ReqHandler(){

                    @Override
                    public void onBeforeSend() {
                        super.onBeforeSend();
                        showLoading(SmUserManagerActivity.this);
                    }

                    @Override
                    public void onAfterSend() {
                        super.onAfterSend();
                        hideLoading(SmUserManagerActivity.this);
                    }

                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<UserSaveResultBean> rt = JSON.parseObject(response, new TypeReference<ResultBean<UserSaveResultBean>>() {
                        });

                        if(rt.getCode()== ResultCode.SUCCESS) {
                            UserSaveResultBean d=rt.getData();

                            dialog_UserEdit.hide();
                        }
                        else {
                            showToast(rt.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                });

            }
        });
    }

    private void initEvent() {
        btn_NewUser.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_UserEdit != null) {
            dialog_UserEdit.cancel();
        }


    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case  R.id.btn_NewUser:
                    dialog_UserEdit.show();
                    break;
            }
        }
    }
}
