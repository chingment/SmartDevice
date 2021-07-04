package com.lumos.smartdevice.activity.sm;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.own.AppManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmFactorySettingActivity extends BaseFragmentActivity implements View.OnClickListener {

    private LinearLayout btn_Nav_AppSceneSetting;
    private TextView tv_Nav_AppScene_Tips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smfactorysetting);

        setNavHeaderTtile(R.string.aty_factorysetting_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        initView();
        initEvent();
        initData();

    }

    private void initView() {
        btn_Nav_AppSceneSetting = findViewById(R.id.btn_Nav_AppSceneSetting);
        tv_Nav_AppScene_Tips = findViewById(R.id.tv_Nav_AppScene_Tips);
    }

    private void initEvent() {
        btn_Nav_AppSceneSetting.setOnClickListener(this);
    }

    private void initData() {

        setNavAppSceneTips();

    }

    private void setNavAppSceneTips(){

        String version_mode = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);
        String scene_mode = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_SCENE_MODE);
        if (version_mode == null || version_mode.equals("0")) {
            tv_Nav_AppScene_Tips.setText(R.string.aty_factorysetting_tips_nosetversion);
        } else if (scene_mode == null || scene_mode.equals("0")) {
            tv_Nav_AppScene_Tips.setText(R.string.aty_factorysetting_tips_nosetscene);
        }
        else {
            tv_Nav_AppScene_Tips.setText(ConfigDao.getSceneModeName(scene_mode)+"["+ConfigDao.getVersionModeName(version_mode)+"]");
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
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case R.id.btn_Nav_AppSceneSetting:
                    Intent intent = new Intent(getAppContext(), SmAppSceneSettingActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }
}
