package com.lumos.smartdevice.activity.sm;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmDeviceInfoActivity extends SmBaseActivity {

    private static final String TAG = "SmLockerBoxActivity";


    private TextView tv_MerchId;
    private TextView tv_DeviceId;
    private TextView tv_AppVersion;
    private TextView tv_VersionMode;
    private TextView tv_SceneMode;

    private DeviceVo device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_device_info);

        setNavHeaderTtile(R.string.aty_nav_title_smdeviceinfo);
        setNavHeaderBtnByGoBackIsVisible(true);

        device = getDevice();

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tv_MerchId = findViewById(R.id.tv_MerchId);
        tv_DeviceId = findViewById(R.id.tv_DeviceId);
        tv_AppVersion = findViewById(R.id.tv_AppVersion);
        tv_VersionMode = findViewById(R.id.tv_VersionMode);
        tv_SceneMode = findViewById(R.id.tv_SceneMode);
    }

    private void initEvent() {

    }

    private void initData() {

        tv_MerchId.setText(device.getMerchId());
        tv_DeviceId.setText(DeviceUtil.getDeviceId());
        tv_AppVersion.setText(BuildConfig.VERSION_NAME);


        if (device.getSceneMode() == 1) {
            tv_SceneMode.setText(getAppContext().getString(R.string.t_scenemode_1));
        } else if (device.getSceneMode() == 2) {
            tv_SceneMode.setText(getAppContext().getString(R.string.t_scenemode_2));
        }

        if (device.getVersionMode() == 1) {
            tv_VersionMode.setText(getAppContext().getString(R.string.t_vesionmode_1));
        } else if (device.getVersionMode() == 2) {
            tv_VersionMode.setText(getAppContext().getString(R.string.t_vesionmode_2));
        }
    }

    @Override
    public void onClick(View v) {

        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            }

        }
    }
}