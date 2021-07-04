package com.lumos.smartdevice.activity.sm;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmFactorySettingActivity extends BaseFragmentActivity implements View.OnClickListener {

    private LinearLayout btn_Nav_AppSceneSetting;

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
    }

    private void initEvent() {
        btn_Nav_AppSceneSetting.setOnClickListener(this);
    }

    private void initData() {

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
