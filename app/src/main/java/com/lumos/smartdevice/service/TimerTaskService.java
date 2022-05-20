package com.lumos.smartdevice.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.InitDataActivity;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.vo.TripMsgBean;
import com.lumos.smartdevice.app.AppManager;
import com.lumos.smartdevice.app.AppUtil;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TimerTaskService extends Service {
    private static final String TAG = "TimerTaskService";

    private final static int FOREGROUND_ID = 1000;

    private Timer timerByUploadTripMsgs;

    private Timer timerByKeepAppLive;

    @Override
    public void onCreate() {
        uploadTripMsgs();
        sendDeviceStauts();
        if(!BuildConfig.DEBUG) {
            keepAppLive();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher_round);
            builder.setContentTitle("Foreground");
            builder.setContentText("I am a foreground service");
            builder.setContentInfo("Content Info");
            builder.setWhen(System.currentTimeMillis());
            Intent activityIntent = new Intent(this, InitDataActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            startForeground(FOREGROUND_ID, notification);
        }catch (Exception ex){
            LogUtil.d(TAG,ex);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){

        if(timerByUploadTripMsgs!=null){
            timerByUploadTripMsgs.cancel();
        }

        if(timerByKeepAppLive!=null){
            timerByKeepAppLive.cancel();
        }

        super.onDestroy();
    }

    private void uploadTripMsgs(){

        TimerTask  task = new TimerTask() {
            @Override
            public void run() {
                //LogUtil.d(TAG,"正在执行上传离线消息");

                List<TripMsgBean> tripMsgs=DbManager.getInstance().getTripMsgs();
                if(tripMsgs!=null&&tripMsgs.size()>0 ){

                    for (TripMsgBean tripMsg: tripMsgs  ) {
                        if(tripMsg.getCreateTime()+6000< System.currentTimeMillis()) {
                            com.alibaba.fastjson.JSONObject params = JSON.parseObject(tripMsg.getContent());
                            params.put("msgId", tripMsg.getMsgId());
                            params.put("msgMode", "timer");
                            String respone = HttpClient.myPost(tripMsg.getPostUrl(), params);
                            if (!StringUtil.isEmpty(respone)) {
                                ResultBean<Object> result = JsonUtil.toResult(respone, new TypeReference<ResultBean<Object>>() {
                                });
                                if (result.getCode() == ResultCode.SUCCESS||result.getCode()==2001) {
                                    DbManager.getInstance().deleteTripMsg(tripMsg.getMsgId());
                                }
                            }
                        }
                    }
                }

            }
        };

        timerByUploadTripMsgs = new Timer();
        timerByUploadTripMsgs.schedule(task, 2000, 2000);
    }

    private void keepAppLive(){

        TimerTask  task = new TimerTask() {
            @Override
            public void run() {
                //LogUtil.d(TAG,"正在监听保活服务");

                if(!isForeground(getApplicationContext())){
                    Intent intent = getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
                    startActivity(intent);
                }
            }
        };

        timerByKeepAppLive = new Timer();
        timerByKeepAppLive.schedule(task, 2000, 2000);

    }

    private void sendDeviceStauts(){


        TimerTask  task = new TimerTask() {
            @Override
            public void run() {
                //LogUtil.d(TAG,"发送设备状态");
                try {
                    Activity activity = AppManager.getAppManager().currentActivity();
                    JSONObject params = new JSONObject();
                    params.put("activity", activity == null ? "" : activity.getLocalClassName());
                    params.put("status", AppUtil.getDeviceStatus());
                    MqttService.publish(UUID.randomUUID().toString().replace("-", ""), "device_status", params, 1);
                } catch (Exception ex) {
                    LogUtil.e(TAG,ex);
                }
            }
        };

        timerByKeepAppLive = new Timer();
        timerByKeepAppLive.schedule(task, 2000, 2000);

    }

    private boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            if(processes!=null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : processes) {

                    if (processInfo.processName.equals(context.getPackageName())) {

                        //Log.i(TAG, "processInfo.processName：" + processInfo.processName);
                        //Log.i(TAG, "context.getPackageName()：" + context.getPackageName());
                        //Log.i(TAG, "processInfo.importance：" + processInfo.importance);

                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
