package com.lumos.smartdevice.activity.sm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerBorrowReturnOverviewActivity;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlReceiver;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlService;
import com.lumos.smartdevice.activity.booker.service.BorrowReturnFlowThread;
import com.lumos.smartdevice.activity.booker.service.MessageByAction;
import com.lumos.smartdevice.activity.booker.service.TakeStockFlowThread;
import com.lumos.smartdevice.activity.sm.adapter.SmBookerStockSlotAdapter;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmConfirm;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerStockSlots;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.HAUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmBookerTakeStockResultActivity extends SmBaseActivity {

    private static final String TAG = "SmBookerTakeStockResultActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_booker_take_stock_result);
        setNavHeaderTtile(R.string.aty_nav_title_smbookertakestockresult);
        setNavHeaderBtnByGoBackIsVisible(true);
        initView();
        initEvent();
        initData();
    }

    private void initView() {

    }

    private void initEvent() {

    }

    private void initData() {

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