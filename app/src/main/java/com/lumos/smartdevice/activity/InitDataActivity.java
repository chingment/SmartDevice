package com.lumos.smartdevice.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.scenelocker.LockerMainActivity;
import com.lumos.smartdevice.activity.sm.SmLoginActivity;
import com.lumos.smartdevice.adapter.LogTipsAdapter;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.LogTipsBean;
import com.lumos.smartdevice.model.api.DeviceInitDataResultBean;
import com.lumos.smartdevice.own.AppCacheManager;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyListView;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.LongClickUtil;
import com.lumos.smartdevice.utils.StringUtil;
import com.lumos.smartdevice.widget.shapeloading.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InitDataActivity extends BaseFragmentActivity {

    private TextView tv_DeviceId;
    private TextView tv_VersionName;
    private MyListView ls_Logs;
    private LinearLayout btn_HelpTool;
    private LoadingView ld_Animation;

    private Handler handler_msg;
    private List<LogTipsBean> log_tips=new ArrayList<>();
    private boolean initActionIsRun=false;
    private Handler initActionHandler = new Handler();
    private Runnable initActionRunable = new Runnable() {

        @Override
        public void run() {

            if(!initActionIsRun) {
                initActionIsRun = true;
                setDeviceInitData();
            }

            initActionHandler.postDelayed(this, 1000);
        }
    };

    public final int WHAT_TIPS = 1;
    public final int WHAT_READ_CONFIG_SUCCESS = 2;
    public final int WHAT_READ_CONFIG_FAILURE = 3;
    public final int WHAT_SET_CONFIG_SUCCESS = 4;
    public final int WHAT_SET_CONFIG_FALURE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initdata);

        initView();
        initEvent();
        initData();


        initActionHandler.postDelayed(initActionRunable, 1000);

    }


    private void initView() {

        tv_DeviceId = findViewById(R.id.tv_DeviceId);
        tv_VersionName = findViewById(R.id.tv_VersionName);
        ls_Logs = findViewById(R.id.ls_Logs);
        btn_HelpTool= findViewById(R.id.btn_HelpTool);
        ld_Animation=findViewById(R.id.ld_Animation);
    }

    private void initEvent() {

        LongClickUtil.setLongClick(new Handler(), btn_HelpTool, 500, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getAppContext(), SmLoginActivity.class);
                intent.putExtra("scene","init_data_help");
                startActivity(intent);
                //finish();
                return true;
            }
        });


        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if (msg == null)
                    return false;

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
                        setHandleMessage(WHAT_TIPS, "正在配置设备信息");

                        if (bundle.getSerializable("deviceInitDataResultBean") == null) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, "配置设备信息失败：初始数据为空");
                            return false;
                        }

                        DeviceInitDataResultBean initData = (DeviceInitDataResultBean) bundle.getSerializable("deviceInitDataResultBean");//全局数据

                        if (initData == null) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, "配置设备信息失败：初始对象为空");
                            return false;
                        }

                        final DeviceBean device = initData.getDevice();//设备数据

                        if (device == null || StringUtil.isEmptyNotNull(device.getDeviceId())) {
                            setHandleMessage(WHAT_SET_CONFIG_FALURE, "配置设备信息失败：设备对象为空");
                            return false;
                        }

                        AppCacheManager.setDevice(device);

                        setHandleMessage(WHAT_SET_CONFIG_SUCCESS, "信息配置完成，正在启动设备恢复原始状态");


                        new Thread(new Runnable() {
                            public void run() {
                                SystemClock.sleep(6000);


                                setHandleMessage(WHAT_TIPS, "配置结束，进入购物车界面");

                                if (device.getSceneMode().equals(AppVar.SCENE_MODE_1)) {
                                    Intent intent = new Intent(getAppContext(), LockerMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).start();

                        break;
                    case WHAT_READ_CONFIG_FAILURE:
                        setHandleMessage(WHAT_TIPS, "重新尝试读取设备信息");
                        initActionIsRun = false;
                        break;
                }

                return false;
            }
        });
    }

    private void initData() {
        tv_DeviceId.setText(DeviceUtil.getDeviceId());
        tv_VersionName.setText(BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(initActionHandler!=null&&initActionRunable!=null) {
            initActionHandler.removeCallbacks(initActionRunable);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        initActionHandler.postDelayed(initActionRunable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(initActionHandler!=null&&initActionRunable!=null) {
            initActionHandler.removeCallbacks(initActionRunable);
        }

    }

    public void setHandleMessage(int what, String tips, DeviceInitDataResultBean deviceInitDataResult) {
        final Message m = new Message();
        m.what = what;
        Bundle bundle = new Bundle();
        bundle.putString("tips", tips);
        if(deviceInitDataResult!=null) {
            bundle.putSerializable("deviceInitDataResultBean", deviceInitDataResult);
        }
        m.setData(bundle);
        handler_msg.sendMessage(m);
    }

    public void setHandleMessage(int what, String msg) {
        setHandleMessage(what,msg,null);
    }

    private void setDeviceInitData(){

        setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_settingdevice));

        String version_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);
        if(version_mode==null||version_mode.equals(AppVar.VERSION_MODE_0)) {
            initActionIsRun = false;
            setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_verion_mode));
            return;
        }

        String scene_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_SCENE_MODE);
        if(scene_mode==null||scene_mode.equals(AppVar.SCENE_MODE_0)) {
            initActionIsRun=false;
            setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_scene_mode));
            return;
        }

        if(version_mode.equals(AppVar.VERSION_MODE_1)){
            if(DbManager.getInstance().getCabinets().size()==0) {
                initActionIsRun = false;
                setHandleMessage(WHAT_TIPS, getAppContext().getString(R.string.aty_initdata_tips_set_cabinet));
                return;
            }
        }


        RopDeviceInitData rop=new RopDeviceInitData();
        rop.setDeviceId(DeviceUtil.getDeviceId());
        rop.setSceneMode(scene_mode);
        rop.setVesionMode(version_mode);

        ReqInterface.getInstance().deviceInitData(rop, new ReqHandler(){

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);

                ResultBean<DeviceInitDataResultBean> rt = JSON.parseObject(response, new TypeReference<ResultBean<DeviceInitDataResultBean>>() {
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
            }
        });
    }
}
