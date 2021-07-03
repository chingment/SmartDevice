package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmLoginActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "SmLoginActivity";

    private Button btn_LoginByAccount;
    private EditText et_UserName;
    private EditText et_Password;
    private TextView tv_Scene;
    private String scene;//登录场景
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smlogin);

        setNavHeaderTtile(R.string.aty_smlogin_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        scene = getIntent().getStringExtra("scene");

        initView();
        initEvent();
        initData();
    }

    private void initView() {

        btn_LoginByAccount = findViewById(R.id.btn_LoginByAccount);
        et_UserName = findViewById(R.id.et_UserName);
        et_Password = findViewById(R.id.et_Password);
        tv_Scene = findViewById(R.id.tv_Scene);

    }

    private void initEvent() {
        btn_LoginByAccount.setOnClickListener(this);
    }

    private void initData() {

        switch (scene){
            case "init_data_help":
                tv_Scene.setText(R.string.aty_smlogin_scene_init_data_help);
                break;
            case "manager_center":
                tv_Scene.setText(R.string.aty_smlogin_scene_manager_center);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case R.id.btn_LoginByAccount:
                    loginByAccount();
                    break;
            }
        }
    }

    private void loginByAccount(){
        Intent intent = new Intent(getAppContext(), SmHelpToolActivity.class);
        startActivity(intent);
        finish();
    }
}
