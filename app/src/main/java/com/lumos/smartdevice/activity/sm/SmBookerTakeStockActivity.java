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
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerStockSlots;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.UserVo;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.ui.refreshview.OnRefreshHandler;
import com.lumos.smartdevice.ui.refreshview.SuperRefreshLayout;
import com.lumos.smartdevice.utils.HAUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmBookerTakeStockActivity extends SmBaseActivity {

    private static final String TAG = "SmBookerTakeStockActivity";

    private View et_StockSlots;
    private SuperRefreshLayout sf_StockSlots;
    private RecyclerView rv_StockSlots;
    private int rv_StockSlots_PageNum=1;
    private final int rv_StockSlots_PageSize=10;
    private SmBookerStockSlotAdapter rv_StockSlots_Adapter;
    private DialogSmConfirm dialog_Confirm;

    private DeviceVo device;
    private List<BookerSlotVo> slots;
    private BookerSlotVo curSlot;
    private DialogBookerFlowHandling dialog_BookerFlowHandling;


    private BookerCtrlReceiver bookerCtrlServiceReceiver;

    private BookerCtrlService.CtrlBinder bookerCtrlServiceBinder;

    private UserVo currentUser;

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
        device = getDevice();
        initView();
        initEvent();
        initData();

        currentUser = AppCacheManager.getCurrentUser();
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
                    case "open_door":
                        dlgOpenDoor();
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

                int flowId = message.getFlowType();
                String actionCode = message.getActionCode();
                String actionRemark = message.getActionRemark();
                HashMap<String, Object> actionData = message.getActionData();
                LogUtil.d(TAG, "actionCode:" + actionCode + ",actionRemark:" + actionRemark);

                if(flowId==2) {
                    switch (actionCode) {
                        case TakeStockFlowThread.ACTION_TIPS:
                            showToast(actionRemark);
                            break;
                        case TakeStockFlowThread.ACTION_FLOW_START:
                            setTimerPauseByActivityFinish();
                            dialog_BookerFlowHandling.setTipsText("设备正在初始化");
                            dialog_BookerFlowHandling.show();
                            break;
                        case TakeStockFlowThread.ACTION_INIT_DATA_FAILURE:
                            dialog_BookerFlowHandling.setTipsText(actionRemark);
                            break;
                        case TakeStockFlowThread.ACTION_INIT_DATA_SUCCESS:
                            dialog_BookerFlowHandling.setTipsText("初始化数据成功");
                            break;
                        case TakeStockFlowThread.ACTION_CHECK_DOOR_STATUS:
                            dialog_BookerFlowHandling.setTipsText("检查柜门状态");
                            break;
                        case TakeStockFlowThread.ACTION_CHECK_DOOR_STATUS_SUCCESS:
                            dialog_BookerFlowHandling.setTipsText("检查柜门状态成功");
                            break;
                        case TakeStockFlowThread.ACTION_CHECK_DOOR_STATUS_FAILURE:
                            dialog_BookerFlowHandling.setTipsText("检查柜门状态失败");
                            break;
                        case TakeStockFlowThread.ACTION_DOOR_OPEN:
                            dialog_BookerFlowHandling.setTipsText("柜门状态已打开");
                            break;
                        case TakeStockFlowThread.ACTION_DOOR_CLOSE:
                            dialog_BookerFlowHandling.setTipsText("柜门状态已关闭");
                            break;
                        case TakeStockFlowThread.ACTION_WAIT_RFREADER:
                            dialog_BookerFlowHandling.setTipsText("等待检阅数量");
                            break;
                        case TakeStockFlowThread.ACTION_RFREADER_SUCCESS:
                            dialog_BookerFlowHandling.setTipsText("检阅成功");
                            break;
                        case TakeStockFlowThread.ACTION_RFREADER_FAILURE:
                            dialog_BookerFlowHandling.setTipsText("检阅失败");
                            break;
                        case TakeStockFlowThread.ACTION_TAKESTOCK_SUCCESS:
                            dialog_BookerFlowHandling.setTipsText("盘点成功");
                            break;
                        case TakeStockFlowThread.ACTION_TAKESTOCK_FAILURE:
                            dialog_BookerFlowHandling.setTipsText("盘点失败");
                            break;
                        case TakeStockFlowThread.ACTION_FLOW_END:
                            dialog_BookerFlowHandling.setTipsText("处理结束");

                            RetBookerTakeStock retBookerTakeStock = new RetBookerTakeStock();
                            retBookerTakeStock.setFlowId(String.valueOf(actionData.get("flowId")));
                            retBookerTakeStock.setSheetId(String.valueOf(actionData.get("sheetId")));
                            retBookerTakeStock.setSheetItems(HAUtil.objToList(actionData.get("sheetItems"), BookerBookVo.class));
                            retBookerTakeStock.setWarnItems(HAUtil.objToList(actionData.get("warnItems"), BookerBookVo.class));
                            Intent intent = new Intent(getAppContext(), SmBookerTakeStockResultActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ret_booker_take_stock", retBookerTakeStock);
                            intent.putExtras(bundle);
                            openActivity(intent);
                            finish();
                            break;
                        case BorrowReturnFlowThread.ACTION_EXCEPTION:
                            dialog_BookerFlowHandling.setTipsText("设备处理异常");
                            break;
                    }

                    if (actionCode.contains("failure") || actionCode.contains("exception")) {
                        dialog_BookerFlowHandling.startCancleCountDownTimer();
                    }
                }
                else{
                    switch (actionCode) {
                        case TakeStockFlowThread.ACTION_TIPS:
                            showToast(actionRemark);
                            break;
                        case TakeStockFlowThread.ACTION_FLOW_START:
                            showLoading(SmBookerTakeStockActivity.this,1800);
                            break;
                        case TakeStockFlowThread.ACTION_FLOW_END:

                            break;
                    }

                    if (actionCode.contains("failure") || actionCode.contains("exception")) {
                        hideLoading(SmBookerTakeStockActivity.this);
                        showToast(actionRemark);
                    }

                }

            }
        });

        bookerCtrlServiceReceiver.register(SmBookerTakeStockActivity.this);

        dialog_BookerFlowHandling = new DialogBookerFlowHandling(SmBookerTakeStockActivity.this);
        dialog_BookerFlowHandling.setOnClickListener(new DialogBookerFlowHandling.OnClickListener() {
            @Override
            public void onTryAgainOpen() {
                dialog_BookerFlowHandling.stopCancleCountDownTimer();
                bookerCtrlServiceBinder.takeStock(currentUser.getUserId(),3,currentUser.getUserId(), device, curSlot);
            }

            @Override
            public void onCancle() {
                if( bookerCtrlServiceBinder.checkTakeStockIsRunning(curSlot)){
                    showToast("正在执行中，请稍后再试");
                }
                else {
                    dialog_BookerFlowHandling.hide();
                    dialog_BookerFlowHandling.stopCancleCountDownTimer();
                    setTimerStartByActivityFinish();
                }
            }
        });

        sf_StockSlots =  findViewById(R.id.sf_StockSlots);
        rv_StockSlots = findViewById(R.id.rv_StockSlots);
        et_StockSlots= findViewById(R.id.et_StockSlots);

        rv_StockSlots.setLayoutManager(new GridLayoutManager(getAppContext(), 1));
        rv_StockSlots.setItemAnimator(new DefaultItemAnimator());

        rv_StockSlots_Adapter = new SmBookerStockSlotAdapter();
        rv_StockSlots_Adapter.setOnClickListener(new SmBookerStockSlotAdapter.OnClickListener() {
            @Override
            public void onTakeStock(BookerSlotVo item) {
                dialog_Confirm.setFunction("take_stock");
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTag(item);
                dialog_Confirm.setTipsText("是否进行盘点？");
                dialog_Confirm.show();
            }

            @Override
            public void onOpenDoor(BookerSlotVo item){
                dialog_Confirm.setFunction("open_door");
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTag(item);
                dialog_Confirm.setTipsText("是否打开柜门？");
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

                    List<BookerSlotVo> items = d.getItems();


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
        curSlot = (BookerSlotVo) dialog_Confirm.getTag();
        bookerCtrlServiceBinder.takeStock(currentUser.getUserId(),3,currentUser.getUserId(), device, curSlot);
    }

    private void dlgOpenDoor(){
        curSlot = (BookerSlotVo) dialog_Confirm.getTag();
        bookerCtrlServiceBinder.openDoor(currentUser.getUserId(),3,currentUser.getUserId(), device, curSlot);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getStockSlots();
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