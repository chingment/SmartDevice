package com.lumos.smartdevice.activity.sm;


import android.os.Bundle;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.BaseActivity;

public class SmExcepitonViewerActivity extends SmBaseActivity {

    private static final String TAG = "SmExcepitonViewerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_excepiton_viewer);

        setNavHeaderTtile(R.string.aty_nav_title_smexceptionviewer);
    }
}
