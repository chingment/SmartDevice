package com.lumos.smartdevice.activity.sm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetOwnLogin;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

public class SmLoginActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "SmLoginActivity";

    private Button btn_LoginByAccount;
    private LinearLayout btn_HelpTool;
    private EditText et_UserName;
    private ImageView btn_UserName_Del;
    private EditText et_Password;
    private ImageView btn_Password_Del;
    private TextView tv_Scene;
    private TextView tv_AppVersion;
    private String scene_mode;//登录场景
    private int app_version_mode;//版本模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_login);

        setNavHeaderTtile(R.string.aty_nav_title_smlogin);
        setNavHeaderBtnByGoBackIsVisible(true);

        scene_mode = getIntent().getStringExtra("scene_mode");
        app_version_mode= DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_VERSION_MODE);

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
        btn_UserName_Del= findViewById(R.id.btn_UserName_Del);
        btn_Password_Del= findViewById(R.id.btn_Password_Del);
        tv_AppVersion= findViewById(R.id.tv_AppVersion);
        et_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        et_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());


    }

    private void initEvent() {
        btn_LoginByAccount.setOnClickListener(this);
        btn_UserName_Del.setOnClickListener(this);
        btn_Password_Del.setOnClickListener(this);

        LongClickUtil.setLongClick(new Handler(), btn_HelpTool, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!scene_mode.equals("init_data_help")) {
                    Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                    intent.putExtra("scene_mode", "init_data_help");
                    startActivity(intent);
                }
                //finish();
                return true;
            }
        });


        et_UserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isEmpty(editable + "")) {
                    btn_UserName_Del.setVisibility(View.INVISIBLE);

                } else {
                    btn_UserName_Del.setVisibility(View.VISIBLE);

                }
            }
        });

        et_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isEmpty(editable + "")) {
                    btn_Password_Del.setVisibility(View.INVISIBLE);

                } else {
                    btn_Password_Del.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData() {

        String lastUsername=AppCacheManager.getLastUserName(scene_mode);
        et_UserName.setText(lastUsername);
        switch (scene_mode){
            case "init_data_help":
                tv_Scene.setText(R.string.aty_smlogin_scene_init_data_help);
                break;
            case "manager_center":
                tv_Scene.setText(R.string.aty_smlogin_scene_manager_center);
                break;
        }

        tv_AppVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            } else if (id == R.id.btn_LoginByAccount) {
                loginByAccount();
            } else if (id == R.id.btn_Password_Del) {
                et_Password.setText("");
            } else if (id == R.id.btn_UserName_Del) {
                et_UserName.setText("");
            }
        }
    }

    private void loginByAccount(){


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

        switch (scene_mode){
            case "init_data_help":

                UserBean user1=DbManager.getInstance().checkUserPassword(username,password,"1");

                if(user1==null){
                    showToast(R.string.tips_password_noright);
                    return;
                }

                AppCacheManager.setLastUserName(scene_mode,username);
                AppCacheManager.setCurrentUser(user1);
                Intent  var1 = new Intent(getAppContext(), SmHelpToolActivity.class);
                startActivity(var1);
                finish();
                break;
            case "manager_center":

                RopOwnLoginByAccount rop=new RopOwnLoginByAccount();
                rop.setUserName(username);
                rop.setPassword(password);

                ReqInterface.getInstance().ownLoginByAccount(rop, new ReqHandler(){


                    @Override
                    public void onBeforeSend() {
                        super.onBeforeSend();
                        showLoading(SmLoginActivity.this);
                    }

                    @Override
                    public void onAfterSend() {
                        super.onAfterSend();
                        hideLoading(SmLoginActivity.this);
                    }

                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetOwnLogin> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetOwnLogin>>() {
                        });

                        if(rt.getCode()== ResultCode.SUCCESS) {
                            RetOwnLogin d=rt.getData();

                            AppCacheManager.setLastUserName(scene_mode,rt.getData().getUserName());

                            UserBean user=new UserBean();
                            user.setUserId(d.getUserId());
                            user.setUserName(d.getUserName());
                            user.setFullName(d.getFullName());
                            user.setAvatar(d.getAvatar());

                            AppCacheManager.setCurrentUser(user);

                            Intent var2 = new Intent(getAppContext(), SmHomeActivity.class);
                            startActivity(var2);
                            finish();
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

                break;
        }


    }
}
