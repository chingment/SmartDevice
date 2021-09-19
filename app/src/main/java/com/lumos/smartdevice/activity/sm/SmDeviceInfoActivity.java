package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmDeviceInfoActivity extends BaseFragmentActivity {

    private static final String TAG = "SmLockerBoxActivity";


    private TextView tv_MerchName;
    private TextView tv_StoreName;
    private TextView tv_ShopName;
    private TextView tv_Address;
    private TextView tv_DeviceId;
    private TextView tv_ComName;
    private TextView tv_AppVersion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smdeviceinfo);

        setNavHeaderTtile(R.string.aty_deviceinfo_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

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
        tv_ComName = findViewById(R.id.tv_ComName);
        tv_AppVersion = findViewById(R.id.tv_AppVersion);
    }

    private void initEvent() {

    }

    private void initData() {

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