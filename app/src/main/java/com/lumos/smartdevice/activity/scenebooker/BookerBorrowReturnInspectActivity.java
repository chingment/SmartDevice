package com.lumos.smartdevice.activity.scenebooker;

import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.dialog.CustomDialogBookerBorrowReturnCabinetHandle;
import com.lumos.smartdevice.adapter.BookerBorrowReturnInspectCabinetBoxAdapter;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetBoxBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.DeviceBean;
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

    private View btn_Nav_Footer_Goback;
    private MyGridView gdv_Boxs;

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
                CabinetLayoutBean cabinetLayout = JSON.parseObject(cabinet.getLayout(), new TypeReference<CabinetLayoutBean>() {
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
        btn_Nav_Footer_Goback = findViewById(R.id.btn_Nav_Footer_Goback);

        gdv_Boxs = findViewById(R.id.gdv_Boxs);

        dialog_BookerCabinetHandle=new CustomDialogBookerBorrowReturnCabinetHandle(BookerBorrowReturnInspectActivity.this);
    }

    private void initEvent() {
        btn_Nav_Footer_Goback.setOnClickListener(this);
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