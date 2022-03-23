package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerConfirm;
import com.lumos.smartdevice.adapter.BookerBorrowBookAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerSawBorrowBooks;
import com.lumos.smartdevice.api.rop.RopBookerRenewBooks;
import com.lumos.smartdevice.api.rop.RopBookerSawBorrowBooks;
import com.lumos.smartdevice.model.BookerBorrowBookVo;
import com.lumos.smartdevice.model.DeviceVo;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;


public class BookerSawBorrowBooksActivity extends BookerBaseActivity {
    private static final String TAG = "BookerBorrowedBookActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    private DialogBookerConfirm dialog_Confirm;
    private SuperRefreshLayout sf_BorrowedBooks;
    private RecyclerView rv_BorrowedBooks;
    private int rv_BorrowedBooks_PageNum=1;
    private final int rv_BorrowedBooks_PageSize=10;
    private BookerBorrowBookAdapter rv_BorrowedBooksAdapter;

    private DeviceVo device;
    private int identityType;
    private String identityId;
    private String clientUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_saw_borrow_books);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrowed_book);

        device = getDevice();
        identityType = getIntent().getIntExtra("identityType", 0);
        identityId = getIntent().getStringExtra("identityId");
        clientUserId = getIntent().getStringExtra("clientUserId");

        initView();
        initEvent();
        initData();
    }

    private void initView(){

        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        dialog_Confirm = new DialogBookerConfirm(BookerSawBorrowBooksActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new DialogBookerConfirm.OnClickListener() {
            @Override
            public void onSure() {

                dialog_Confirm.hide();

                String fun = dialog_Confirm.getFunction();

                switch (fun) {
                    case "renew":
                        BookerBorrowBookVo item = (BookerBorrowBookVo) dialog_Confirm.getTag();

                        List<String> borrowIds = new ArrayList<>();
                        borrowIds.add(item.getBorrowId());
                        renewBooks("multi", borrowIds);
                        break;
                }

            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });

        sf_BorrowedBooks =  findViewById(R.id.sf_BorrowedBooks);
        rv_BorrowedBooks = findViewById(R.id.rv_BorrowedBooks);

        rv_BorrowedBooks.setLayoutManager(new GridLayoutManager(getAppContext(), 1));

        rv_BorrowedBooks.setItemAnimator(new DefaultItemAnimator());

        rv_BorrowedBooksAdapter = new BookerBorrowBookAdapter();
        rv_BorrowedBooksAdapter.setOnClickListener(new BookerBorrowBookAdapter.OnClickListener() {
            @Override
            public void onRenew(BookerBorrowBookVo item) {
                dialog_Confirm.setFunction("renew");
                dialog_Confirm.setTag(item);
                dialog_Confirm.setTipsImageByNetwork(item.getSkuImgUrl());
                dialog_Confirm.setTipsText("是否续借该书本？");
                dialog_Confirm.show();

            }
        });
        sf_BorrowedBooks.setAdapter(rv_BorrowedBooks, rv_BorrowedBooksAdapter);
        sf_BorrowedBooks.setOnRefreshHandler(new OnRefreshHandler() {
            @Override
            public void refresh() {
                sf_BorrowedBooks.setRefreshing(true);
                rv_BorrowedBooks_PageNum = 1;
                getBorrowedBooks();
            }
            @Override
            public void loadMore() {
                super.loadMore();
                rv_BorrowedBooks_PageNum++;
                getBorrowedBooks();
            }
        });



    }

    private void initEvent(){
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
    }

    private void initData() {

        getBorrowedBooks();
    }

    private void getBorrowedBooks(){

        RopBookerSawBorrowBooks rop=new RopBookerSawBorrowBooks();
        rop.setDeviceId(device.getDeviceId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityId(identityId);
        rop.setIdentityType(identityType);
        rop.setPageNum(rv_BorrowedBooks_PageNum);
        rop.setPageSize(rv_BorrowedBooks_PageSize);

        ReqInterface.getInstance().bookerSawBorrowBooks(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(BookerSawBorrowBooksActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(BookerSawBorrowBooksActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetBookerSawBorrowBooks> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerSawBorrowBooks>>() {
                });

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetBookerSawBorrowBooks d = rt.getData();

                    int totalPages = d.getTotalPages();

                    List<BookerBorrowBookVo> items = d.getItems();

//
//                    if(total==0){
//                        ll_UseRecordsEmpty.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        ll_UseRecordsEmpty.setVisibility(View.GONE);
//                    }

                    boolean hasMore = true;
                    if (totalPages == rv_BorrowedBooks_PageNum) {
                        hasMore = false;
                    }

                    if (items == null || items.size() == 0) {
                        items = new ArrayList<>();
                        sf_BorrowedBooks.setVisibility(View.GONE);
                    } else {
                        sf_BorrowedBooks.setVisibility(View.VISIBLE);
                    }

                    if (rv_BorrowedBooks_PageNum == 1) {
                        sf_BorrowedBooks.setRefreshing(false);
                        rv_BorrowedBooksAdapter.setData(items, BookerSawBorrowBooksActivity.this);
                    } else {

                        rv_BorrowedBooksAdapter.addData(items, BookerSawBorrowBooksActivity.this);

                    }
                    sf_BorrowedBooks.loadComplete(hasMore);


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


    private void renewBooks(String actionCode,List<String> borrowIds){

        RopBookerRenewBooks rop=new RopBookerRenewBooks();
        rop.setDeviceId(device.getDeviceId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityId(identityId);
        rop.setIdentityType(identityType);
        rop.setActionCode(actionCode);
        rop.setBorrowIds(borrowIds);


        ReqInterface.getInstance().bookerRenewBooks(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(BookerSawBorrowBooksActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(BookerSawBorrowBooksActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<Object> rt = JSON.parseObject(response, new TypeReference<ResultBean<Object>>() {
                });

                showToast(rt.getMsg());

                if(rt.getCode()== ResultCode.SUCCESS) {
                    getBorrowedBooks();
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
    protected void onDestroy() {

        if(dialog_Confirm!=null) {
            dialog_Confirm.cancel();
        }

        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHome) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                openActivity(intent);
                finish();
            }
        }
    }
}