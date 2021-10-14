package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmDeviceInfoActivity extends BaseFragmentActivity {

    private static final String TAG = "SmLockerBoxActivity";


    private TextView tv_MerchName;
    private TextView tv_StoreName;
    private TextView tv_ShopName;
    private TextView tv_Address;
    private TextView tv_DeviceId;
    private TextView tv_AppVersion;
    private TextView tv_VersionMode;
    private TextView tv_SceneMode;

    private DeviceBean device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smdeviceinfo);

        setNavHeaderTtile(R.string.aty_nav_title_smdeviceinfo);
        setNavHeaderBtnByGoBackIsVisible(true);

        device = getDevice();

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tv_MerchName = findViewById(R.id.tv_MerchName);
        tv_StoreName = findViewById(R.id.tv_StoreName);
        tv_ShopName = findViewById(R.id.tv_ShopName);
        tv_Address = findViewById(R.id.tv_Address);
        tv_DeviceId = findViewById(R.id.tv_DeviceId);
        tv_AppVersion = findViewById(R.id.tv_AppVersion);
        tv_VersionMode = findViewById(R.id.tv_VersionMode);
        tv_SceneMode = findViewById(R.id.tv_SceneMode);
    }

    private void initEvent() {

    }

    private void initData() {
        tv_DeviceId.setText(DeviceUtil.getDeviceId());
        tv_AppVersion.setText(BuildConfig.VERSION_NAME);

        tv_MerchName.setText(device.getMerchName());
        tv_StoreName.setText(device.getStoreName());
        tv_ShopName.setText(device.getShopName());
        tv_Address.setText(device.getAddress());

        if (device.getSceneMode().equals("1")) {
            tv_SceneMode.setText(getAppContext().getString(R.string.t_scenemode_1));
        } else if (device.getSceneMode().equals("2")) {
            tv_SceneMode.setText(getAppContext().getString(R.string.t_scenemode_2));
        }

        if (device.getVersionMode().equals("1")) {
            tv_VersionMode.setText(getAppContext().getString(R.string.t_vesionmode_1));
        } else if (device.getVersionMode().equals("2")) {
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