package com.lumos.smartdevice.activity.sm;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmLockerBoxCabinetSelectAdapter;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.ViewHolder;
import com.lumos.smartdevice.ui.dialog.CustomDialogCabinetConfig;
import com.lumos.smartdevice.ui.dialog.CustomDialogLockerBox;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SmLockerBoxActivity extends BaseFragmentActivity {

    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    private TextView tv_CabinetName;
    private ListView lv_Cabinets;
    private TableLayout tl_Boxs;

    private CabinetBean cur_Cabinet;
    private List<CabinetBean> cabinets;
    private static int cur_Cabinet_Position = 0;

    private CustomDialogCabinetConfig dialog_CabinetConfig;

    private CustomDialogLockerBox dialog_LockerBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smlockerbox);

        setNavHeaderTtile(R.string.aty_smlockerbox_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);

        HashMap<String, CabinetBean> l_Cabinets=getDevice().getCabinets();
        if(l_Cabinets!=null) {

            cabinets = new ArrayList<>(l_Cabinets.values());

            Collections.sort(cabinets, new Comparator<CabinetBean>() {
                @Override
                public int compare(CabinetBean t1, CabinetBean t2) {
                    return t2.getPriority() - t1.getPriority();
                }
            });
        }

        initView();
        initEvent();
        initData();

    }

    private void initView() {
        lv_Cabinets = findViewById(R.id.lv_Cabinets);
        tv_CabinetName = findViewById(R.id.tv_CabinetName);
        tl_Boxs = findViewById(R.id.tl_Boxs);
        dialog_CabinetConfig = new CustomDialogCabinetConfig(SmLockerBoxActivity.this);
        dialog_LockerBox=new CustomDialogLockerBox(SmLockerBoxActivity.this);
        dialog_LockerBox.setOnClickListener(new CustomDialogLockerBox.OnClickListener() {
            @Override
            public void onSelectUser() {

                Intent intent = new Intent(SmLockerBoxActivity.this, SmUserManagerActivity.class);
                intent.putExtra("scene",2);
                startActivity(intent);
            }
        });

    }

    private void initEvent() {
        tv_CabinetName.setOnClickListener(this);
        lv_Cabinets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                cur_Cabinet_Position = position;
                loadCabinetData();
            }
        });
    }

    private void initData() {
        loadCabinetData();
    }

    private void loadCabinetData(){

        if (cabinets == null)
            return;
        if (cabinets.size() == 0)
            return;

        if(cabinets.size()==1){
            lv_Cabinets.setVisibility(View.GONE);
        }

        cur_Cabinet = cabinets.get(cur_Cabinet_Position);

        if (cur_Cabinet == null)
            return;

        SmLockerBoxCabinetSelectAdapter list_cabinet_adapter = new SmLockerBoxCabinetSelectAdapter(getAppContext(), cabinets, cur_Cabinet_Position);
        lv_Cabinets.setAdapter(list_cabinet_adapter);

        tv_CabinetName.setText(cur_Cabinet.getCabinetId());

        drawsBoxs(cur_Cabinet.getLayout());
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

                String box_Id=cols.get(j);

                String[] box_Prams=box_Id.split("-");

                String id=box_Prams[0];
                String plate=box_Prams[1];
                String name=box_Prams[2];
                String isUse=box_Prams[3];

                final View convertView = LayoutInflater.from(SmLockerBoxActivity.this).inflate(R.layout.item_list_lockerbox, tableRow, false);


                TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);

                tv_Name.setText(name);

                convertView.setTag(box_Id);

                if(isUse.equals("1")){
                    convertView.setVisibility(View.INVISIBLE);
                }
                else {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String l_Box_Id=v.getTag().toString();
                            dialog_LockerBox.setLockerBox(cur_Cabinet,l_Box_Id);
                            dialog_LockerBox.show();
                        }
                    });
                }

                //if(id.equals("2"))
                //    tv_Name.setBackgroundResource(R.drawable.locker_box_status_2);


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
                    dialog_CabinetConfig.setCofing(cur_Cabinet);
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
