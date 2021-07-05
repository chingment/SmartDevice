package com.lumos.smartdevice.activity.scenelocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.LongClickUtil;

public class LockerMainActivity  extends BaseFragmentActivity {

    private RelativeLayout lyt_Body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockermain);

        initView();
        initEvent();
        initData();

    }

    private void initView() {

        lyt_Body = findViewById(R.id.lyt_Body);

    }

    private void initEvent() {

        LongClickUtil.setLongClick(new Handler(), lyt_Body, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene","manager_center");
                startActivity(intent);
                //finish();
                return true;
            }
        });

    }

    private void initData() {


    }
}
