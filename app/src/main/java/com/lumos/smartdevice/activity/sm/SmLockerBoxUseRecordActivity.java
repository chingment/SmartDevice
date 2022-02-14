package com.lumos.smartdevice.activity.sm;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmLockerBoxUseRecordAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetLockerGetBoxUseRecords;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxUseRecords;
import com.lumos.smartdevice.model.LockerBoxUseRecordBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class SmLockerBoxUseRecordActivity  extends BaseFragmentActivity {

    private static final String TAG = "SmLockerBoxUseRecordActivity";


    private SuperRefreshLayout lv_UseRecordsRefresh;
    private RecyclerView lv_UseRecordsData;
    private int lv_UseRecords_PageIndex=0;
    private final int lv_UseRecords_PageSize=10;
    private LinearLayout ll_UseRecordsEmpty;
    private SmLockerBoxUseRecordAdapter lv_UseRecordsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_locker_box_userecord);

        setNavHeaderBtnByGoBackIsVisible(true);

        setNavHeaderTtile(R.string.aty_nav_title_smlockerboxuserecord);

        initView();
        initEvent();
        initData();
    }

    private void initView() {

        lv_UseRecordsRefresh =  findViewById(R.id.lv_UseRecordsRefresh);
        lv_UseRecordsData = findViewById(R.id.lv_UseRecordsData);
        ll_UseRecordsEmpty= findViewById(R.id.ll_UseRecordsEmpty);

        lv_UseRecordsData.setLayoutManager(new GridLayoutManager(getAppContext(), 1));

        lv_UseRecordsData.setItemAnimator(new DefaultItemAnimator());

        lv_UseRecordsAdapter = new SmLockerBoxUseRecordAdapter();


        lv_UseRecordsRefresh.setAdapter(lv_UseRecordsData, lv_UseRecordsAdapter);
        lv_UseRecordsRefresh.setOnRefreshHandler(new OnRefreshHandler() {
            @Override
            public void refresh() {
                lv_UseRecordsRefresh.setRefreshing(true);
                lv_UseRecords_PageIndex = 0;
                getUseRecords();
            }
            @Override
            public void loadMore() {
                super.loadMore();
                lv_UseRecords_PageIndex++;
                getUseRecords();
            }
        });

        lv_UseRecords_PageIndex = 0;
        getUseRecords();
    }

    private void initEvent() {

    }

    private void initData() {

    }

    private void getUseRecords(){

        RopLockerGetBoxUseRecords rop=new RopLockerGetBoxUseRecords();
        rop.setPageIndex(lv_UseRecords_PageIndex);
        rop.setPageSize(lv_UseRecords_PageSize);

        ReqInterface.getInstance().lockerGetBoxUseRecords(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmLockerBoxUseRecordActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmLockerBoxUseRecordActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetLockerGetBoxUseRecords> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetLockerGetBoxUseRecords>>() {
                });

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetLockerGetBoxUseRecords d=rt.getData();


                    int total=d.getTotal();
                    int totalPage = (total + lv_UseRecords_PageSize - 1)/lv_UseRecords_PageSize;
                    List<LockerBoxUseRecordBean> items = d.getItems();

                    if(total==0){
                        ll_UseRecordsEmpty.setVisibility(View.VISIBLE);
                    }
                    else {
                        ll_UseRecordsEmpty.setVisibility(View.GONE);
                    }

                    boolean hasMore=true;
                    if(totalPage==(lv_UseRecords_PageIndex+1)) {
                        hasMore = false;
                    }

                    if(items==null||items.size()==0) {
                        items = new ArrayList<>();
                        lv_UseRecordsRefresh.setVisibility(View.GONE);
                    }
                    else {
                        lv_UseRecordsRefresh.setVisibility(View.VISIBLE);
                    }

                    if (lv_UseRecords_PageIndex == 0) {
                        lv_UseRecordsRefresh.setRefreshing(false);
                        lv_UseRecordsAdapter.setData(items, SmLockerBoxUseRecordActivity.this);
                    }
                    else {
                        lv_UseRecordsAdapter.addData(items, SmLockerBoxUseRecordActivity.this);
                    }

                    lv_UseRecordsRefresh.loadComplete(hasMore);
                }
                else {
                    showToast(rt.getMsg());
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                super.onFailure(msg, e);
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            }
        }
    }
}