package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.BaseActivity;

public class SmBookerTakeStockActivity extends SmBaseActivity {

    private static final String TAG = "SmBookerTakeStockActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_booker_take_stock);
        setNavHeaderTtile(R.string.aty_nav_title_smbookertakestock);
        setNavHeaderBtnByGoBackIsVisible(true);


        initView();
        initEvent();
        initData();
    }

    private void initView() {

    }

    private void initEvent() {

    }

    private void initData() {

    }
}