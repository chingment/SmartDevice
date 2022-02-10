package com.lumos.smartdevice.activity.scenebooker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class BookerBorrowReturnOverviewActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerBorrowReturnOverviewActivity";

    private View btn_Nav_Footer_Goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_overview);
        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify_by_iccard);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_Goback = findViewById(R.id.btn_Nav_Footer_Goback);
    }

    private void initEvent() {
        btn_Nav_Footer_Goback.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_Goback) {
                finish();
            }
        }
    }
}