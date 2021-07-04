package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.db.model.ConfigBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmAppSceneSettingActivity extends BaseFragmentActivity {

    private RadioGroup rg_VesionMode;
    private RadioGroup rg_SceneMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smappscenesetting);

        setNavHeaderTtile(R.string.aty_appscenesetting_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);
        setNavHeaderBtnByRightIsVisible(true);


        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rg_VesionMode = findViewById(R.id.rg_VesionMode);
        rg_SceneMode = findViewById(R.id.rg_SceneMode);
    }

    private void initEvent() {

    }

    private void initData() {

        String version_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);

        if(version_mode!=null) {
            if (version_mode.equals("1")) {
                rg_VesionMode.check(R.id.rbtn_VesionMode_1);
            } else if (version_mode.equals("2")) {
                rg_VesionMode.check(R.id.rbtn_VesionMode_2);
            }
        }

        String scene_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_SCENE_MODE);
        if(scene_mode!=null) {
            if (scene_mode.equals("1")) {
                rg_SceneMode.check(R.id.rbtn_SceneMode_1);
            } else if (scene_mode.equals("2")) {
                rg_SceneMode.check(R.id.rbtn_SceneMode_2);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case R.id.btn_Nav_Header_Right:
                    RadioButton rb_VesionMode = (RadioButton) findViewById(rg_VesionMode.getCheckedRadioButtonId());
                    if (rb_VesionMode != null) {
                       String  version_mode= rb_VesionMode.getTag().toString();
                       DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,version_mode);
                    }

                    RadioButton rb_SceneMode = (RadioButton) findViewById(rg_SceneMode.getCheckedRadioButtonId());
                    if (rb_SceneMode != null) {
                        String  scene_mode= rb_SceneMode.getTag().toString();
                        DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE,scene_mode);
                    }


                    showToast(getAppContext().getString(R.string.save_success));
                    break;

            }
        }
    }
}
