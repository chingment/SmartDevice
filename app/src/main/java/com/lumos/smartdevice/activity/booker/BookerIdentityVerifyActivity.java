package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.DialogBookerIdentityVerifyByIcCard;
import com.lumos.smartdevice.adapter.BookerIdentityVerfiyWayAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetIdentityVerify;
import com.lumos.smartdevice.api.rop.RopIdentityVerify;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.ReadCardUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerIdentityVerifyActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerIdentityVerifyActivity";

    private MyGridView gdv_Ways;
    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    private List<GridNineItemBean> gdv_Ways_Items;
 
    private DialogBookerIdentityVerifyByIcCard dialog_BookerIdentityVerifyByIcCard;

    private String intent_extra_action="";


    private DeviceBean device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify);

        intent_extra_action = getIntent().getStringExtra("action");
        device = getDevice();
        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify);

        setIcReaderSuccessListener(new ReadCardUtil.OnReadSuccessListener() {
            @Override
            public void onScanSuccess(String code) {
                LogUtil.e(TAG, "code: " + code);
                verfiy("1",code);
            }
        });

        initView();
        initEvent();
        initData();

    }

    private void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        gdv_Ways = findViewById(R.id.gdv_Ways);
        gdv_Ways.setFocusable(false);
        dialog_BookerIdentityVerifyByIcCard = new DialogBookerIdentityVerifyByIcCard(this);
        dialog_BookerIdentityVerifyByIcCard.setOnClickListener(new DialogBookerIdentityVerifyByIcCard.OnClickListener() {
            @Override
            public void testSuccesss() {
                verfiy("1","0007729527");
            }

            @Override
            public void testFailure() {

            }
        });
    }

    private void initEvent() {
        btn_Nav_Footer_GoBack.setOnClickListener(this);
        btn_Nav_Footer_GoHome.setOnClickListener(this);
        gdv_Ways.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    GridNineItemBean gdv_Nine_Item = gdv_Ways_Items.get(position);
                    String action = gdv_Nine_Item.getAction();
                    switch (action) {
                        case "iccard":
                            gdvIcCard();
                            break;
                        default:
                            break;
                    }

                }
            }
        });
    }

    private void initData() {
        gdv_Ways_Items = new ArrayList<>();
        gdv_Ways_Items.add(new GridNineItemBean(getAppContext().getString(R.string.t_brush_iccard), GridNineItemType.Function, "iccard", R.drawable.ic_booker_identity_verify_way_iccard));
        BookerIdentityVerfiyWayAdapter gdvWaysItemAdapter = new BookerIdentityVerfiyWayAdapter(getAppContext(), gdv_Ways_Items);
        gdv_Ways.setAdapter(gdvWaysItemAdapter);

    }

    private void verfiy(String dataType,String payload) {

        RopIdentityVerify rop = new RopIdentityVerify();
        rop.setDeviceId(device.getDeviceId());
        rop.setDataType(dataType);
        rop.setPayload(payload);
        ReqInterface.getInstance().identityVerify(rop, new ReqHandler() {

            @Override
            public void onBeforeSend() {
                super.onBeforeSend();
            }

            @Override
            public void onAfterSend() {
                super.onAfterSend();
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ResultBean<RetIdentityVerify> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetIdentityVerify>>() {
                });

                if (rt.getCode() == ResultCode.SUCCESS) {

                    RetIdentityVerify d = rt.getData();
                    Intent intent = new Intent(getAppContext(), BookerBorrowReturnInspectActivity.class);
                    intent.putExtra("action", intent_extra_action);
                    intent.putExtra("identityType", d.getIdentityType());
                    intent.putExtra("identityId", d.getIdentityId());
                    intent.putExtra("clientUserId", d.getClientUserId());

                    openActivity(intent);

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


    private void gdvIcCard() {
        dialog_BookerIdentityVerifyByIcCard.show();
    }

    @Override
    public void onClick(View v) {

        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_GoBack) {
                finish();
            }
            else if(id==R.id.btn_Nav_Footer_GoHome) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                openActivity(intent);
                finish();
            }
        }
    }

}