package com.lumos.smartdevice.activity.sm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerBorrowReturnOverviewActivity;
import com.lumos.smartdevice.activity.booker.adapter.BookerBorrowReturnBookAdapter;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlReceiver;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlService;
import com.lumos.smartdevice.activity.booker.service.BorrowReturnFlowThread;
import com.lumos.smartdevice.activity.booker.service.MessageByAction;
import com.lumos.smartdevice.activity.booker.service.TakeStockFlowThread;
import com.lumos.smartdevice.activity.sm.adapter.SmBookerStockSlotAdapter;
import com.lumos.smartdevice.activity.sm.adapter.SmBookerTakeStockBookAdapter;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmConfirm;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerStockInbound;
import com.lumos.smartdevice.api.rop.RetBookerStockSlots;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RetOwnLogin;
import com.lumos.smartdevice.api.rop.RopBookerStockInbound;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.UserVo;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.ui.my.MyListView;
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

    private DialogSmConfirm dialog_Confirm;
    private MyListView lv_SheetItems;
    private TextView tv_SheetItemsCount;
    private MyListView lv_WarnItems;
    private TextView tv_WarnItemsCount;
    private Button btn_StockInbound;
    private DeviceVo device;
    private RetBookerTakeStock retBookerTakeStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_booker_take_stock_result);
        setNavHeaderTtile(R.string.aty_nav_title_smbookertakestockresult);
        setNavHeaderBtnByGoBackIsVisible(true);
        device = getDevice();
        retBookerTakeStock = (RetBookerTakeStock) getIntent().getSerializableExtra("ret_booker_take_stock");


        initView();
        initEvent();
        initData();
    }

    private void initView() {

        lv_SheetItems = findViewById(R.id.lv_SheetItems);
        tv_SheetItemsCount = findViewById(R.id.tv_SheetItemsCount);
        lv_WarnItems = findViewById(R.id.lv_WarnItems);
        tv_WarnItemsCount = findViewById(R.id.tv_WarnItemsCount);
        btn_StockInbound= findViewById(R.id.btn_StockInbound);
        dialog_Confirm=new DialogSmConfirm(SmBookerTakeStockResultActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new DialogSmConfirm.OnClickListener() {
            @Override
            public void onSure() {
                String fun = dialog_Confirm.getFunction();

                switch (fun) {
                    case "stock_inbound":
                        dlgStockInbound();
                        break;
                }
                dialog_Confirm.hide();
            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });
    }

    private void initEvent() {
        btn_StockInbound.setOnClickListener(this);
    }

    private void initData() {

        List<BookerBookVo> sheetItems = retBookerTakeStock.getSheetItems();
        List<BookerBookVo> warnItems = retBookerTakeStock.getWarnItems();

        tv_SheetItemsCount.setText(String.valueOf(sheetItems.size()));
        SmBookerTakeStockBookAdapter borrowBooksAdapter=new SmBookerTakeStockBookAdapter(SmBookerTakeStockResultActivity.this,sheetItems);
        lv_SheetItems.setFocusable(false);
        lv_SheetItems.setClickable(false);
        lv_SheetItems.setPressed(false);
        lv_SheetItems.setEnabled(false);
        lv_SheetItems.setAdapter(borrowBooksAdapter);

        tv_WarnItemsCount.setText(String.valueOf(warnItems.size()));
        SmBookerTakeStockBookAdapter returnBooksAdapter=new SmBookerTakeStockBookAdapter(SmBookerTakeStockResultActivity.this,warnItems);
        lv_WarnItems.setFocusable(false);
        lv_WarnItems.setClickable(false);
        lv_WarnItems.setPressed(false);
        lv_WarnItems.setEnabled(false);
        lv_WarnItems.setAdapter(returnBooksAdapter);

    }

    private void dlgStockInbound(){

        RopBookerStockInbound rop=new RopBookerStockInbound();
        rop.setDeviceId(device.getDeviceId());
        rop.setSheetId(retBookerTakeStock.getSheetId());
        ReqInterface.getInstance().bookerStockInbound(rop, new ReqHandler(){
            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmBookerTakeStockResultActivity.this);
            }
            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmBookerTakeStockResultActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetBookerStockInbound> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetBookerStockInbound>>() {});

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetBookerStockInbound d=rt.getData();
                    finish();
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
            } else if (id == R.id.btn_StockInbound) {
                dialog_Confirm.setFunction("stock_inbound");
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTipsText("是否进行盘点入库？");
                dialog_Confirm.show();
            }
        }
    }
}