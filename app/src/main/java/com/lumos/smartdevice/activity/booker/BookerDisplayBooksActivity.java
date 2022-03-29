package com.lumos.smartdevice.activity.booker;


import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.adapter.BookerBorrowReturnInspectSlotAdapter;
import com.lumos.smartdevice.activity.booker.adapter.BookerDisplayBookAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerDisplayBooks;
import com.lumos.smartdevice.api.rop.RetBookerSawBorrowBooks;
import com.lumos.smartdevice.api.rop.RopBookerDisplayBooks;
import com.lumos.smartdevice.api.rop.RopBookerSawBorrowBooks;
import com.lumos.smartdevice.api.vo.BookerBorrowBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerDisplayBooksActivity extends BookerBaseActivity {

    private static final String TAG = "BookerQueryBookActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    private MyGridView gdv_DisplayBooks;

    private int rv_DisplayBooks_PageNum=1;
    private final int rv_DisplayBooks_PageSize=10;

    private DeviceVo device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_display_books);
        setNavHeaderTtile(R.string.aty_bookermain_query);
        device = getDevice();
        initView();
        initEvent();
        initData();
        setTimerByActivityFinish(120);
    }

    public void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        gdv_DisplayBooks = findViewById(R.id.gdv_DisplayBooks);
    }

    public void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
    }

    public void initData() {
        getDisplayBooks();
    }

    private void getDisplayBooks(){

        RopBookerDisplayBooks rop=new RopBookerDisplayBooks();
        rop.setDeviceId(device.getDeviceId());
        rop.setPageNum(rv_DisplayBooks_PageNum);
        rop.setPageSize(rv_DisplayBooks_PageSize);

        ReqInterface.getInstance().bookerDisplayBooks(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(BookerDisplayBooksActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(BookerDisplayBooksActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetBookerDisplayBooks> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetBookerDisplayBooks>>() {});

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetBookerDisplayBooks d = rt.getData();


                    BookerDisplayBookAdapter gdv_DisplayBooks_Adapter = new BookerDisplayBookAdapter(getAppContext(), d.getItems());


                    gdv_DisplayBooks.setAdapter(gdv_DisplayBooks_Adapter);


                }
                else {
                    showToast(rt.getMsg());
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                super.onFailure(msg, e);
                showToast(msg);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHome) {
                finish();
            }
        }
    }


}