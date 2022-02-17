package com.lumos.smartdevice.activity.scenebooker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class BookerBorrowReturnOverviewActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerBorrowReturnOverviewActivity";

    private View btn_Nav_Footer_Finish;
    private View btn_Nav_Footer_GoHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_overview);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_overview);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_Finish = findViewById(R.id.btn_Nav_Footer_Finish);
        btn_Nav_Footer_GoHelp = findViewById(R.id.btn_Nav_Footer_GoHelp);
    }

    private void initEvent() {
        btn_Nav_Footer_Finish.setOnClickListener(this);
        btn_Nav_Footer_GoHelp.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_Finish) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHelp) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}