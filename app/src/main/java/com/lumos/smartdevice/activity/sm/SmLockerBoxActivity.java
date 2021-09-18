package com.lumos.smartdevice.activity.sm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.SmCabinetLayoutBoxAdapter;
import com.lumos.smartdevice.adapter.SmCabinetNameAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetLockerGetBox;
import com.lumos.smartdevice.api.rop.RetLockerGetBoxs;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxs;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.LockerBoxBean;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogCabinetConfig;
import com.lumos.smartdevice.ui.dialog.CustomDialogConfirm;
import com.lumos.smartdevice.ui.dialog.CustomDialogLockerBox;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SmLockerBoxActivity extends BaseFragmentActivity {
    private static final String TAG = "SmLockerBoxActivity";
    private TextView tv_CabinetName;
    private ListView lv_Cabinets;
    private TextView btn_OpenAllBox;
    private CabinetBean cur_Cabinet;
    private List<CabinetBean> cabinets;
    private static int cur_Cabinet_Position = 0;

    private CustomDialogCabinetConfig dialog_CabinetConfig;
    private CustomDialogLockerBox dialog_LockerBox;
    private CustomDialogConfirm dialog_Confirm;
    private DeviceBean device;

    private RecyclerView tl_Boxs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smlockerbox);

        setNavHeaderTtile(R.string.aty_smlockerbox_nav_title);
        setNavHeaderBtnByGoBackIsVisible(true);
        device=getDevice();
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
        btn_OpenAllBox=findViewById(R.id.btn_OpenAllBox);
        tl_Boxs = findViewById(R.id.tl_Boxs);

        dialog_CabinetConfig = new CustomDialogCabinetConfig(SmLockerBoxActivity.this);
        dialog_Confirm = new CustomDialogConfirm(SmLockerBoxActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new CustomDialogConfirm.OnClickListener() {
            @Override
            public void onSure() {
                String fun = dialog_Confirm.getFunction();
                switch (fun) {
                    case "deleteusage":
                        LockerBoxUsageBean usage=(LockerBoxUsageBean)dialog_Confirm.getTag();
                        lockerBoxDeleteUsage(usage);
                        break;
                    case "openallbox":

                        break;

                }
                dialog_Confirm.hide();
            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });


        dialog_LockerBox = new CustomDialogLockerBox(SmLockerBoxActivity.this);
        dialog_LockerBox.setOnClickListener(new CustomDialogLockerBox.OnClickListener() {
            @Override
            public void onGoSelectUser(String deviceId, String cabinetId,String slotId) {

                Intent intent = new Intent(SmLockerBoxActivity.this, SmUserManagerActivity.class);
                intent.putExtra("scene_mode", 2);
                HashMap<String, String> scene_param=new HashMap<String, String>();
                scene_param.put("device_id",deviceId);
                scene_param.put("cabinet_id",cabinetId);
                scene_param.put("slot_id",slotId);

                intent.putExtra("scene_param",scene_param);
                startActivity(intent);
            }

            @Override
            public void onDeleteUsage(LockerBoxUsageBean usage) {


                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setFunction("deleteusage");
                dialog_Confirm.setTag(usage);
                dialog_Confirm.setTipsText(getAppContext().getString(R.string.confrim_tips_delete));
                dialog_Confirm.show();


            }


            @Override
            public void onOpenBox(String deviceId, String cabinetId,String slotId){

                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTipsText("确定打开箱子？");
                dialog_Confirm.setFunction("openonebox");
                dialog_Confirm.show();

            }
        });

    }

    private void initEvent() {
        btn_OpenAllBox.setOnClickListener(this);
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

        SmCabinetNameAdapter list_cabinet_adapter = new SmCabinetNameAdapter(getAppContext(), cabinets, cur_Cabinet_Position);
        lv_Cabinets.setAdapter(list_cabinet_adapter);
        tv_CabinetName.setText(cur_Cabinet.getName());

        lockerGetBoxs();

    }

    public void drawsLayout(String json_layout,HashMap<String, LockerBoxBean> boxs) {
        CabinetLayoutBean layout = JSON.parseObject(json_layout, new TypeReference<CabinetLayoutBean>() {});
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(layout.getSpanCount(),StaggeredGridLayoutManager.VERTICAL);
        tl_Boxs.setLayoutManager(staggeredGridLayoutManager);
        SmCabinetLayoutBoxAdapter tl_Boxs_Adapter = new SmCabinetLayoutBoxAdapter(this, layout.getCells(),boxs);
        tl_Boxs.setItemAnimator(new DefaultItemAnimator());
        tl_Boxs.setAdapter(tl_Boxs_Adapter);
        tl_Boxs_Adapter.setOnItemClickListener(new SmCabinetLayoutBoxAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LockerBoxBean l_Box = (LockerBoxBean)view.getTag();
                dialog_LockerBox.setConfig(device, cur_Cabinet, l_Box);
                lockerGetBox();
                dialog_LockerBox.show();
            }
        });
    }

//    public void drawsLayout(String json_layout,HashMap<String, LockerBoxBean> boxs) {
//
//        List<List<String>> layout = JSON.parseObject(json_layout, new TypeReference< List<List<String>>>() {});
//
//        tl_Boxs.removeAllViews();
//        tl_Boxs.setStretchAllColumns(true);
//
//        int rowsSize=layout.size();
//
//        for (int i = 0; i <rowsSize; i++) {
//
//            TableRow tableRow = new TableRow(SmLockerBoxActivity.this);
//
//            List<String> cols=layout.get(i);
//            int colsSize=cols.size();
//
//            for (int j = 0; j < colsSize; j++) {
//                //tv用于显示
//
//                String col=cols.get(j);
//                String slot_Id=col;
//                LockerBoxBean box=boxs.get(slot_Id);
//                String[] col_Prams=col.split("-");
//
//                String id=col_Prams[0];
//                String plate=col_Prams[1];
//                String name=col_Prams[2];
//                String isUse=col_Prams[3];
//
//                final View convertView = LayoutInflater.from(SmLockerBoxActivity.this).inflate(R.layout.item_list_lockerbox, tableRow, false);
//
//                TextView tv_Name = ViewHolder.get(convertView, R.id.tv_Name);
//
//                tv_Name.setText(name);
//
//                convertView.setTag(box);
//
//                if(isUse.equals("1")){
//                    convertView.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            LockerBoxBean l_Box = (LockerBoxBean)v.getTag();
//                            dialog_LockerBox.setConfig(device, cur_Cabinet, l_Box);
//                            lockerGetBox();
//                            dialog_LockerBox.show();
//                        }
//                    });
//                }
//
//                if(box!=null){
//                    String box_IsUsed=box.getIsUsed();
//                    if(box_IsUsed.equals("0")){
//                        tv_Name.setBackgroundResource(R.drawable.locker_box_status_1);
//                    }
//                    else {
//                        tv_Name.setBackgroundResource(R.drawable.locker_box_status_2);
//                    }
//                }
//
//
//                tableRow.addView(convertView, new TableRow.LayoutParams(MP, WC, 1));
//            }
//
//            tl_Boxs.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
//
//        }
//    }

    public void lockerGetBox() {

        String deviceId = dialog_LockerBox.getDeviceId();
        String cabinetId = dialog_LockerBox.getCabinetId();
        String slotId = dialog_LockerBox.getSlotId();


        if (StringUtil.isEmptyNotNull(deviceId))
            return;
        if (StringUtil.isEmptyNotNull(cabinetId))
            return;
        if (StringUtil.isEmptyNotNull(slotId))
            return;


        RopLockerGetBox rop = new RopLockerGetBox();
        rop.setDeviceId(deviceId);
        rop.setCabinetId(cabinetId);
        rop.setSlotId(slotId);
        ReqInterface.getInstance().lockerGetBox(rop, new ReqHandler() {

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

                        ResultBean<RetLockerGetBox> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetLockerGetBox>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetLockerGetBox ret = rt.getData();
                            LockerBoxBean lockerBox=new LockerBoxBean();
                            lockerBox.setDeviceId(ret.getDeviceId());
                            lockerBox.setCabinetId(ret.getCabinetId());
                            lockerBox.setSlotId(ret.getSlotId());
                            lockerBox.setIsUsed(ret.getIsUsed());
                            lockerBox.setUsages(ret.getUsages());
                            dialog_LockerBox.setLockerBox(lockerBox);
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                }
        );
    }

    public void lockerGetBoxs(){

        RopLockerGetBoxs rop=new RopLockerGetBoxs();
        rop.setDeviceId(device.getDeviceId());
        rop.setCabinetId(cur_Cabinet.getCabinetId());

        ReqInterface.getInstance().lockerGetBoxs(rop, new ReqHandler(){

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

                        ResultBean<RetLockerGetBoxs> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetLockerGetBoxs>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetLockerGetBoxs ret = rt.getData();
                            drawsLayout(cur_Cabinet.getLayout(), ret.getBoxs());
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                }
        );

    }

    public void lockerBoxDeleteUsage(LockerBoxUsageBean usage){

        RopLockerDeleteBoxUsage rop=new RopLockerDeleteBoxUsage();
        rop.setDeviceId(usage.getDeviceId());
        rop.setCabinetId(usage.getCabinetId());
        rop.setSlotId(usage.getSlotId());
        rop.setUsageType(usage.getUsageType());
        rop.setUsageData(usage.getUsageData());

        ReqInterface.getInstance().lockerDeleteBoxUsage(rop, new ReqHandler(){

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

                        ResultBean<Object> rt = JSON.parseObject(response, new TypeReference<ResultBean<Object>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            lockerGetBoxs();
                            lockerGetBox();
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                    }
                }
        );

    }
    @Override
    public  void onResume() {
        super.onResume();

        lockerGetBoxs();
        lockerGetBox();
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
                case R.id.btn_OpenAllBox:
                    dialog_Confirm.setTipsImageVisibility(View.GONE);
                    dialog_Confirm.setTipsText("确定打开全部箱子？");
                    dialog_Confirm.setFunction("openallbox");
                    dialog_Confirm.show();
                    break ;
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

        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }
    }
}
