package com.lumos.smartdevice.activity.booker;


import android.os.Bundle;
import android.view.View;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class BookerQueryBookActivity extends BookerBaseActivity {

    private static final String TAG = "BookerQueryBookActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_query_book);
        setNavHeaderTtile(R.string.aty_bookermain_query);

        initView();
        initEvent();
        initData();
        setTimerByActivityFinish(120);
    }

    public void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
    }

    public void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
    }

    public void initData() {

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHome) {
                finish();
            }
        }
    }
}