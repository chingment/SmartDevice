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

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmCabinetConfig;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmConfirm;
import com.lumos.smartdevice.activity.sm.dialog.DialogSmLockerBox;
import com.lumos.smartdevice.activity.sm.adapter.SmLockerBoxCabinetBoxAdapter;
import com.lumos.smartdevice.activity.sm.adapter.SmLockerBoxCabinetNameAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetLockerGetBox;
import com.lumos.smartdevice.api.rop.RetLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopLockerGetCabinet;
import com.lumos.smartdevice.api.vo.CabinetVo;
import com.lumos.smartdevice.api.vo.CabinetLayoutVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.LockerBoxVo;
import com.lumos.smartdevice.api.vo.LockerBoxUsageVo;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.HashMap;
import java.util.List;

public class SmLockerBoxManagerActivity extends SmBaseActivity {
    private static final String TAG = "SmLockerBoxManagerActivity";
    private TextView tv_CabinetName;
    private ListView lv_Cabinets;
    private TextView btn_OpenAllBox;
    private CabinetVo cur_Cabinet;
    private List<CabinetVo> cabinets;
    private static int cur_Cabinet_Position = 0;

    private DialogSmCabinetConfig dialog_SmCabinetConfig;
    private DialogSmLockerBox dialog_SmLockerBox;
    private DialogSmConfirm dialog_Confirm;
    private DeviceVo device;

    private RecyclerView tl_Boxs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_locker_box_manager);

        setNavHeaderTtile(R.string.aty_nav_title_smlockerboxmanager);
        setNavHeaderBtnByGoBackIsVisible(true);
        device=getDevice();
//        HashMap<String, CabinetBean> l_Cabinets=device.getCabinets();
//        if(l_Cabinets!=null) {
//
//            cabinets = new ArrayList<>(l_Cabinets.values());
//
//            Collections.sort(cabinets, new Comparator<CabinetBean>() {
//                @Override
//                public int compare(CabinetBean t1, CabinetBean t2) {
//                    return t2.getPriority() - t1.getPriority();
//                }
//            });
//        }

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        lv_Cabinets = findViewById(R.id.lv_Cabinets);
        tv_CabinetName = findViewById(R.id.tv_CabinetName);
        btn_OpenAllBox=findViewById(R.id.btn_OpenAllBox);
        tl_Boxs = findViewById(R.id.tl_Boxs);

        dialog_SmCabinetConfig = new DialogSmCabinetConfig(SmLockerBoxManagerActivity.this);
        dialog_Confirm = new DialogSmConfirm(SmLockerBoxManagerActivity.this, "", true);
//        dialog_Confirm.setOnClickListener(new DialogConfirm.OnClickListener() {
//            @Override
//            public void onSure() {
//               final String device_id;
//               final String cabinet_id;
//               final  String slot_id;
//                String fun = dialog_Confirm.getFunction();
//                Object tag=dialog_Confirm.getTag();
//                switch (fun) {
//                    case "deleteusage":
//                        LockerBoxUsageBean usage = (LockerBoxUsageBean) dialog_Confirm.getTag();
//                        lockerBoxDeleteUsage(usage);
//                        break;
//                    case "openallbox":
//                        break;
//                    case "openonebox":
//
//                        HashMap<String, String> hash_tag = (HashMap<String, String>) tag;
//                        device_id = hash_tag.get("device_id");
//                        cabinet_id = hash_tag.get("cabinet_id");
//                        slot_id = hash_tag.get("slot_id");
//
//                        CabinetBean cabinet = device.getCabinets().get(cabinet_id);
//
//                        LockerBoxInterface.getInstance(cabinet.getComId(), cabinet.getComBaud(), cabinet.getComPrl()).open(slot_id, new ILockerBoxCtrl.OnOpenListener() {
//                            @Override
//                            public void onSuccess() {
//                                LogUtil.i(TAG, "开锁成功");
//
//                                RopLockerSaveBoxOpenResult rop = new RopLockerSaveBoxOpenResult();
//                                rop.setDeviceId(device_id);
//                                rop.setCabinetId(cabinet_id);
//                                rop.setSlotId(slot_id);
//                                rop.setResult(1);
//                                rop.setAction("admin_open_one");
//                                rop.setRemark("后台人员操作打开");
//                                ReqInterface.getInstance().lockerSaveBoxOpenResult(rop, new ReqHandler() {
//                                            @Override
//                                            public void onBeforeSend() {
//                                                super.onBeforeSend();
//                                            }
//                                            @Override
//                                            public void onAfterSend() {
//                                                super.onAfterSend();
//                                            }
//                                            @Override
//                                            public void onSuccess(String response) {
//                                            }
//                                        }
//                                );
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                LogUtil.i(TAG, "开锁失败");
//
//                                RopLockerSaveBoxOpenResult rop = new RopLockerSaveBoxOpenResult();
//                                rop.setDeviceId(device_id);
//                                rop.setCabinetId(cabinet_id);
//                                rop.setSlotId(slot_id);
//                                rop.setResult(2);
//                                rop.setAction("admin_open_one");
//                                rop.setRemark("后台人员操作打开");
//                                ReqInterface.getInstance().lockerSaveBoxOpenResult(rop,new ReqHandler() {
//                                            @Override
//                                            public void onBeforeSend() {
//                                                super.onBeforeSend();
//                                            }
//                                            @Override
//                                            public void onAfterSend() {
//                                                super.onAfterSend();
//                                            }
//                                            @Override
//                                            public void onSuccess(String response) {
//                                            }
//                                        }
//                                );
//                            }
//                        });
//
//                        break;
//
//                }
//                dialog_Confirm.hide();
//            }
//
//            @Override
//            public void onCancle() {
//                dialog_Confirm.hide();
//            }
//        });


        dialog_SmLockerBox = new DialogSmLockerBox(SmLockerBoxManagerActivity.this);
        dialog_SmLockerBox.setOnClickListener(new DialogSmLockerBox.OnClickListener() {
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
            public void onDeleteUsage(LockerBoxUsageVo usage) {
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

    public void drawsLayout(String json_layout,List<LockerBoxVo> boxs) {
        CabinetLayoutVo layout =JsonUtil.toObject(json_layout,new TypeReference<CabinetLayoutVo>() {});
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(layout.getSpanCount(),StaggeredGridLayoutManager.VERTICAL);
        tl_Boxs.setLayoutManager(staggeredGridLayoutManager);


        SmLockerBoxCabinetBoxAdapter tl_Boxs_Adapter = new SmLockerBoxCabinetBoxAdapter(this, boxs);


        tl_Boxs.setItemAnimator(new DefaultItemAnimator());
        tl_Boxs.setAdapter(tl_Boxs_Adapter);
        tl_Boxs_Adapter.setOnItemClickListener(new SmLockerBoxCabinetBoxAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LockerBoxVo l_Box = (LockerBoxVo)view.getTag();
                dialog_SmLockerBox.setConfig(device, cur_Cabinet, l_Box);
                lockerGetBox();
                dialog_SmLockerBox.show();
            }
        });
    }

    public void lockerGetBox() {

        String deviceId = dialog_SmLockerBox.getDeviceId();
        String cabinetId = dialog_SmLockerBox.getCabinetId();
        String slotId = dialog_SmLockerBox.getSlotId();


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

                        ResultBean<RetLockerGetBox> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetLockerGetBox>>() {});

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetLockerGetBox ret = rt.getData();
                            LockerBoxVo lockerBox=new LockerBoxVo();
                            lockerBox.setDeviceId(ret.getDeviceId());
                            lockerBox.setCabinetId(ret.getCabinetId());
                            lockerBox.setSlotId(ret.getSlotId());
                            lockerBox.setUsed(ret.isUsed());
                            lockerBox.setType(ret.getType());
                            lockerBox.setHeight(ret.getHeight());
                            lockerBox.setWidth(ret.getWidth());
                            lockerBox.setUsages(ret.getUsages());
                            dialog_SmLockerBox.setLockerBox(lockerBox);


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

                        ResultBean<RetLockerGetCabinet> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetLockerGetCabinet>>() {});

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

    public void lockerBoxDeleteUsage(LockerBoxUsageVo usage){

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

                        ResultBean<Object> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<Object>>() {});

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
                dialog_SmCabinetConfig.setCofing(cur_Cabinet);
                dialog_SmCabinetConfig.show();
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
        if (dialog_SmCabinetConfig != null) {
            dialog_SmCabinetConfig.cancel();
        }

        if (dialog_SmLockerBox != null) {
            dialog_SmLockerBox.cancel();
        }

        if (dialog_Confirm != null) {
            dialog_Confirm.cancel();
        }
    }
}
