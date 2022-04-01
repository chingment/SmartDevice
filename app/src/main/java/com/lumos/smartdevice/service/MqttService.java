package com.lumos.smartdevice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

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


public class MqttService extends Service {

    private static final String TAG = "MqttService";

    private static MqttAndroidClient mqttAndroidClient;
    private static MqttConnectOptions mMqttConnectOptions;

    private String clientId="";
    private String host = "";
    private String userName = "";
    private String password = "";
    private String deviceName="";
    private String deviceClass="";

    private static String topic_Subscribe="";//订阅主题
    private static String topic_Pubish="";//发布主题

    private Handler handler_msg;

    @Override
    public void onCreate() {
        super.onCreate();


        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
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
                mqttAndroidClient.subscribe(topic_Subscribe, 1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            LogUtil.i(TAG,"连接失败");
        }
    };

    private final MqttCallback mqttCallback = new MqttCallbackExtended() {  //回传
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LogUtil.d(TAG,"连接成功："+reconnect+",serverURI:"+serverURI);
            try {
                if (mqttAndroidClient != null) {
                    mqttAndroidClient.subscribe(topic_Subscribe, 1);
                }
            }
            catch (Exception ex){
                LogUtil.e(TAG,ex);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            LogUtil.d(TAG,"连接断开");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {  // 接收的消息


        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private void buildMqttClient() {

        closeMqttClient();

        topic_Subscribe ="/" + deviceClass + "/" + deviceName + "/user/get";
        topic_Pubish = "/" + deviceClass + "/" + deviceName + "/user/update";

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), host, clientId);

        mMqttConnectOptions = new MqttConnectOptions();
        // 在重新启动和重新连接时记住状态
        //mMqttConnectOptions.setCleanSession(true);
        // 设置连接的用户名
        mMqttConnectOptions.setUserName(userName);
        // 设置密码connect-onFailure-java
        mMqttConnectOptions.setPassword(password.toCharArray());
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

    public static void publish(String payload,int qos) {
        String topic = topic_Pubish;
        Boolean retained = false;
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

        //UUID.randomUUID().toString().replace("-","")

        JSONObject obj_Payload = new JSONObject();

        try {
            obj_Payload.put("id", id);
            obj_Payload.put("method", method);
            obj_Payload.put("params",params);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String str_Payload=obj_Payload.toString();

        publish(str_Payload,qos);

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
