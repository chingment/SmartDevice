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


    private TextView tv_MerchNameK;
    private TextView tv_MerchNameV;
    private TextView tv_StoreNameK;
    private TextView tv_StoreNameV;
    private TextView tv_ShopNameK;
    private TextView tv_ShopNameV;
    private TextView tv_ShopAddress;
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
        tv_MerchNameK = findViewById(R.id.tv_MerchNameK);
        tv_MerchNameV = findViewById(R.id.tv_MerchNameV);
        tv_StoreNameK = findViewById(R.id.tv_StoreNameK);
        tv_StoreNameV = findViewById(R.id.tv_StoreNameV);
        tv_ShopNameK = findViewById(R.id.tv_ShopNameK);
        tv_ShopNameV = findViewById(R.id.tv_ShopNameV);
        tv_ShopAddress = findViewById(R.id.tv_ShopAddress);
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

        tv_MerchNameK.setText(device.getMerch().getNameK());
        tv_MerchNameV.setText(device.getMerch().getNameV());
        tv_StoreNameK.setText(device.getStore().getNameK());
        tv_StoreNameV.setText(device.getStore().getNameV());
        tv_ShopNameK.setText(device.getShop().getNameK());
        tv_ShopNameV.setText(device.getShop().getNameV());
        tv_ShopAddress.setText(device.getShop().getAddress());

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