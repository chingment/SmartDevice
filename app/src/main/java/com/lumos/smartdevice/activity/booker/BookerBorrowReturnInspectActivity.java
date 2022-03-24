package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.activity.booker.adapter.BookerBorrowReturnInspectSlotAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RetIdentityInfo;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
import com.lumos.smartdevice.devicectrl.BookerBorrowReturnFlowCtrl;
import com.lumos.smartdevice.model.BookerSlotVo;
import com.lumos.smartdevice.model.DeviceVo;
import com.lumos.smartdevice.model.IdentityInfoByBorrowerVo;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.ui.my.MyGridView;
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

    private DialogBookerFlowHandling dialog_BookerFlowHandling;

    private int identityType;
    private String identityId;
    private String clientUserId;

    private BookerBorrowReturnFlowCtrl bookerBorrowReturnFlowCtrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_inspect);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_inspect);
        identityType = getIntent().getIntExtra("identityType", 0);
        identityId = getIntent().getStringExtra("identityId");
        clientUserId = getIntent().getStringExtra("clientUserId");
        device = getDevice();
        slots = AppCacheManager.getBookerCustomData().getSlots();

        bookerBorrowReturnFlowCtrl = BookerBorrowReturnFlowCtrl.getInstance();

        bookerBorrowReturnFlowCtrl.setOpenHandlerListener(new BookerBorrowReturnFlowCtrl.OnOpenHandlerListener() {
            @Override
            public void onHandle(int actionCode, HashMap<String, Object> actionData, String actionRemark) {

                LogUtil.d(TAG, "actionCode:" + actionCode + ",actionRemark:" + actionRemark);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //showToast(actionRemark);
                        switch (actionCode) {
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_FLOW_START:
                                dialog_BookerFlowHandling.setTipsText("设备正在初始化");
                                dialog_BookerFlowHandling.show();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_INIT_DATA_FAILURE:
                                dialog_BookerFlowHandling.setTipsText(actionRemark);
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_OPEN_AUTH:
                                dialog_BookerFlowHandling.setTipsText("验证打开授权中");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE:
                                dialog_BookerFlowHandling.setTipsText("验证打开授权失败");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS:
                                dialog_BookerFlowHandling.setTipsText("验证打开授权成功");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_SEND_OPEN_COMMAND:
                                dialog_BookerFlowHandling.setTipsText("设备发送打开命令");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS:
                                dialog_BookerFlowHandling.setTipsText("设备发送打开命令成功");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_SEND_OPEN_COMMAND_FAILURE:
                                dialog_BookerFlowHandling.setTipsText("设备发送打开命令失败");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_OPEN_SUCCESS:
                                dialog_BookerFlowHandling.setTipsText("柜门已打开，等待关门中");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_OPEN_FAILURE:
                                dialog_BookerFlowHandling.setTipsText("柜门打开失败，请尝试再次打开");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_CLOSE_SUCCESS:
                                dialog_BookerFlowHandling.setTipsText("柜门关门成功");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_CLOSE_FAILURE:
                                dialog_BookerFlowHandling.setTipsText("柜门关失败");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_CLOSE_AUTH:
                                dialog_BookerFlowHandling.setTipsText("验证关闭授权s");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS:
                                dialog_BookerFlowHandling.setTipsText("验证关闭授权成功");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE:
                                dialog_BookerFlowHandling.setTipsText("验证关闭授权失败");
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_FLOW_END:
                                dialog_BookerFlowHandling.setTipsText("处理结束");
                                RetBookerBorrowReturn retBookerBorrowReturn = (RetBookerBorrowReturn) actionData.get("ret_booker_borrow_return");
                                Intent intent = new Intent(getAppContext(), BookerBorrowReturnOverviewActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("ret_booker_borrow_return", retBookerBorrowReturn);
                                intent.putExtras(bundle);
                                openActivity(intent);
                                finish();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_EXCEPTION:
                                dialog_BookerFlowHandling.setTipsText("设备处理异常");
                                break;
                        }
                    }
                });

            }
        });

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
    }

    public void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
        iv_SawBorrowBooks.setOnClickListener(this);
        tv_BorrowedQuantity.setOnClickListener(this);
    }

    public void initData() {

        BookerBorrowReturnInspectSlotAdapter slotAdapter = new BookerBorrowReturnInspectSlotAdapter(getAppContext(), slots);

        slotAdapter.setOnClickListener(new BookerBorrowReturnInspectSlotAdapter.OnClickListener() {
            @Override
            public void onClick(BookerSlotVo slot) {
                borrowRetrunCreateFlow(slot);
            }
        });

        gdv_Slots.setAdapter(slotAdapter);

    }


    private void borrowRetrunCreateFlow(BookerSlotVo slot ) {

        RopBookerCreateFlow rop = new RopBookerCreateFlow();
        rop.setDeviceId(device.getDeviceId());
        rop.setSlotId(slot.getSlotId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);
        rop.setType(1);

        ReqInterface.getInstance().bookerCreateFlow(rop, new ReqHandler() {

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
                ResultBean<RetBookerCreateFlow> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetBookerCreateFlow>>() {});

                if (rt.getCode() == ResultCode.SUCCESS) {
                    RetBookerCreateFlow d=rt.getData();
                    bookerBorrowReturnFlowCtrl.open(device,slot,d.getFlowId());
                } else {
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

                    tv_FullName.setText(borrower.getFullName());
                    tv_CardNo.setText(borrower.getCardNo());
                    tv_BorrowedQuantity.setText(String.valueOf(borrower.getBorrowedQuantity()));
                    tv_CanBorrowQuantity.setText(String.valueOf(borrower.getCanBorrowQuantity()));
                    tv_WilldueQuantity.setText(String.valueOf(borrower.getWilldueQuantity()));
                    tv_OverdueQuantity.setText(String.valueOf(borrower.getOverdueQuantity()));
                    tv_OverdueFine.setText(String.valueOf(borrower.getOverdueFine()));
                    tv_Status.setText(borrower.getStatus().getText());

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
            } else if (id == R.id.tv_BorrowedQuantity||id==R.id.iv_SawBorrowBooks) {
                Intent intent = new Intent(getAppContext(), BookerSawBorrowBooksActivity.class);
                intent.putExtra("action", "saw_borrow_books");
                intent.putExtra("identityType", identityType);
                intent.putExtra("identityId", identityId);
                intent.putExtra("clientUserId", clientUserId);
                openActivity(intent);
            }
        }
    }

}