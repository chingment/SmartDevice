package com.lumos.smartdevice.activity.scenebooker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

public class BookerMainActivity  extends BaseFragmentActivity {

    private static final String TAG = "BookerMainActivity";

    private View btn_QueryBook;
    private View rl_Header;
    private View btn_BorrowBook;
    private View btn_ReturnBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_main);

        initView();
        initEvent();
        initData();

        //DbManager.getInstance().saveLockerBoxUsage("locker_1","1-1-1-0","2","dsdaasda");
        //DbManager.getInstance().saveLockerBoxUsage("locker_1","2-1-2-0","2","dsdaasdDDa");
        //int a=1/0;
    }

    private void initView() {

        btn_QueryBook = findViewById(R.id.btn_QueryBook);
        btn_BorrowBook = findViewById(R.id.btn_BorrowBook);
        btn_ReturnBook = findViewById(R.id.btn_ReturnBook);

        rl_Header = findViewById(R.id.rl_Header);
    }

    private void initEvent() {
        btn_QueryBook.setOnClickListener(this);
        btn_BorrowBook.setOnClickListener(this);
        btn_ReturnBook.setOnClickListener(this);

        LongClickUtil.setLongClick(new Handler(), rl_Header, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene_mode","manager_center");
                startActivity(intent);
                return true;
            }
        });

    }

    private void initData() {


    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_BorrowBook) {
                Intent intent = new Intent(getAppContext(), BookerIdentityVerifyWaySelectActivity.class);
                intent.putExtra("action", "borrow_book");
                startActivity(intent);
            } else if (id == R.id.btn_ReturnBook) {
                Intent intent = new Intent(getAppContext(), BookerIdentityVerifyWaySelectActivity.class);
                intent.putExtra("action", "return_book");
                startActivity(intent);
            } else if (id == R.id.btn_QueryBook) {
                Intent intent = new Intent(getAppContext(), BookerQueryBookActivity.class);
                intent.putExtra("action", "query_book");
                startActivity(intent);
            }

        }
    }
}