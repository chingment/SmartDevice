package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.CustomDialogBookerIdentityVerifyByIcCard;
import com.lumos.smartdevice.adapter.BookerIdentityVerfiyWayAdapter;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerIdentityVerifyActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerIdentityVerifyActivity";

    private MyGridView gdv_Ways;
    private View btn_Nav_Footer_GoBack;
    private View btn_Nav_Footer_GoHome;

    private List<GridNineItemBean> gdv_Ways_Items;
 
    private CustomDialogBookerIdentityVerifyByIcCard dialog_BookerIdentityVerifyByIcCard;

    private String intent_extra_action="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify);

        intent_extra_action = getIntent().getStringExtra("action");

        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_GoBack = findViewById(R.id.btn_Nav_Footer_GoBack);
        btn_Nav_Footer_GoHome = findViewById(R.id.btn_Nav_Footer_GoHome);
        gdv_Ways = findViewById(R.id.gdv_Ways);

        dialog_BookerIdentityVerifyByIcCard = new CustomDialogBookerIdentityVerifyByIcCard(this);
        dialog_BookerIdentityVerifyByIcCard.setOnClickListener(new CustomDialogBookerIdentityVerifyByIcCard.OnClickListener() {
            @Override
            public void testSuccesss() {

                Intent intent = new Intent(getAppContext(), BookerBorrowReturnInspectActivity.class);
                intent.putExtra("action", intent_extra_action);
                openActivity(intent);

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