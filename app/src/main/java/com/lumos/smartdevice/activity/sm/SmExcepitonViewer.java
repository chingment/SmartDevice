package com.lumos.smartdevice.activity.sm;


import android.os.Bundle;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;

public class SmExcepitonViewer extends BaseFragmentActivity{

    private static final String TAG = "SmExcepitonViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_excepiton_viewer);

        setNavHeaderTtile(R.string.aty_nav_title_smfactorysetting);
    }
}
