package com.lumos.smartdevice.activity.scenebooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.CustomDialogBookerBorrowReturnCabinetHandle;
import com.lumos.smartdevice.activity.sm.SmUserManagerActivity;
import com.lumos.smartdevice.adapter.BookerBorrowReturnInspectCabinetBoxAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetIdentityBorrower;
import com.lumos.smartdevice.api.rop.RetUserGetList;
import com.lumos.smartdevice.api.rop.RopIdentityBorrower;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetBoxBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.UserBean;
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
    private MyGridView gdv_Boxs;
    private TextView tv_SignName;
    private TextView tv_CardNo;
    private TextView tv_CanBorrowQuantity;
    private TextView tv_BorrowedQuantity;

    private DeviceBean device;

    private List<CabinetBoxBean> cabinetBoxs=new ArrayList<>();

    private CustomDialogBookerBorrowReturnCabinetHandle dialog_BookerCabinetHandle;

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

                            CabinetBoxBean cabinetBox = new CabinetBoxBean();
                            cabinetBox.setCabinetId(cabinet.getCabinetId());
                            cabinetBox.setBoxId(id);
                            cabinetBox.setBoxName(name);
                            cabinetBox.setBoxPlate(plate);

                            cabinetBoxs.add(cabinetBox);

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
        gdv_Boxs = findViewById(R.id.gdv_Boxs);
        dialog_BookerCabinetHandle = new CustomDialogBookerBorrowReturnCabinetHandle(BookerBorrowReturnInspectActivity.this);
    }

    private void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
    }

    private void initData() {

        BookerBorrowReturnInspectCabinetBoxAdapter gridNineItemAdapter = new BookerBorrowReturnInspectCabinetBoxAdapter(getAppContext(), cabinetBoxs);

        gridNineItemAdapter.setOnClickListener(new BookerBorrowReturnInspectCabinetBoxAdapter.OnClickListener() {
            @Override
            public void onClick(CabinetBoxBean v) {

                dialog_BookerCabinetHandle.show();

                //Intent intent = new Intent(getAppContext(), BookerBorrowReturnOverviewActivity.class);
                //startActivity(intent);
            }
        });

        gdv_Boxs.setAdapter(gridNineItemAdapter);

        getIdentityBorrower("1", "1");
    }

    private void getIdentityBorrower(String identityType,String identityId){

        RopIdentityBorrower rop=new RopIdentityBorrower();
        rop.setDeviceId(device.getDeviceId());
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);

        ReqInterface.getInstance().identityBorrower(rop, new ReqHandler(){

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
                ResultBean<RetIdentityBorrower> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetIdentityBorrower>>() {
                });

                if(rt.getCode()== ResultCode.SUCCESS) {
                    RetIdentityBorrower d = rt.getData();

                    tv_SignName.setText(d.getSignName());
                    tv_CardNo.setText(d.getCardNo());
                    tv_BorrowedQuantity.setText(String.valueOf(d.getBorrowedQuantity()));
                    tv_CanBorrowQuantity.setText(String.valueOf(d.getCanBorrowQuantity()));

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

        dialog_BookerCabinetHandle.cancel();

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHome) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}