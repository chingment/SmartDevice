package com.lumos.smartdevice.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.BookerMainActivity;
import com.lumos.smartdevice.activity.locker.LockerMainActivity;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.adapter.LogTipsAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetDeviceInitData;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.api.vo.BookerCustomDataVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.LogTipsBean;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.app.AppVar;
import com.lumos.smartdevice.service.MqttService;
import com.lumos.smartdevice.service.TimerTaskService;
import com.lumos.smartdevice.ui.my.MyListView;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.StringUtil;
import com.lumos.smartdevice.utils.runtimepermissions.PermissionsManager;
import com.lumos.smartdevice.utils.runtimepermissions.PermissionsResultAction;
import com.lumos.smartdevice.ui.shapeloading.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InitDataActivity extends BaseActivity {
    private static final String TAG = "InitDataActivity";
    private TextView tv_DeviceId;
    private TextView tv_AppVersion;
    private MyListView ls_Logs;
    private LinearLayout btn_HelpTool;
    private LoadingView ld_Animation;

    private Handler handler_msg;
    private final List<LogTipsBean> log_tips=new ArrayList<>();
    private boolean initActionIsRun=false;
    private final Handler initActionHandler = new Handler();
    private final Runnable initActionRunable = new Runnable() {

        @Override
        public void run() {

            if (!initActionIsRun) {
                initActionIsRun = true;
                setDeviceInitData();
            }

            initActionHandler.postDelayed(this, 2000);
        }
    };

    public final int WHAT_TIPS = 1;
    public final int WHAT_READ_CONFIG_SUCCESS = 2;
    public final int WHAT_READ_CONFIG_FAILURE = 3;
    public final int WHAT_SET_CONFIG_SUCCESS = 4;
    public final int WHAT_SET_CONFIG_FALURE = 5;


    public static  String[] byteToBit(int i) {

        byte b = (byte) i;

        String s_b="" + (byte) ((b >> 0) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 7) & 0x1);

        String[] arr_s=new String[s_b.length()];

        for (int x = 0; x<arr_s.length; x++){
            arr_s[x]=s_b.substring(x,x+1);
        }

        return arr_s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init_data);


        ILockeqCtrl lockeqCtrl = LockeqCtrlInterface.getInstance("ttyS4", 9600, "sdas");
        lockeqCtrl.sendOpenSlot("1,3");

        //lockeqCtrl.setLight("1,128");

        lockeqCtrl.getSlotStatus("1,3");

//        String s=byteToBit((byte) 32);
//
//
//        String[] arr_s=new String[s.length()];
//
//        for (int i = 0; i<arr_s.length; i++){
//            arr_s[i]=s.substring(i,i+1);
//        }
//
//
//        char c=s.toCharArray()[8-3];
//
//
//
//
//        int bb=c-0;


        Intent mqttService = new Intent(this, MqttService.class);
        stopService(mqttService);

        Intent timerTaskService = new Intent(this, TimerTaskService.class);
        startService(timerTaskService);

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                DbManager.getInstance().init();
            }

            @Override
            public void onDenied(String permission) {
            }
        });

        OstCtrlInterface.getInstance().setHideStatusBar(this,false);

        initView();
        initEvent();
        initData();

    }


    private void initView() {

        tv_DeviceId = findViewById(R.id.tv_DeviceId);
        tv_AppVersion = findViewById(R.id.tv_AppVersion);
        ls_Logs = findViewById(R.id.ls_Logs);
        btn_HelpTool= findViewById(R.id.btn_HelpTool);
        ld_Animation=findViewById(R.id.ld_Animation);
    }

    private void initEvent() {

        LongClickUtil.setLongClick(new Handler(), btn_HelpTool, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene_mode","init_data_help");
                openActivity(intent);
                //finish();
                return true;
            }
        });


        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                Bundle bundle = msg.getData();

                if (bundle == null)
                    return false;

                String tips = bundle.getString("tips", "");

                if (StringUtil.isEmptyNotNull(tips)) {
                    return false;
                }

                ld_Animation.setLoadingText(tips);

                LogTipsBean log_tip = new LogTipsBean();

                Date currentTime = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
                String dateString = sdf.format(currentTime);

                log_tip.setDateTime(dateString);
                log_tip.setContent(tips);
                log_tips.add(log_tip);

                List<LogTipsBean> top_logs = new ArrayList<>();

                for (int i = log_tips.size(); i > 0; i--) {
                    if (top_logs.size() > 10) {
                        break;
                    }
                    top_logs.add(log_tips.get(i - 1));
                }

                LogTipsAdapter logTipsAdapter = new LogTipsAdapter(InitDataActivity.this, top_logs);
                ls_Logs.setAdapter(logTipsAdapter);

                switch (msg.what) {
                    case WHAT_TIPS:
                        break;
                    case WHAT_READ_CONFIG_SUCCESS:
                        setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_setting_config));

                        if (bundle.getSerializable("ret_device_init_data") == null) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, getAppContext().getString(R.string.aty_initdata_tips_setting_config_is_null));
                            return false;
                        }

                        RetDeviceInitData initData = (RetDeviceInitData) bundle.getSerializable("ret_device_init_data");//全局数据

                        if (initData == null) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, getAppContext().getString(R.string.aty_initdata_tips_setting_object_is_null));
                            return false;
                        }

                        final DeviceVo device = initData.getDevice();//设备数据

                        if (device == null || StringUtil.isEmptyNotNull(device.getDeviceId())) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, getAppContext().getString(R.string.aty_initdata_tips_setting_device_is_null));
                            return false;
                        }

                        AppCacheManager.setDevice(device);

                        final int scene_mode=device.getSceneMode();


                        if(scene_mode==AppVar.SCENE_MODE_2) {
                            BookerCustomDataVo bookerCustomData = JsonUtil.toObject(initData.getCustomData(), BookerCustomDataVo.class);
                            AppCacheManager.setBookerCustomData(bookerCustomData);
                        }

                        setHandleMessage(WHAT_SET_CONFIG_SUCCESS, getAppContext().getString(R.string.aty_initdata_tips_setting_complete));

                        OstCtrlInterface.getInstance().setHideStatusBar(InitDataActivity.this,true);

                        new Thread(new Runnable() {
                            public void run() {

                                //SystemClock.sleep(6000);


                                setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_setting_end));


                                Intent mqttService = new Intent(getAppContext(), MqttService.class);
                                startService(mqttService);

                                if (scene_mode==AppVar.SCENE_MODE_1) {
                                    Intent intent = new Intent(getAppContext(), LockerMainActivity.class);
                                    openActivity(intent);
                                    finish();
                                }
                                else if(scene_mode==AppVar.SCENE_MODE_2){

                                    Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                                    openActivity(intent);
                                    finish();
                                }
                            }
                        }).start();

                        break;
                    case WHAT_READ_CONFIG_FAILURE:
                        setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_retry_read_config));
                        initActionIsRun = false;
                        break;
                }

                return false;
            }
        });
    }

    private void initData() {
        tv_DeviceId.setText(DeviceUtil.getDeviceId());
        tv_AppVersion.setText(BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onDestroy() {


        if (initActionHandler != null && initActionRunable != null) {
            initActionHandler.removeCallbacks(initActionRunable);
        }

        super.onDestroy();
    }



    @Override
    protected void onResume() {
        super.onResume();

        if(initActionHandler!=null&&initActionRunable!=null) {
            initActionHandler.postDelayed(initActionRunable, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(initActionHandler!=null&&initActionRunable!=null) {
            initActionHandler.removeCallbacks(initActionRunable);
        }

    }

    public void setHandleMessage(int what, String tips, RetDeviceInitData retDeviceInitData) {
        if (handler_msg == null)
            return;
        final Message m = new Message();
        m.what = what;
        Bundle bundle = new Bundle();
        bundle.putString("tips", tips);
        if (retDeviceInitData != null) {
            bundle.putSerializable("ret_device_init_data", retDeviceInitData);
        }
        m.setData(bundle);
        handler_msg.sendMessage(m);
    }

    public void setHandleMessage(int what, String msg) {
        setHandleMessage(what,msg,null);
    }

    private void setDeviceInitData(){

        setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_settingdevice));

        int version_mode=DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_VERSION_MODE);
        if(version_mode==AppVar.VERSION_MODE_0) {
            initActionIsRun = false;
            setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_verion_mode));
            return;
        }

        int scene_mode= DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_SCENE_MODE);
        if(scene_mode==AppVar.SCENE_MODE_0) {
            initActionIsRun=false;
            setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_scene_mode));
            return;
        }

        if(version_mode==AppVar.VERSION_MODE_1){
            if(DbManager.getInstance().getCabinets().size()==0) {
                initActionIsRun = false;
                setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_set_cabinet));
                return;
            }
        }

        RopDeviceInitData rop=new RopDeviceInitData();
        rop.setDeviceId(DeviceUtil.getDeviceId());
        rop.setImeiId(DeviceUtil.getImeiId());
        rop.setMacAddr(DeviceUtil.getMacAddr());
        rop.setCtrlVerName(DeviceUtil.getCtrlVerName());
        rop.setAppVerCode(String.valueOf(BuildConfig.VERSION_CODE));
        rop.setAppVerName(BuildConfig.VERSION_NAME);
        rop.setSysVerName(android.os.Build.VERSION.RELEASE);

        ReqInterface.getInstance().deviceInitData(rop, new ReqHandler(){

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);


                ResultBean<RetDeviceInitData> rt = JsonUtil.toResult(response, new TypeReference<ResultBean<RetDeviceInitData>>() {
                });

                if(rt.getCode()==ResultCode.SUCCESS){
                    setHandleMessage(WHAT_READ_CONFIG_SUCCESS, getAppContext().getString(R.string.aty_initdata_tips_read_config_success),rt.getData());
                }
                else {
                    setHandleMessage(WHAT_READ_CONFIG_FAILURE, rt.getMsg());
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                super.onFailure(msg, e);

                setHandleMessage(WHAT_READ_CONFIG_FAILURE, msg);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        DbManager.getInstance().init();

//        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                showToast("授权成功");
//            } else {
//                showToast("授权失败,请去设置打开权限");
//            }
//        }
    }

    public void showLoading(Context context) {

    }

    public void hideLoading(Context context) {

    }
}
