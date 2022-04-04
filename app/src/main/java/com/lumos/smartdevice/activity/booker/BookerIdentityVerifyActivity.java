package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.dialog.DialogBookerIdentityVerifyByIcCard;
import com.lumos.smartdevice.activity.booker.adapter.BookerIdentityVerfiyWayAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetIdentityVerify;
import com.lumos.smartdevice.api.rop.RopIdentityVerify;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.adapter.GridNineItemBean;
import com.lumos.smartdevice.adapter.GridNineItemType;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.UsbReaderUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerIdentityVerifyActivity extends BookerBaseActivity {

    private static final String TAG = "BookerIdentityVerifyActivity";

    private MyGridView gdv_Ways;
    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    private List<GridNineItemBean> gdv_Ways_Items;
 
    private DialogBookerIdentityVerifyByIcCard dialog_BookerIdentityVerifyByIcCard;

    private String intent_extra_action="";


    private DeviceVo device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify);

        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify);

        intent_extra_action = getIntent().getStringExtra("action");
        device = getDevice();

        setUsbReaderListener(new UsbReaderUtil.OnListener() {
            @Override
            public void onSuccess(String code) {
                LogUtil.e(TAG, "code: " + code);
                verify(1,code);
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
        gdv_Ways = findViewById(R.id.gdv_Ways);
        gdv_Ways.setFocusable(false);
        dialog_BookerIdentityVerifyByIcCard = new DialogBookerIdentityVerifyByIcCard(this);
        dialog_BookerIdentityVerifyByIcCard.setOnClickListener(new DialogBookerIdentityVerifyByIcCard.OnClickListener() {
            @Override
            public void test() {
                verify(1,"0007729527");
            }
        });
    }

    public void initEvent() {
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

    public void initData() {
        gdv_Ways_Items = new ArrayList<>();
        gdv_Ways_Items.add(new GridNineItemBean(getAppContext().getString(R.string.t_brush_iccard), GridNineItemType.Function, "iccard", R.drawable.ic_booker_identity_verify_way_iccard));
        BookerIdentityVerfiyWayAdapter gdvWaysItemAdapter = new BookerIdentityVerfiyWayAdapter(getAppContext(), gdv_Ways_Items);
        gdv_Ways.setAdapter(gdvWaysItemAdapter);

    }

    private void verify(int verifyMode,String payload) {

        RopIdentityVerify rop = new RopIdentityVerify();
        rop.setDeviceId(device.getDeviceId());
        rop.setVerifyMode(verifyMode);
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
                ResultBean<RetIdentityVerify> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetIdentityVerify>>() {});

                if (rt.getCode() == ResultCode.SUCCESS) {

                    RetIdentityVerify d = rt.getData();
                    Intent intent = new Intent(getAppContext(), BookerBorrowReturnInspectActivity.class);
                    intent.putExtra("action", intent_extra_action);
                    intent.putExtra("identityType", d.getIdentityType());
                    intent.putExtra("identityId", d.getIdentityId());
                    intent.putExtra("clientUserId", d.getClientUserId());
                    openActivity(intent);

                    if(dialog_BookerIdentityVerifyByIcCard.isShowing()) {
                        dialog_BookerIdentityVerifyByIcCard.hide();
                    }


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


    private void gdvIcCard() {
        toSpeech("请把卡片放在机器的感应区");
        dialog_BookerIdentityVerifyByIcCard.show();
    }

    @Override
    public void onDestroy() {
        if(dialog_BookerIdentityVerifyByIcCard!=null) {
            dialog_BookerIdentityVerifyByIcCard.cancel();
        }
        super.onDestroy();
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