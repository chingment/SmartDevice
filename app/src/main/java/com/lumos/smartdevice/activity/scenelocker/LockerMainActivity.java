package com.lumos.smartdevice.activity.scenelocker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.LongClickUtil;

public class LockerMainActivity  extends BaseFragmentActivity {

    private static final String TAG = "LockerMainActivity";
    private RelativeLayout lyt_Body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockermain);

        initView();
        initEvent();
        initData();

        DbManager.getInstance().savelockerBoxUsage("locker_1","1-1-1-0","2","dsdaasda");

        int a=1/0;
    }

    private void initView() {

        lyt_Body = findViewById(R.id.lyt_Body);

    }

    private void initEvent() {

        LongClickUtil.setLongClick(new Handler(), lyt_Body, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene_mode","manager_center");
                startActivity(intent);
                //finish();
                return true;
            }
        });

    }

    private void initData() {


    }
}
