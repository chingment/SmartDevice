package com.lumos.smartdevice.activity.scenebooker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.ui.BaseFragmentActivity;

public class BookerMainActivity  extends BaseFragmentActivity {

    private static final String TAG = "BookerMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookermain);
    }
}