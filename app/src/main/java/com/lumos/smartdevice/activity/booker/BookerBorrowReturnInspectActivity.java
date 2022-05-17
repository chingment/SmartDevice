package com.lumos.smartdevice.activity.booker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.activity.booker.adapter.BookerBorrowReturnInspectSlotAdapter;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlReceiver;
import com.lumos.smartdevice.activity.booker.service.BookerCtrlService;
import com.lumos.smartdevice.activity.booker.service.MessageByAction;
import com.lumos.smartdevice.activity.booker.service.BorrowReturnFlowThread;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetIdentityInfo;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.IdentityInfoByBorrowerVo;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.HAUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.HashMap;
import java.util.List;

public class BookerBorrowReturnInspectActivity extends BookerBaseActivity {

    private static final String TAG = "BookerBorrowReturnInspectActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;
    private MyGridView gdv_Slots;
    private TextView tv_FullName;
    private TextView tv_CardNo;
    private TextView tv_CanBorrowQuantity;
    private TextView tv_BorrowedQuantity;

    private TextView tv_WilldueQuantity;
    private TextView tv_OverdueQuantity;
    private TextView tv_OverdueFine;
    private TextView tv_Status;
    private ImageView iv_SawBorrowBooks;

    private View ll_WilldueQuantity;
    private View ll_OverdueQuantity;
    private View ll_OverdueFine;
    private View ll_Booker_Card_Info;
    private View ll_Booker_Card_Fun;
    private View btn_RenewBookByOneKey;
    private View btn_GoPayOverdueFine;

    private DeviceVo device;
    private List<BookerSlotVo> slots;
    private BookerSlotVo curSlot;
    private DialogBookerFlowHandling dialog_BookerFlowHandling;

    private int identityType;
    private String identityId;
    private String clientUserId;
    private int borrowedQuantity;
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
        setContentView(R.layout.activity_booker_borrow_return_inspect);
        LogUtil.i(TAG,"onCreate");
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_inspect);
        identityType = getIntent().getIntExtra("identityType", 0);
        identityId = getIntent().getStringExtra("identityId");
        clientUserId = getIntent().getStringExtra("clientUserId");
        device = getDevice();
        slots = AppCacheManager.getBookerCustomData().getSlots();

        Intent serviceIntent = new Intent(this, BookerCtrlService.class);
        bindService(serviceIntent, bookerCtrlServiceConnection, Context.BIND_AUTO_CREATE);

        bookerCtrlServiceReceiver = new BookerCtrlReceiver(new BookerCtrlReceiver.OnListener() {
            @Override
            public void handleMessage(MessageByAction message) {

                String actionCode = message.getActionCode();
                String actionRemark = message.getActionRemark();
                HashMap<String, Object> actionData = message.getActionData();
                LogUtil.d(TAG, "actionCode:" + actionCode + ",actionRemark:" + actionRemark);

                switch (actionCode) {
                    case BorrowReturnFlowThread.ACTION_TIPS:
                        showToast(actionRemark);
                        break;
                    case BorrowReturnFlowThread.ACTION_FLOW_START:
                        setTimerPauseByActivityFinish();
                        dialog_BookerFlowHandling.setTipsText("设备正在初始化");
                        dialog_BookerFlowHandling.show();
                        break;
                    case BorrowReturnFlowThread.ACTION_INIT_DATA_FAILURE:
                        dialog_BookerFlowHandling.setTipsText(actionRemark);
                        break;
                    case BorrowReturnFlowThread.ACTION_INIT_DATA_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("初始化数据成功");
                        break;
                    case BorrowReturnFlowThread.ACTION_OPEN_BEFORE_RFREADER_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("RDIF设备读取失败");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_OPEN_AUTH:
                        dialog_BookerFlowHandling.setTipsText("验证打开授权中");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_OPEN_AUTH_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("验证打开授权失败");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_OPEN_AUTH_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("验证打开授权成功");
                        break;
                    case BorrowReturnFlowThread.ACTION_SEND_OPEN_COMMAND:
                        dialog_BookerFlowHandling.setTipsText("设备发送打开命令");
                        break;
                    case BorrowReturnFlowThread.ACTION_SEND_OPEN_COMMAND_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("设备发送打开命令成功");
                        break;
                    case BorrowReturnFlowThread.ACTION_SEND_OPEN_COMMAND_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("设备发送打开命令失败");
                        break;
                    case BorrowReturnFlowThread.ACTION_WAIT_OPEN:
                        dialog_BookerFlowHandling.setTipsText("等待打开柜门");
                        break;
                    case BorrowReturnFlowThread.ACTION_OPEN_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("柜门已打开，等待关门中");
                        break;
                    case BorrowReturnFlowThread.ACTION_OPEN_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("柜门打开失败，请尝试再次打开");
                        break;
                    case BorrowReturnFlowThread.ACTION_CLOSE_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("柜门关门成功");
                        break;
                    case BorrowReturnFlowThread.ACTION_CLOSE_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("柜门关失败");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_CLOSE_AUTH:
                        dialog_BookerFlowHandling.setTipsText("验证关闭授权");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_CLOSE_AUTH_SUCCESS:
                        dialog_BookerFlowHandling.setTipsText("验证关闭授权成功");
                        break;
                    case BorrowReturnFlowThread.ACTION_REQUEST_CLOSE_AUTH_FAILURE:
                        dialog_BookerFlowHandling.setTipsText("验证关闭授权失败");
                        break;
                    case BorrowReturnFlowThread.ACTION_FLOW_END:
                        dialog_BookerFlowHandling.setTipsText("处理结束");
                        RetBookerBorrowReturn retBookerBorrowReturn = new RetBookerBorrowReturn();
                        retBookerBorrowReturn.setFlowId(String.valueOf(actionData.get("flowId")));
                        retBookerBorrowReturn.setBorrowBooks(HAUtil.objToList(actionData.get("borrowBooks"), BookerBookVo.class));
                        retBookerBorrowReturn.setReturnBooks(HAUtil.objToList(actionData.get("returnBooks"), BookerBookVo.class));

                        Intent intent = new Intent(getAppContext(), BookerBorrowReturnOverviewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("ret_booker_borrow_return", retBookerBorrowReturn);
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
        });

        bookerCtrlServiceReceiver.register(BookerBorrowReturnInspectActivity.this);


        initView();
        initEvent();
        initData();

        setTimerByActivityFinish(120);
    }

    public void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);

        tv_FullName = findViewById(R.id.tv_FullName);
        tv_CardNo = findViewById(R.id.tv_CardNo);
        tv_CanBorrowQuantity = findViewById(R.id.tv_CanBorrowQuantity);
        tv_BorrowedQuantity = findViewById(R.id.tv_BorrowedQuantity);
        iv_SawBorrowBooks= findViewById(R.id.iv_SawBorrowBooks);
        iv_SawBorrowBooks.setVisibility(View.VISIBLE);
        tv_WilldueQuantity= findViewById(R.id.tv_WilldueQuantity);
        tv_OverdueQuantity= findViewById(R.id.tv_OverdueQuantity);
        tv_OverdueFine= findViewById(R.id.tv_OverdueFine);
        tv_Status= findViewById(R.id.tv_Status);
        ll_WilldueQuantity= findViewById(R.id.ll_WilldueQuantity);
        ll_OverdueQuantity= findViewById(R.id.ll_OverdueQuantity);
        ll_OverdueFine= findViewById(R.id.ll_OverdueFine);
        ll_Booker_Card_Info= findViewById(R.id.ll_Booker_Card_Info);
        ll_Booker_Card_Fun= findViewById(R.id.ll_Booker_Card_Fun);
        btn_RenewBookByOneKey= findViewById(R.id.btn_RenewBookByOneKey);
        btn_GoPayOverdueFine= findViewById(R.id.btn_GoPayOverdueFine);

        gdv_Slots = findViewById(R.id.gdv_Slots);
        dialog_BookerFlowHandling = new DialogBookerFlowHandling(BookerBorrowReturnInspectActivity.this);
        dialog_BookerFlowHandling.setOnClickListener(new DialogBookerFlowHandling.OnClickListener() {
            @Override
            public void onTryAgainOpen() {
                dialog_BookerFlowHandling.stopCancleCountDownTimer();
                bookerCtrlServiceBinder.borrowReturnStart(clientUserId, identityType, identityId, device, curSlot);
            }

            @Override
            public void onCancle() {
                if( bookerCtrlServiceBinder.checkBorrowReturnIsRunning(curSlot)){
                    showToast("正在执行中，请稍后再试");
                }
                else {
                    dialog_BookerFlowHandling.hide();
                    dialog_BookerFlowHandling.stopCancleCountDownTimer();
                    setTimerStartByActivityFinish();
                }
            }
        });
    }

    public void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
        iv_SawBorrowBooks.setOnClickListener(this);
        tv_BorrowedQuantity.setOnClickListener(this);
        btn_RenewBookByOneKey.setOnClickListener(this);
        btn_GoPayOverdueFine.setOnClickListener(this);
    }

    public void initData() {

        BookerBorrowReturnInspectSlotAdapter slotAdapter = new BookerBorrowReturnInspectSlotAdapter(getAppContext(), slots);

        slotAdapter.setOnClickListener(new BookerBorrowReturnInspectSlotAdapter.OnClickListener() {
            @Override
            public void onClick(BookerSlotVo slot) {
                curSlot=slot;
                bookerCtrlServiceBinder.borrowReturnStart(clientUserId, identityType, identityId, device, slot);
            }
        });

        gdv_Slots.setAdapter(slotAdapter);

    }

    private void getIdentityInfo() {

        RopIdentityInfo rop = new RopIdentityInfo();
        rop.setDeviceId(device.getDeviceId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);
        rop.setSceneMode(device.getSceneMode());
        ReqInterface.getInstance().identityInfo(rop, new ReqHandler() {

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
                showLoading(BookerBorrowReturnInspectActivity.this);
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
                hideLoading(BookerBorrowReturnInspectActivity.this);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetIdentityInfo> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetIdentityInfo>>() {});

                if (rt.getCode() == ResultCode.SUCCESS) {
                    RetIdentityInfo d = rt.getData();

                    IdentityInfoByBorrowerVo borrower = JsonUtil.toObject(d.getInfo(),IdentityInfoByBorrowerVo.class);

                    if(borrower!=null) {
                        tv_FullName.setText(borrower.getFullName());
                        tv_CardNo.setText(borrower.getCardNo());
                        tv_BorrowedQuantity.setText(String.valueOf(borrower.getBorrowedQuantity()));
                        tv_CanBorrowQuantity.setText(String.valueOf(borrower.getCanBorrowQuantity()));
                        tv_WilldueQuantity.setText(String.valueOf(borrower.getWilldueQuantity()));
                        tv_OverdueQuantity.setText(String.valueOf(borrower.getOverdueQuantity()));
                        tv_OverdueFine.setText(String.valueOf(borrower.getOverdueFine()));
                        tv_Status.setText(borrower.getStatus().getText());
                        borrowedQuantity = borrower.getBorrowedQuantity();
                    }
//                    if(borrower.getWilldueQuantity()>0){
//                        ll_WilldueQuantity.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        ll_WilldueQuantity.setVisibility(View.INVISIBLE);
//                    }
//
//                    if(borrower.getOverdueQuantity()>0){
//                        ll_OverdueQuantity.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        ll_OverdueQuantity.setVisibility(View.INVISIBLE);
//                    }
//
//                    if(borrower.getOverdueFine()>0){
//                        ll_OverdueFine.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        ll_OverdueFine.setVisibility(View.INVISIBLE);
//                    }


                } else {
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
    protected void onResume() {
        super.onResume();

        getIdentityInfo();

    }

    @Override
    public void onDestroy() {
        if(dialog_BookerFlowHandling!=null) {
            dialog_BookerFlowHandling.cancel();
        }

        if(bookerCtrlServiceReceiver!=null){
            bookerCtrlServiceReceiver.unRegister(BookerBorrowReturnInspectActivity.this);
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

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHome) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                openActivity(intent);
                finish();
            } else if (id == R.id.tv_BorrowedQuantity||id==R.id.iv_SawBorrowBooks||id==R.id.btn_RenewBookByOneKey) {

                if(borrowedQuantity==0){
                    showToast("当前没有借阅的书本");
                }
                else {
                    Intent intent = new Intent(getAppContext(), BookerSawBorrowBooksActivity.class);
                    intent.putExtra("action", "saw_borrow_books");
                    intent.putExtra("identityType", identityType);
                    intent.putExtra("identityId", identityId);
                    intent.putExtra("clientUserId", clientUserId);
                    openActivity(intent);
                }
            }
            else if(id == R.id.btn_GoPayOverdueFine) {
                showToast("设备暂未开通支付功能");
            }
        }
    }
}