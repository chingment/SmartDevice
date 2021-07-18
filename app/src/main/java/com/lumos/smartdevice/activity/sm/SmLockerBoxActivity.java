package com.lumos.smartdevice.activity.sm;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.ui.dialog.CustomDialogCabinetConfig;
import com.lumos.smartdevice.ui.dialog.CustomDialogLockerBox;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.HashMap;
import java.util.List;

public class SmLockerBoxActivity extends BaseFragmentActivity {

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    private TextView tv_CabinetName;
    private TableLayout tl_Boxs;

    private CabinetBean cabinet;

    private CustomDialogCabinetConfig dialog_CabinetConfig;

    private CustomDialogLockerBox dialog_LockerBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smlockerbox);

        cabinet = (CabinetBean)getIntent().getSerializableExtra("cabinet");

        setNavHeaderTtile(R.string.aty_smlockerbox_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        initView();
        initEvent();
        initData();

    }

    private void initView() {
        tv_CabinetName = findViewById(R.id.tv_CabinetName);
        tl_Boxs = findViewById(R.id.tl_Boxs);
        dialog_CabinetConfig = new CustomDialogCabinetConfig(SmLockerBoxActivity.this);
        dialog_LockerBox=new CustomDialogLockerBox(SmLockerBoxActivity.this);

    }

    private void initEvent() {
        tv_CabinetName.setOnClickListener(this);
    }

    private void initData() {
        tv_CabinetName.setText(cabinet.getCabinetId());

        drawsBoxs(cabinet.getLayout());
    }


    public void drawsBoxs(String json_layout) {


        List<List<String>> layout = JSON.parseObject(json_layout, new TypeReference< List<List<String>>>() {});


        //清除表格所有行
        tl_Boxs.removeAllViews();
        //全部列自动填充空白处
        tl_Boxs.setStretchAllColumns(true);
        //生成X行，Y列的表格

        int rowsSize=layout.size();

        for (int i = 0; i <rowsSize; i++) {

            TableRow tableRow = new TableRow(SmLockerBoxActivity.this);


            List<String> cols=layout.get(i);
            int colsSize=cols.size();

            for (int j = 0; j < colsSize; j++) {
                //tv用于显示
                String[] col=cols.get(j).split("-");

                String id=col[0];
                String nick=col[0];
                String plate=col[0];
                String isUse=col[0];

                final View convertView = LayoutInflater.from(SmLockerBoxActivity.this).inflate(R.layout.item_list_lockerbox, tableRow, false);

                TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);

                tv_Name.setText(nick);

                if(isUse.equals("1")){
                    convertView.setVisibility(View.INVISIBLE);
                }
                else {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_LockerBox.show();
                        }
                    });
                }

                if(id.equals("2"))
                    tv_Name.setBackgroundResource(R.drawable.locker_box_status_2);


                tableRow.addView(convertView, new TableRow.LayoutParams(MP, WC, 1));
            }

            tl_Boxs.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));

        }
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {
            switch (v.getId()) {
                case R.id.btn_Nav_Header_Goback:
                    finish();
                    break;
                case R.id.tv_CabinetName:
                    dialog_CabinetConfig.setCofing(cabinet);
                    dialog_CabinetConfig.show();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog_CabinetConfig != null) {
            dialog_CabinetConfig.cancel();
        }

        if (dialog_LockerBox != null) {
            dialog_LockerBox.cancel();
        }
    }
}
