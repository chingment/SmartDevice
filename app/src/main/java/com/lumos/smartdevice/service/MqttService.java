package com.lumos.smartdevice.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.MqttVo;
import com.lumos.smartdevice.app.AppCacheManager;
import com.lumos.smartdevice.app.CommandManager;
import com.lumos.smartdevice.utils.LogUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MqttService extends Service {

    private static final String TAG = "MqttService";

    @SuppressLint("StaticFieldLeak")
    private static MqttAndroidClient mqttAndroidClient;
    private static MqttConnectOptions mMqttConnectOptions;

    private Handler handler_msg;
    private Handler handler_connect;

    private static String subTopic="";//订阅主题
    private static String pubTopic="";//发布主题

    @Override
    public void onCreate() {
        super.onCreate();

        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle=msg.getData();
                String topic = bundle.getString("topic", "");
                String payload = bundle.getString("payload", "");

                LogUtil.d(TAG, "topic:" + topic);
                LogUtil.d(TAG, "payload:" + payload);

                if (topic.contains("/user/get")) {

                    Map map_payload = JSON.parseObject(payload);

                    String id = "";
                    String method = "";
                    String params = "";

                    if (map_payload.containsKey("id")) {
                        Object obj_id = map_payload.get("id");
                        if (obj_id != null) {
                            id = obj_id.toString();
                        }
                    }

                    if (map_payload.containsKey("method")) {
                        Object obj_method = map_payload.get("method");
                        if (obj_method != null) {
                            method = obj_method.toString();
                        }
                    }

                    if (map_payload.containsKey("params")) {
                        Object obj_params = map_payload.get("params");
                        if (obj_params != null) {
                            params = obj_params.toString();
                        }
                    }

                    publish(id, "msg_arrive", null, 1);

                    CommandManager.Execute(id, method, params);

                }

                return  false;
            }
        });

        handler_connect = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                connectMqttClient();
                return  false;
            }
        });

        buildMqttClient();

    }

    private final IMqttActionListener mqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            LogUtil.i(TAG,"连接成功");
            try {
                mqttAndroidClient.subscribe(subTopic, 1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            LogUtil.i(TAG,"连接失败");
            if(handler_connect!=null) {
                handler_connect.sendEmptyMessage(0);
            }
        }
    };

    private final MqttCallback mqttCallback = new MqttCallbackExtended() {  //回传
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LogUtil.d(TAG,"连接成功："+reconnect+",serverURI:"+serverURI);
            try {
                if (mqttAndroidClient != null) {
                    mqttAndroidClient.subscribe(subTopic, 1);
                }
            }
            catch (Exception ex){
                LogUtil.e(TAG,"connectComplete",ex);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            LogUtil.d(TAG,"连接断开");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {  // 接收的消息
            String payload = new String(message.getPayload());
            final Message m = new Message();
            m.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("topic", topic);
            bundle.putString("payload", payload);
            m.setData(bundle);
            handler_msg.sendMessage(m);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void buildMqttClient() {

        closeMqttClient();

        DeviceVo device = AppCacheManager.getDevice();

        MqttVo mqttVo = device.getMqtt();

        subTopic = mqttVo.getSubTopic();
        pubTopic = mqttVo.getPubTopic();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mqttVo.getHost(), mqttVo.getClientId());

        mMqttConnectOptions = new MqttConnectOptions();
        // 在重新启动和重新连接时记住状态
        mMqttConnectOptions.setCleanSession(false);
        // 设置连接的用户名
        mMqttConnectOptions.setUserName(mqttVo.getUserName());
        // 设置密码connect-onFailure-java
        mMqttConnectOptions.setPassword(mqttVo.getPassword().toCharArray());
        // 设置超时时间，单位：秒
        //mMqttConnectOptions.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        //mMqttConnectOptions.setKeepAliveInterval(20);
        mMqttConnectOptions.setAutomaticReconnect(true);
        mqttAndroidClient.setCallback(mqttCallback);// 回调

        connectMqttClient();

    }

    private synchronized void connectMqttClient() {
        if (mMqttConnectOptions == null)
            return;
        if (mqttAndroidClient == null)
            return;
        if (mqttAndroidClient.isConnected())
            return;

        try {
            mqttAndroidClient.connect(mMqttConnectOptions, getApplicationContext(), mqttActionListener);
            LogUtil.d(TAG, "连接中，ClientId：" + mqttAndroidClient.getClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void closeMqttClient(){
        LogUtil.d(TAG,"关闭连接");
        if (mqttAndroidClient != null){
            try {
                LogUtil.i(TAG,"关闭连接.ClientId："+mqttAndroidClient.getClientId());
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient.close();
                mqttAndroidClient.disconnect();
                mqttAndroidClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private static void publish(String payload,int qos,boolean retained) {
        String topic = pubTopic;
        try {
            if (mqttAndroidClient != null) {
                if (mqttAndroidClient.isConnected()) {
                    mqttAndroidClient.publish(topic, payload.getBytes(), qos, retained);
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void publish(String id, String method,JSONObject params, int qos) {

        JSONObject obj_Payload = new JSONObject();

        try {
            obj_Payload.put("id", id);
            obj_Payload.put("method", method);
            obj_Payload.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String str_Payload = obj_Payload.toString();

        publish(str_Payload, qos, false);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            closeMqttClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
