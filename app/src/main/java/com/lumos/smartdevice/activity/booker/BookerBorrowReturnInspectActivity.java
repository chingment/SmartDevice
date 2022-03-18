package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.adapter.BookerBorrowReturnInspectSlotAdapter;
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
import com.lumos.smartdevice.model.BookerSlotBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.IdentityInfoByBorrowerBean;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.HashMap;
import java.util.List;

public class BookerBorrowReturnInspectActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerBorrowReturnInspectActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;
    private MyGridView gdv_Slots;
    private TextView tv_FullName;
    private TextView tv_CardNo;
    private TextView tv_CanBorrowQuantity;
    private TextView tv_BorrowedQuantity;

    private DeviceBean device;

    private List<BookerSlotBean> slots;

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
                                dialog_BookerFlowHandling.show();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_INIT_DATA_FAILURE:
                                //dialog_BookerFlowHandling.hide();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE:
                                //dialog_BookerFlowHandling.hide();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_SEND_OPEN_COMMAND_FAILURE:
                                //dialog_BookerFlowHandling.hide();
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_CLOSE_FAILURE:
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE:
                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_FLOW_END:

                                RetBookerBorrowReturn retBookerBorrowReturn = (RetBookerBorrowReturn) actionData.get("ret_booker_borrow_return");

                                Intent intent = new Intent(getAppContext(), BookerBorrowReturnOverviewActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("ret_booker_borrow_return", retBookerBorrowReturn);
                                intent.putExtras(bundle);
                                openActivity(intent);
                                finish();

                                break;
                            case BookerBorrowReturnFlowCtrl.ACTION_CODE_EXCEPTION:
                                break;
                        }
                    }
                });

            }
        });

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        tv_FullName = findViewById(R.id.tv_FullName);
        tv_CardNo = findViewById(R.id.tv_CardNo);
        tv_CanBorrowQuantity = findViewById(R.id.tv_CanBorrowQuantity);
        tv_BorrowedQuantity = findViewById(R.id.tv_BorrowedQuantity);
        gdv_Slots = findViewById(R.id.gdv_Slots);
        dialog_BookerFlowHandling = new DialogBookerFlowHandling(BookerBorrowReturnInspectActivity.this);
    }

    private void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
    }

    private void initData() {

        BookerBorrowReturnInspectSlotAdapter slotAdapter = new BookerBorrowReturnInspectSlotAdapter(getAppContext(), slots);

        slotAdapter.setOnClickListener(new BookerBorrowReturnInspectSlotAdapter.OnClickListener() {
            @Override
            public void onClick(BookerSlotBean slot) {
                borrowRetrunCreateFlow(slot);
            }
        });

        gdv_Slots.setAdapter(slotAdapter);

        getIdentityInfo();
    }


    private void borrowRetrunCreateFlow(BookerSlotBean slot ) {

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
                ResultBean<RetBookerCreateFlow> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerCreateFlow>>() {
                });

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
                ResultBean<RetIdentityInfo> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetIdentityInfo>>() {
                });

                if (rt.getCode() == ResultCode.SUCCESS) {
                    RetIdentityInfo d = rt.getData();

                    IdentityInfoByBorrowerBean borrower = JSON.parseObject(JSON.toJSONString(d.getInfo()), IdentityInfoByBorrowerBean.class);

                    tv_FullName.setText(borrower.getFullName());
                    tv_CardNo.setText(borrower.getCardNo());
                    tv_BorrowedQuantity.setText(String.valueOf(borrower.getBorrowedQuantity()));
                    tv_CanBorrowQuantity.setText(String.valueOf(borrower.getCanBorrowQuantity()));

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
    public void onDestroy() {
        super.onDestroy();

        dialog_BookerFlowHandling.cancel();

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