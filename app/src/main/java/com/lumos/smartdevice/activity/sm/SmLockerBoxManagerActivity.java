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
import com.lumos.smartdevice.adapter.SmLockerBoxCabinetBoxAdapter;
import com.lumos.smartdevice.adapter.SmLockerBoxCabinetNameAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetLockerGetBox;
import com.lumos.smartdevice.api.rop.RetLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxOpenResult;
import com.lumos.smartdevice.devicectrl.ILockerBoxCtrl;
import com.lumos.smartdevice.devicectrl.LockerBoxInterface;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.LockerBoxBean;
import com.lumos.smartdevice.model.LockerBoxUsageBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.dialog.CustomDialogCabinetConfig;
import com.lumos.smartdevice.ui.dialog.CustomDialogConfirm;
import com.lumos.smartdevice.ui.dialog.CustomDialogLockerBox;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SmLockerBoxManagerActivity extends BaseFragmentActivity {
    private static final String TAG = "SmLockerBoxManagerActivity";
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
        setContentView(R.layout.activity_sm_locker_box_manager);

        setNavHeaderTtile(R.string.aty_nav_title_smlockerboxmanager);
        setNavHeaderBtnByGoBackIsVisible(true);
        device=getDevice();
        HashMap<String, CabinetBean> l_Cabinets=device.getCabinets();
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

        dialog_CabinetConfig = new CustomDialogCabinetConfig(SmLockerBoxManagerActivity.this);
        dialog_Confirm = new CustomDialogConfirm(SmLockerBoxManagerActivity.this, "", true);
        dialog_Confirm.setOnClickListener(new CustomDialogConfirm.OnClickListener() {
            @Override
            public void onSure() {
               final String device_id;
               final String cabinet_id;
               final  String slot_id;
                String fun = dialog_Confirm.getFunction();
                Object tag=dialog_Confirm.getTag();
                switch (fun) {
                    case "deleteusage":
                        LockerBoxUsageBean usage = (LockerBoxUsageBean) dialog_Confirm.getTag();
                        lockerBoxDeleteUsage(usage);
                        break;
                    case "openallbox":
                        break;
                    case "openonebox":

                        HashMap<String, String> hash_tag = (HashMap<String, String>) tag;
                        device_id = hash_tag.get("device_id");
                        cabinet_id = hash_tag.get("cabinet_id");
                        slot_id = hash_tag.get("slot_id");

                        CabinetBean cabinet = device.getCabinets().get(cabinet_id);

                        LockerBoxInterface.getInstance(cabinet.getComId(), cabinet.getComBaud(), cabinet.getComPrl()).open(slot_id, new ILockerBoxCtrl.OnOpenListener() {
                            @Override
                            public void onSuccess() {
                                LogUtil.i(TAG, "开锁成功");

                                RopLockerSaveBoxOpenResult rop = new RopLockerSaveBoxOpenResult();
                                rop.setDeviceId(device_id);
                                rop.setCabinetId(cabinet_id);
                                rop.setSlotId(slot_id);
                                rop.setResult(1);
                                rop.setAction("admin_open_one");
                                rop.setRemark("后台人员操作打开");
                                ReqInterface.getInstance().lockerSaveBoxOpenResult(rop, new ReqHandler() {
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
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onFailure() {
                                LogUtil.i(TAG, "开锁失败");

                                RopLockerSaveBoxOpenResult rop = new RopLockerSaveBoxOpenResult();
                                rop.setDeviceId(device_id);
                                rop.setCabinetId(cabinet_id);
                                rop.setSlotId(slot_id);
                                rop.setResult(2);
                                rop.setAction("admin_open_one");
                                rop.setRemark("后台人员操作打开");
                                ReqInterface.getInstance().lockerSaveBoxOpenResult(rop,new ReqHandler() {
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
                                            }
                                        }
                                );
                            }
                        });

                        break;

                }
                dialog_Confirm.hide();
            }

            @Override
            public void onCancle() {
                dialog_Confirm.hide();
            }
        });


        dialog_LockerBox = new CustomDialogLockerBox(SmLockerBoxManagerActivity.this);
        dialog_LockerBox.setOnClickListener(new CustomDialogLockerBox.OnClickListener() {
            @Override
            public void onGoSelectUser(String deviceId, String cabinetId,String slotId) {

                Intent intent = new Intent(SmLockerBoxManagerActivity.this, SmUserManagerActivity.class);
                intent.putExtra("scene_mode", 2);
                HashMap<String, String> scene_param=new HashMap<>();
                scene_param.put("device_id",deviceId);
                scene_param.put("cabinet_id",cabinetId);
                scene_param.put("slot_id",slotId);

                intent.putExtra("scene_param",scene_param);
                openActivity(intent);
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
            public void onOpenBox(String deviceId, String cabinetId,String slotId) {
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTipsText("确定打开箱子？");
                dialog_Confirm.setFunction("openonebox");
                HashMap<String, String> tag = new HashMap<>();
                tag.put("device_id", deviceId);
                tag.put("cabinet_id", cabinetId);
                tag.put("slot_id", slotId);
                dialog_Confirm.setTag(tag);
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
                loadData();
            }
        });
    }

    private void initData() {
        loadData();
    }

    private void loadData(){

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

        SmLockerBoxCabinetNameAdapter list_cabinet_adapter = new SmLockerBoxCabinetNameAdapter(getAppContext(), cabinets, cur_Cabinet_Position);
        lv_Cabinets.setAdapter(list_cabinet_adapter);
        tv_CabinetName.setText(cur_Cabinet.getName());

        lockerGetCabinet();

    }

    public void drawsLayout(String json_layout,List<LockerBoxBean> boxs) {
        CabinetLayoutBean layout = JSON.parseObject(json_layout, new TypeReference<CabinetLayoutBean>() {});
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(layout.getSpanCount(),StaggeredGridLayoutManager.VERTICAL);
        tl_Boxs.setLayoutManager(staggeredGridLayoutManager);


        SmLockerBoxCabinetBoxAdapter tl_Boxs_Adapter = new SmLockerBoxCabinetBoxAdapter(this, boxs);


        tl_Boxs.setItemAnimator(new DefaultItemAnimator());
        tl_Boxs.setAdapter(tl_Boxs_Adapter);
        tl_Boxs_Adapter.setOnItemClickListener(new SmLockerBoxCabinetBoxAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LockerBoxBean l_Box = (LockerBoxBean)view.getTag();
                dialog_LockerBox.setConfig(device, cur_Cabinet, l_Box);
                lockerGetBox();
                dialog_LockerBox.show();
            }
        });
    }

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
                            lockerBox.setUsed(ret.isUsed());
                            lockerBox.setType(ret.getType());
                            lockerBox.setHeight(ret.getHeight());
                            lockerBox.setWidth(ret.getWidth());
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

    public void lockerGetCabinet(){

        RopLockerGetCabinet rop=new RopLockerGetCabinet();
        rop.setDeviceId(device.getDeviceId());
        rop.setCabinetId(cur_Cabinet.getCabinetId());

        ReqInterface.getInstance().lockerGetCabinet(rop, new ReqHandler(){

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

                        ResultBean<RetLockerGetCabinet> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetLockerGetCabinet>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetLockerGetCabinet ret = rt.getData();

                            cur_Cabinet.setName(ret.getName());
                            cur_Cabinet.setComBaud(ret.getComBaud());
                            cur_Cabinet.setComId(ret.getComId());
                            cur_Cabinet.setComPrl(ret.getComPrl());
                            cur_Cabinet.setLayout(ret.getLayout());

                            drawsLayout(ret.getLayout(), ret.getBoxs());

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
                            lockerGetCabinet();
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

        lockerGetCabinet();
        lockerGetBox();
    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Header_Goback) {
                finish();
            } else if (id == R.id.tv_CabinetName) {
                dialog_CabinetConfig.setCofing(cur_Cabinet);
                dialog_CabinetConfig.show();
            } else if (id == R.id.btn_OpenAllBox) {
                dialog_Confirm.setTipsImageVisibility(View.GONE);
                dialog_Confirm.setTipsText("确定打开全部箱子？");
                dialog_Confirm.setFunction("openallbox");
                dialog_Confirm.show();
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
