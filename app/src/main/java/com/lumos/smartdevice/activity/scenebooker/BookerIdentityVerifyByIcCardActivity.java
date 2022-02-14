package com.lumos.smartdevice.activity.scenebooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class BookerIdentityVerifyByIcCardActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerIdentityVerifyIcCardActivity";

    private View btn_Nav_Footer_Goback;
    private View btn_GoInspect;

    private String intent_extra_action="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify_by_iccard);

        intent_extra_action = getIntent().getStringExtra("action");

        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify_by_iccard);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_Goback = findViewById(R.id.btn_Nav_Footer_Goback);
        btn_GoInspect = findViewById(R.id.btn_GoInspect);
    }

    private void initEvent() {
        btn_Nav_Footer_Goback.setOnClickListener(this);
        btn_GoInspect.setOnClickListener(this);
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
            else if (id == R.id.btn_GoInspect) {
                Intent intent = new Intent(getAppContext(), BookerBorrowReturnInspectActivity.class);
                intent.putExtra("action", intent_extra_action);
                startActivity(intent);
            }
        }
    }
}