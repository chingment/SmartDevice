package com.lumos.smartdevice.activity.sm;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmFactorySettingActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "SmFactorySettingActivity";
    private LinearLayout btn_Nav_AppSceneSetting;
    private TextView tv_Nav_AppScene_Tips;

    private TextView tv_DeviceId;
    private TextView tv_AppVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smfactorysetting);

        setNavHeaderTtile(R.string.aty_nav_title_smfactorysetting);
        setNavHeaderBtnByGoBackIsVisible(true);

        initView();
        initEvent();
        initData();

    }

    private void initView() {
        btn_Nav_AppSceneSetting = findViewById(R.id.btn_Nav_AppSceneSetting);
        tv_Nav_AppScene_Tips = findViewById(R.id.tv_Nav_AppScene_Tips);
        tv_DeviceId  = findViewById(R.id.tv_DeviceId);
        tv_AppVersion  = findViewById(R.id.tv_AppVersion);
    }

    private void initEvent() {
        btn_Nav_AppSceneSetting.setOnClickListener(this);
    }

    private void initData() {

        tv_DeviceId.setText(DeviceUtil.getDeviceId());
        tv_AppVersion.setText(BuildConfig.VERSION_NAME);

        setNavAppSceneTips();

    }

    private void setNavAppSceneTips(){

        String app_version_mode = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);
        String app_scene_mode = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_SCENE_MODE);
        if (app_version_mode == null || app_version_mode.equals("0")) {
            tv_Nav_AppScene_Tips.setText(R.string.aty_smfactorysetting_tips_nosetversion);
        } else if (app_scene_mode == null || app_scene_mode.equals("0")) {
            tv_Nav_AppScene_Tips.setText(R.string.aty_smfactorysetting_tips_nosetscene);
        }
        else {
            String appScene_Tips = ConfigDao.getSceneModeName(app_scene_mode) + "[" + ConfigDao.getVersionModeName(app_version_mode) + "]";
            tv_Nav_AppScene_Tips.setText(appScene_Tips);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavAppSceneTips();
    }


    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            } else if (id == R.id.btn_Nav_AppSceneSetting) {
                Intent intent = new Intent(getAppContext(), SmAppSceneSettingActivity.class);
                startActivity(intent);
            }
        }
    }
}
