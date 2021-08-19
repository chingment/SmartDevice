package com.lumos.smartdevice.activity.sm;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.List;

public class SmAppSceneSettingActivity extends BaseFragmentActivity {
    private static final String TAG = "SmAppSceneSettingActivity";
    private RadioGroup rg_VesionMode;
    private RadioGroup rg_SceneMode;
    private EditText et_Com_Prl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smappscenesetting);

        setNavHeaderTtile(R.string.aty_smappscenesetting_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);
        setNavHeaderBtnByRightIsVisible(true);


        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rg_VesionMode = findViewById(R.id.rg_VesionMode);
        rg_SceneMode = findViewById(R.id.rg_SceneMode);
        et_Com_Prl = findViewById(R.id.et_Com_Prl);
    }

    private void initEvent() {
        rg_VesionMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View v= findViewById(checkedId);
                DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,v.getTag().toString());

            }
        });

        rg_SceneMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View v = findViewById(checkedId);
                DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE, v.getTag().toString());
            }
        });
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

        //

        et_Com_Prl.setText("[{\"cabinet_id\":\"locker_1\",\"name\":\"箱子01\",\"com_id\":\"sys1\",\"com_baud\":19200,\"com_prl\":\"lbl_ss\",\"layout\":[[\"1-1-1-0\",\"2-1-2-0\"],[\"3-1-3-0\",\"4-1-4-1\"]]}]");
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case R.id.btn_Nav_Header_Right:
                    RadioButton rb_VesionMode = findViewById(rg_VesionMode.getCheckedRadioButtonId());
                    String version_mode = "0";
                    if (rb_VesionMode != null) {
                        version_mode = rb_VesionMode.getTag().toString();
                    }

                    String scene_mode = "0";
                    RadioButton rb_SceneMode = findViewById(rg_SceneMode.getCheckedRadioButtonId());
                    if (rb_SceneMode != null) {
                        scene_mode = rb_SceneMode.getTag().toString();
                    }

                    String json_Com_Prl = et_Com_Prl.getText().toString();

                    ResultBean result = DbManager.getInstance().saveAppScene(version_mode, scene_mode, json_Com_Prl);

                    showToast(result.getMsg());

                    break;

            }
        }
    }


}
