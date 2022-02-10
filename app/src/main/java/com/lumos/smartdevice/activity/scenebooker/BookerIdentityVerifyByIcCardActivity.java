package com.lumos.smartdevice.activity.scenebooker;

import android.os.Bundle;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;

public class BookerIdentityVerifyByIcCardActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerIdentityVerifyIcCardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify_by_iccard);
        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify_by_iccard);
    }
}