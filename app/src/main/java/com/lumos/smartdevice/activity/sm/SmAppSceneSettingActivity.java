package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.db.model.ConfigBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;

public class SmAppSceneSettingActivity extends BaseFragmentActivity {

    private RadioGroup rg_VesionMode;
    private RadioGroup rg_SceneMode;
    private LinearLayout lyt_Setting_Scene_Mode_1;

    private Spinner sp_Com_Scene_Mode_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smappscenesetting);

        setNavHeaderTtile(R.string.aty_smappscenesetting_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);
        setNavHeaderBtnByRightIsVisible(false);


        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rg_VesionMode = findViewById(R.id.rg_VesionMode);
        rg_SceneMode = findViewById(R.id.rg_SceneMode);
        lyt_Setting_Scene_Mode_1 = findViewById(R.id.lyt_Setting_Scene_Mode_1);
        sp_Com_Scene_Mode_1 = findViewById(R.id.sp_Com_Name_Scene_Mode_1);
    }

    private void initEvent() {
        rg_VesionMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View v= findViewById(checkedId);
                DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,v.getTag().toString());

                String scene = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_SCENE_MODE);
                if(scene!=null&&scene.equals("1")) {
                    String version = v.getTag().toString();
                    if (version.equals("1")) {
                        lyt_Setting_Scene_Mode_1.setVisibility(View.VISIBLE);
                    } else {
                        lyt_Setting_Scene_Mode_1.setVisibility(View.GONE);
                    }
                }
                else {
                    lyt_Setting_Scene_Mode_1.setVisibility(View.GONE);
                }

            }
        });

        rg_SceneMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View v = findViewById(checkedId);
                DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE, v.getTag().toString());

                String version = DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);
                if(version!=null&&version.equals("1")) {
                    String scene = v.getTag().toString();
                    if (scene.equals("1")) {
                        lyt_Setting_Scene_Mode_1.setVisibility(View.VISIBLE);
                    } else {
                        lyt_Setting_Scene_Mode_1.setVisibility(View.GONE);
                    }
                }
                else {
                    lyt_Setting_Scene_Mode_1.setVisibility(View.GONE);
                }
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

        ArrayList data_list = new ArrayList<String>();
        data_list.add("北京");
        data_list.add("上海");
        data_list.add("广州");
        data_list.add("深圳");

        //适配器
        ArrayAdapter arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_Com_Scene_Mode_1.setAdapter(arr_adapter);

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
