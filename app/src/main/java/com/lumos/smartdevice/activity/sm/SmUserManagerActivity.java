package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogUserEdit;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

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
