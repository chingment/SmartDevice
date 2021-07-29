package com.lumos.smartdevice.activity.sm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class SmUserManagerActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smusermanager);

        setNavHeaderTtile(R.string.aty_smusermanager_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
            }
        }
    }
}
