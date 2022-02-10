package com.lumos.smartdevice.activity.scenebooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.GridNineItemAdapter;
import com.lumos.smartdevice.model.GridNineItemBean;
import com.lumos.smartdevice.model.GridNineItemType;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyGridView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerIdentityVerifyWaySelectActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerIdentityVerifyWaySelectActivity";

    private List<GridNineItemBean> gdv_Nine_Items;
    private MyGridView gdv_Nine;
    private View btn_Nav_Footer_Goback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_identity_verify_way_select);
        setNavHeaderTtile(R.string.aty_nav_title_booker_identity_verify_way_select);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_Goback=findViewById(R.id.btn_Nav_Footer_Goback);
        gdv_Nine = findViewById(R.id.gdv_Nine);
    }

    private void initEvent() {
        btn_Nav_Footer_Goback.setOnClickListener(this);
        gdv_Nine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NoDoubleClickUtil.isDoubleClick()) {
                    GridNineItemBean gdv_Nine_Item = gdv_Nine_Items.get(position);
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

        gdv_Nine_Items = new ArrayList<>();
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.t_brush_iccard), GridNineItemType.Function, "iccard", R.drawable.ic_booker_identity_verify_option_iccard));
        gdv_Nine_Items.add(new GridNineItemBean(getAppContext().getString(R.string.t_brush_iccard), GridNineItemType.Function, "iccard", R.drawable.ic_booker_identity_verify_option_iccard));
        GridNineItemAdapter gridNineItemAdapter = new GridNineItemAdapter(getAppContext(),R.layout.item_grid_nine_booker_identity_verify_option, gdv_Nine_Items);
        gdv_Nine.setAdapter(gridNineItemAdapter);

    }

    private void gdvIcCard(){
        Intent intent = new Intent(BookerIdentityVerifyWaySelectActivity.this, BookerIdentityVerifyByIcCardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_Goback) {
                finish();
            }
        }
    }
}