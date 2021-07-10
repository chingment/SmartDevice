package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

public class SmLoginActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "SmLoginActivity";

    private Button btn_LoginByAccount;
    private LinearLayout btn_HelpTool;
    private EditText et_UserName;
    private EditText et_Password;
    private TextView tv_Scene;
    private String scene;//登录场景
    private String version_mode;//版本模式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smlogin);

        setNavHeaderTtile(R.string.aty_smlogin_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        scene = getIntent().getStringExtra("scene");
        version_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);

        initView();
        initEvent();
        initData();
    }

    private void initView() {

        btn_LoginByAccount = findViewById(R.id.btn_LoginByAccount);
        btn_HelpTool = findViewById(R.id.btn_HelpTool);
        et_UserName = findViewById(R.id.et_UserName);
        et_Password = findViewById(R.id.et_Password);
        tv_Scene = findViewById(R.id.tv_Scene);


        et_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        et_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());


    }

    private void initEvent() {
        btn_LoginByAccount.setOnClickListener(this);

        if(scene!="1"){
            LongClickUtil.setLongClick(new Handler(), btn_HelpTool, 500, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!scene.equals("init_data_help")) {
                        Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                        intent.putExtra("scene", "init_data_help");
                        startActivity(intent);
                    }
                    //finish();
                    return true;
                }
            });
        }
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

        Intent intent=null;
        String username=et_UserName.getText().toString();
        String password=et_Password.getText().toString();

        if(StringUtil.isEmptyNotNull(username)) {
            showToast(R.string.tips_username_isnotnull);
            return;
        }

        if(StringUtil.isEmptyNotNull(password)) {
            showToast(R.string.tips_password_isnotnull);
            return;
        }

        switch (scene){
            case "init_data_help":

                if(!DbManager.getInstance().checkUserPassword(username,password,"1")){
                    showToast(R.string.tips_password_noright);
                    return;
                }

                intent = new Intent(getAppContext(), SmHelpToolActivity.class);

                break;
            case "manager_center":
                intent = new Intent(getAppContext(), SmHomeActivity.class);
                break;
        }

        if(intent!=null) {
            startActivity(intent);
            finish();
        }
    }
}
