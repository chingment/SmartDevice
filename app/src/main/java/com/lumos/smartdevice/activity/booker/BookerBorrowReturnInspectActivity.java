package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.DialogBookerFlowHandling;
import com.lumos.smartdevice.adapter.BookerBorrowReturnInspectCabinetBoxAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturnCloseAction;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturnCreateFlow;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturnOpenAction;
import com.lumos.smartdevice.api.rop.RetIdentityInfo;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnCloseAction;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnOpenAction;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetSlotBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.IdentityInfoByBorrowerBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BookerBorrowReturnInspectActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerBorrowReturnInspectActivity";

    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;
    private MyGridView gdv_Slots;
    private TextView tv_SignName;
    private TextView tv_CardNo;
    private TextView tv_CanBorrowQuantity;
    private TextView tv_BorrowedQuantity;

    private DeviceBean device;

    private List<CabinetSlotBean> cabinetSlots=new ArrayList<>();

    private DialogBookerFlowHandling dialog_BookerFlowHandling;


    private int identityType=2;
    private String identityId="1";
    private String clientUserId="c89cae062f9b4b098687969fee260000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_inspect);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_inspect);
        device=getDevice();

        HashMap<String, CabinetBean> l_Cabinets=device.getCabinets();
        if(l_Cabinets!=null) {

            List<CabinetBean> cabinets = new ArrayList<>(l_Cabinets.values());

            Collections.sort(cabinets, new Comparator<CabinetBean>() {
                @Override
                public int compare(CabinetBean t1, CabinetBean t2) {
                    return t2.getPriority() - t1.getPriority();
                }
            });

            for (CabinetBean cabinet: cabinets ) {
                String layout=cabinet.getLayout();
                CabinetLayoutBean cabinetLayout = JSON.parseObject(layout, new TypeReference<CabinetLayoutBean>() {
                });
                if (cabinetLayout != null) {
                    List<String> cabinetCells = cabinetLayout.getCells();
                    if(cabinetCells!=null) {
                        for (String cabinetCell : cabinetCells) {

                            String[] cell_prams = cabinetCell.split("-");

                            String id = cell_prams[0];
                            String plate = cell_prams[1];
                            String name = cell_prams[2];

                            CabinetSlotBean cabinetSlot = new CabinetSlotBean();
                            cabinetSlot.setCabinetId(cabinet.getCabinetId());
                            cabinetSlot.setSlotId(id);
                            cabinetSlot.setSlotName(name);
                            cabinetSlot.setSlotPlate(plate);

                            cabinetSlots.add(cabinetSlot);

                        }
                    }

                }

            }


        }

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        tv_SignName = findViewById(R.id.tv_SignName);
        tv_CardNo = findViewById(R.id.tv_CardNo);
        tv_CanBorrowQuantity = findViewById(R.id.tv_CanBorrowQuantity);
        tv_BorrowedQuantity = findViewById(R.id.tv_BorrowedQuantity);
        gdv_Slots = findViewById(R.id.gdv_Slots);
        dialog_BookerFlowHandling = new DialogBookerFlowHandling(BookerBorrowReturnInspectActivity.this);
    }

    private void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);

        dialog_BookerFlowHandling.setOnClickListener(new DialogBookerFlowHandling.OnClickListener() {
            @Override
            public void testOpen(String flowId) {

                RopBookerBorrowReturnOpenAction rop=new RopBookerBorrowReturnOpenAction();
                rop.setDeviceId(device.getDeviceId());
                rop.setFlowId(flowId);
                rop.setActionCode("1000");
                rop.setActionResult(1);

                List<String> rfIds=new ArrayList<>();
                rfIds.add("31");
                rfIds.add("32");
                rfIds.add("33");
                rfIds.add("34");
                rfIds.add("35");

                rop.setRfIds(rfIds);
                ReqInterface.getInstance().bookerBorrowReturnOpenAction(rop, new ReqHandler(){

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
                        ResultBean<RetBookerBorrowReturnOpenAction> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturnOpenAction>>() {
                        });

                        if(rt.getCode()== ResultCode.SUCCESS) {



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
            public void testClose(String flowId) {


                RopBookerBorrowReturnCloseAction rop=new RopBookerBorrowReturnCloseAction();
                rop.setDeviceId(device.getDeviceId());
                rop.setFlowId(flowId);
                rop.setActionCode("2000");
                rop.setActionResult(1);

                List<String> rfIds=new ArrayList<>();
                rfIds.add("31");
                rfIds.add("32");
                rfIds.add("33");
                //rfIds.add("34");
                //rfIds.add("35");

                rop.setRfIds(rfIds);

                ReqInterface.getInstance().bookerBorrowReturnCloseAction(rop, new ReqHandler(){

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
                        ResultBean<RetBookerBorrowReturnCloseAction> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturnCloseAction>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {

                            Intent intent = new Intent(BookerBorrowReturnInspectActivity.this, BookerBorrowReturnOverviewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ret_booker_borrow_return_close_action", rt.getData());
                            intent.putExtras(bundle);
                            openActivity(intent);
                            finish();


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
        });
    }

    private void initData() {

        BookerBorrowReturnInspectCabinetBoxAdapter cabinetBoxAdapter = new BookerBorrowReturnInspectCabinetBoxAdapter(getAppContext(), cabinetSlots);

        cabinetBoxAdapter.setOnClickListener(new BookerBorrowReturnInspectCabinetBoxAdapter.OnClickListener() {
            @Override
            public void onClick(CabinetSlotBean cabinetSlot) {


                RopBookerBorrowReturnCreateFlow rop=new RopBookerBorrowReturnCreateFlow();
                rop.setDeviceId(device.getDeviceId());
                rop.setCabinetId(cabinetSlot.getCabinetId());
                rop.setSlotId(cabinetSlot.getSlotId());
                rop.setClientUserId(clientUserId);
                rop.setIdentityType(identityType);
                rop.setIdentityId(identityId);


                ReqInterface.getInstance().bookerBorrowReturnCreateFlow(rop, new ReqHandler(){

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
                        ResultBean<RetBookerBorrowReturnCreateFlow> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturnCreateFlow>>() {
                        });

                        if(rt.getCode()== ResultCode.SUCCESS) {

                            RetBookerBorrowReturnCreateFlow d = rt.getData();
                            dialog_BookerFlowHandling.setFlowId(d.getFlowId());
                            dialog_BookerFlowHandling.show();

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
        });

        gdv_Slots.setAdapter(cabinetBoxAdapter);

        getIdentityInfo();
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

                    tv_SignName.setText(borrower.getSignName());
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