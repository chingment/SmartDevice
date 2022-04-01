package com.lumos.smartdevice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.vo.TripMsgBean;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskService extends Service {
    private static final String TAG = "TimerTaskService";


    private Timer timerByUploadTripMsgs;

    public TimerTaskService() {
        uploadTripMsgs();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        super.onDestroy();
    }

    private void uploadTripMsgs(){

        TimerTask  task = new TimerTask() {
            @Override
            public void run() {
                LogUtil.d(TAG,"正在执行上传离线消息");

                List<TripMsgBean> tripMsgs=DbManager.getInstance().getTripMsgs();
                if(tripMsgs!=null&&tripMsgs.size()>0 ){

                    for (TripMsgBean tripMsg: tripMsgs  ) {
                        com.alibaba.fastjson.JSONObject params = JSON.parseObject(tripMsg.getContent());
                        params.put("msgId", tripMsg.getMsgId());
                        params.put("msgMode", "timer");
                        String respone = HttpClient.myPost(tripMsg.getPostUrl(), params);
                        if(!StringUtil.isEmpty(respone)){
                            ResultBean<Object> result= JsonUtil.toResult(respone,new TypeReference<ResultBean<Object>>(){});
                            if(result.getCode()== ResultCode.SUCCESS){
                                DbManager.getInstance().deleteTripMsg(tripMsg.getMsgId());
                            }
                        }
                    }
                }

            }
        };

        timerByUploadTripMsgs = new Timer();
        timerByUploadTripMsgs.schedule(task, 2000, 2000);
    }
}
