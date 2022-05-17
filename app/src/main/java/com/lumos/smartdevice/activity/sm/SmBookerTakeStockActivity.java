package com.lumos.smartdevice.activity.sm;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerBorrowReturnInspectActivity;
import com.lumos.smartdevice.activity.booker.BookerBorrowReturnOverviewActivity;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlReceiver;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlService;
import com.lumos.smartdevice.activity.booker.service.BorrowReturnFlowThread;
import com.lumos.smartdevice.activity.booker.service.MessageByAction;
import com.lumos.smartdevice.activity.sm.adapter.SmBookerStockSlotAdapter;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmConfirm;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerStockSlots;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.vo.BookerStockSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class SmBookerTakeStockActivity extends SmBaseActivity {

    private static final String TAG = "SmBookerTakeStockActivity";

    private DialogSmConfirm dialog_Confirm;

    private View et_StockSlots;
    private SuperRefreshLayout sf_StockSlots;
    private RecyclerView rv_StockSlots;
    private int rv_StockSlots_PageNum=1;
    private final int rv_StockSlots_PageSize=10;
    private SmBookerStockSlotAdapter rv_StockSlots_Adapter;

    private DeviceVo device;

    private BookerCtrlReceiver bookerCtrlServiceReceiver;

    private BookerCtrlService.CtrlBinder bookerCtrlServiceBinder;

    private final ServiceConnection bookerCtrlServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bookerCtrlServiceBinder = (BookerCtrlService.CtrlBinder) binder;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_booker_take_stock);
        setNavHeaderTtile(R.string.aty_nav_title_smbookertakestock);
        setNavHeaderBtnByGoBackIsVisible(true);
        device=getDevice();
        initView();
        initEvent();
        initData();
    }

    private void initView() {

        dialog_Confirm=new DialogSmConfirm(SmBookerTakeStockActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new DialogSmConfirm.OnClickListener() {
            @Override
            public void onSure() {
                String fun = dialog_Confirm.getFunction();

                switch (fun) {
                    case "take_stock":
                        dlgTakeStock();
                        break;
                }
                dialog_Confirm.hide();
            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });

        Intent serviceIntent = new Intent(this, BookerCtrlService.class);
        bindService(serviceIntent, bookerCtrlServiceConnection, Context.BIND_AUTO_CREATE);

        bookerCtrlServiceReceiver = new BookerCtrlReceiver(new BookerCtrlReceiver.OnListener() {
            @Override
            public void handleMessage(MessageByAction message) {


            }
        });

        bookerCtrlServiceReceiver.register(SmBookerTakeStockActivity.this);

        sf_StockSlots =  findViewById(R.id.sf_StockSlots);
        rv_StockSlots = findViewById(R.id.rv_StockSlots);
        et_StockSlots= findViewById(R.id.et_StockSlots);

        rv_StockSlots.setLayoutManager(new GridLayoutManager(getAppContext(), 1));
        rv_StockSlots.setItemAnimator(new DefaultItemAnimator());

        rv_StockSlots_Adapter = new SmBookerStockSlotAdapter();
        rv_StockSlots_Adapter.setOnClickListener(new SmBookerStockSlotAdapter.OnClickListener() {
            @Override
            public void onStockTake(BookerStockSlotVo item) {
                dialog_Confirm.setFunction("take_stock");
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTag(item);
                dialog_Confirm.setTipsText("是否进行盘点？");
                dialog_Confirm.show();
            }
        });

        sf_StockSlots.setAdapter(rv_StockSlots, rv_StockSlots_Adapter);
        sf_StockSlots.setOnRefreshHandler(new OnRefreshHandler() {
            @Override
            public void refresh() {
                sf_StockSlots.setRefreshing(true);
                rv_StockSlots_PageNum = 1;
                getStockSlots();
            }
            @Override
            public void loadMore() {
                super.loadMore();
                rv_StockSlots_PageNum++;
                getStockSlots();
            }
        });

        rv_StockSlots_PageNum = 1;
    }

    private void initEvent() {

    }

    private void initData() {
        this.getStockSlots();
    }

    private void getStockSlots(){

        RopBookerStockSlots rop=new RopBookerStockSlots();
        rop.setDeviceId(device.getDeviceId());
        rop.setPageNum(rv_StockSlots_PageNum);
        rop.setPageSize(rv_StockSlots_PageSize);

        ReqInterface.getInstance().bookerStockStocks(rop, new ReqHandler(){

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(SmBookerTakeStockActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(SmBookerTakeStockActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);

                ResultBean<RetBookerStockSlots> rt = JsonUtil.toResult(response, new TypeReference<ResultBean<RetBookerStockSlots>>(){});

                if(rt.getCode()== ResultCode.SUCCESS) {

                    RetBookerStockSlots d=rt.getData();


                    int totalPages = d.getTotalPages();

                    List<BookerStockSlotVo> items = d.getItems();


                    if(d.getTotalSize()==0){
                        et_StockSlots.setVisibility(View.VISIBLE);
                    }
                    else {
                        et_StockSlots.setVisibility(View.GONE);
                    }

                    boolean hasMore = true;
                    if (totalPages == rv_StockSlots_PageNum) {
                        hasMore = false;
                    }

                    if (items == null || items.size() == 0) {
                        items = new ArrayList<>();
                        sf_StockSlots.setVisibility(View.GONE);
                    } else {
                        sf_StockSlots.setVisibility(View.VISIBLE);
                    }

                    if (rv_StockSlots_PageNum == 1) {
                        sf_StockSlots.setRefreshing(false);
                        rv_StockSlots_Adapter.setData(items, SmBookerTakeStockActivity.this);
                    } else {

                        rv_StockSlots_Adapter.addData(items, SmBookerTakeStockActivity.this);

                    }
                    sf_StockSlots.loadComplete(hasMore);

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

    private void dlgTakeStock() {
        bookerCtrlServiceBinder.takeStock(device, null);
    }

    @Override
    public void onDestroy() {
        if(dialog_Confirm!=null) {
            dialog_Confirm.cancel();
        }

        if(bookerCtrlServiceReceiver!=null){
            bookerCtrlServiceReceiver.unRegister(SmBookerTakeStockActivity.this);
        }

        if(bookerCtrlServiceConnection!=null){
            unbindService(bookerCtrlServiceConnection);
        }

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