package com.lumos.smartdevice.activity.sm;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmAppSceneSettingActivity extends BaseFragmentActivity {
    private static final String TAG = "SmAppSceneSettingActivity";
    private RadioGroup rg_VesionMode;
    private RadioGroup rg_SceneMode;
    private EditText et_Com_Prl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_app_scene_setting);

        setNavHeaderTtile(R.string.aty_nav_title_smappscenesetting);
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
//        rg_VesionMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                View v= findViewById(checkedId);
//                DbManager.getInstance().updateConfig(ConfigDao.FIELD_VERSION_MODE,v.getTag().toString());
//
//            }
//        });
//
//        rg_SceneMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                View v = findViewById(checkedId);
//                DbManager.getInstance().updateConfig(ConfigDao.FIELD_SCENE_MODE, v.getTag().toString());
//            }
//        });
    }

    private void initData() {

        int version_mode = DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_VERSION_MODE);

        if (version_mode != AppVar.VERSION_MODE_0) {
            if (version_mode==AppVar.VERSION_MODE_1) {
                rg_VesionMode.check(R.id.rbtn_VesionMode_1);
            } else if (version_mode==AppVar.VERSION_MODE_2) {
                rg_VesionMode.check(R.id.rbtn_VesionMode_2);
            }
        }

        int scene_mode = DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_SCENE_MODE);
        if (scene_mode != AppVar.SCENE_MODE_0) {
            if (scene_mode==AppVar.SCENE_MODE_1) {
                rg_SceneMode.check(R.id.rbtn_SceneMode_1);
            } else if (scene_mode==AppVar.SCENE_MODE_2) {
                rg_SceneMode.check(R.id.rbtn_SceneMode_2);
            }
        }

      //  String com_Prl = "[{\"cabinet_id\":\"locker_1\",\"name\":\"A柜\",\"com_id\":\"sys1\",\"com_baud\":19200,\"com_prl\":\"lbl_ss\",\"layout\":{\"span_count\":2,\"cells\":[\"1-1-1-0\",\"2-1-2-0\",\"3-1-3-0\",\"4-1-4-0\",\"5-1-5-0\",\"6-1-6-0\",\"7-1-7-0\",\"8-1-8-0\",\"9-1-9-0\",\"10-1-10-0\",\"11-1-11-0\",\"12-1-12-0\"]}}]";
      //  String com_Prl="[{\"cabinetId\":\"locker_1\",\"name\":\"A柜\",\"comId\":\"sys1\",\"comBaud\":19200,\"comPrl\":\"lbl_ss\",\"layout\":{\"spanCount\":2,\"cells\":[\"1-1-1-0\",\"2-1-2-0\",\"3-1-3-0\",\"4-1-4-0\",\"5-1-5-0\",\"6-1-6-0\",\"7-1-7-0\",\"8-1-8-0\",\"9-1-9-0\",\"10-1-10-0\",\"11-1-11-0\",\"12-1-12-0\"]}},{\"cabinetId\":\"locker_2\",\"name\":\"B柜\",\"comId\":\"sys1\",\"comBaud\":19200,\"comPrl\":\"lbl_ss\",\"layout\":{\"spanCount\":2,\"cells\":[\"1-1-1-0\",\"2-1-2-0\",\"3-1-3-0\",\"4-1-4-0\",\"5-1-5-0\",\"6-1-6-0\",\"7-1-7-0\",\"8-1-8-0\",\"9-1-9-0\",\"10-1-10-0\",\"11-1-11-0\",\"12-1-12-0\"]}}]";
        String com_Prl="[{\"cabinetId\":\"locker_1\",\"name\":\"A柜\",\"comId\":\"sys1\",\"comBaud\":19200,\"comPrl\":\"lbl_ss\",\"layout\":{\"spanCount\":2,\"cells\":[\"1-1-1-0\",\"2-1-2-0\"]}}]";
        et_Com_Prl.setText(com_Prl);
        //et_Com_Prl.setText("[{\"cabinet_id\":\"locker_1\",\"name\":\"箱子01\",\"com_id\":\"sys1\",\"com_baud\":19200,\"com_prl\":\"lbl_ss\",\"layout\":[[\"1-1-1-0\",\"2-1-2-0\"],[\"3-1-3-0\",\"4-1-4-0\"],[\"5-1-5-0\",\"6-1-6-0\"],[\"7-1-7-0\",\"8-1-8-0\"],[\"9-1-9-0\",\"10-1-10-0\"],[\"11-1-11-0\",\"12-1-12-0\"]]}]");
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            } else if (id == R.id.btn_Nav_Header_Right) {
                RadioButton rb_VesionMode = findViewById(rg_VesionMode.getCheckedRadioButtonId());
                int version_mode = AppVar.VERSION_MODE_0;
                if (rb_VesionMode != null) {
                    version_mode =Integer.parseInt(rb_VesionMode.getTag().toString());
                }

                int scene_mode = AppVar.SCENE_MODE_0;
                RadioButton rb_SceneMode = findViewById(rg_SceneMode.getCheckedRadioButtonId());
                if (rb_SceneMode != null) {
                    scene_mode = Integer.parseInt(rb_SceneMode.getTag().toString());
                }

                String json_Com_Prl = et_Com_Prl.getText().toString();

                ResultBean<Object> result = DbManager.getInstance().saveAppScene(version_mode, scene_mode, json_Com_Prl);

                showToast(result.getMsg());
            }
        }
    }


}
