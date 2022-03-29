package com.lumos.smartdevice.activity.booker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.vo.BookerDriveLockeqVo;
import com.lumos.smartdevice.api.vo.BookerDriveRfeqVo;
import com.lumos.smartdevice.api.vo.BookerSlotDrivesVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BookerCtrlService extends Service {

    private static final String TAG = "BookerCtrlService";

    public static final int ACTION_CODE_FLOW_START = 1;//开始
    public static final int ACTION_CODE_INIT_DATA = 2;//初始数据
    public static final int ACTION_CODE_INIT_DATA_SUCCESS = 3;//初始数据成功
    public static final int ACTION_CODE_INIT_DATA_FAILURE = 4;//初始数据失败 返回 1
    public static final int ACTION_CODE_REQUEST_OPEN_AUTH = 5;//请求是否允许打开
    public static final int ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS = 6;//请求允许打开
    public static final int ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE = 7;//请求不允许打开 返回 1
    public static final int ACTION_CODE_SEND_OPEN_COMMAND = 8;//发送打开命令
    public static final int ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS = 9;//发送打开命令成功
    public static final int ACTION_CODE_SEND_OPEN_COMMAND_FAILURE = 10;//发送打开命令失败 返回 1
    public static final int ACTION_CODE_WAIT_OPEN=11;//等待打开
    public static final int ACTION_CODE_OPEN_SUCCESS= 12;//打开成功
    public static final int ACTION_CODE_OPEN_FAILURE = 13;//打开失败  返回 1
    public static final int ACTION_CODE_WAIT_CLOSE=14;//等待关闭
    public static final int ACTION_CODE_CLOSE_SUCCESS= 15;//关闭成功
    public static final int ACTION_CODE_CLOSE_FAILURE = 16;//关闭失败 ？如何处理？重试？
    public static final int ACTION_CODE_REQUEST_CLOSE_AUTH=17;//请求关闭验证
    public static final int ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS=18;//关闭验证通过
    public static final int ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE=19;//关闭验证不通   返回  8
    public static final int ACTION_CODE_FLOW_END = 20;
    public static final int ACTION_CODE_EXCEPTION = 21;


    private CtrlBinder mCtrlBinder = null;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate...");

        mCtrlBinder = new CtrlBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand...");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind...");
        return mCtrlBinder;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy...");

        super.onDestroy();
    }

    private List<String> open_RfIds;
    private List<String> close_RfIds;
    private ILockeqCtrl lockeqCtrl;
    private IRfeqCtrl rfeqCtrl;
    private boolean openIsRunning=false;

    public void open(DeviceVo device, BookerSlotVo slot, String flowId) {
        new Thread(() -> {
            if (openIsRunning) {
                LogUtil.d(TAG, "有任务正在执行");
            } else {
                openIsRunning = true;
                open_RfIds=new ArrayList<>();
                close_RfIds=new ArrayList<>();
                String deviceId=device.getDeviceId();
                try {

                    sendBroadcastMsg(deviceId,flowId,ACTION_CODE_FLOW_START,"借还开始");

                    sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA,"设备初始数据检查");

                    HashMap<String, DriveVo> drives=device.getDrives();

                    if(drives==null||drives.size()==0) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "设备未配置驱动[02]");
                        return;
                    }

                    if(drives.size()<2) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "设备驱动数量不对[03]");
                        return;
                    }

                    if (slot == null) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "格子未配置[04]");
                        return;
                    }

                    BookerSlotDrivesVo slot_Drives=slot.getDrives();

                    if(slot_Drives==null) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "设备未配置驱动[05]");
                        return;
                    }

                    BookerDriveLockeqVo lockeq=slot_Drives.getLockeq();
                    if(lockeq==null) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "格子未配置锁驱动[06]");
                        return;
                    }

                    if(!drives.containsKey(lockeq.getDriveId())) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "格子驱动找不到[07]");
                        return;
                    }

                    BookerDriveRfeqVo rfeq=slot_Drives.getRfeq();
                    if(rfeq==null) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "射频未配置驱动[08]");
                        return;
                    }

                    if(!drives.containsKey(rfeq.getDriveId())) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "射频驱动找不到[09]");
                        return;
                    }


                    DriveVo lockeqDrive=drives.get(lockeq.getDriveId());

                    if(lockeqDrive==null){
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "格子驱动找不到[11]");
                        return;
                    }

                    DriveVo rfeqDrive=drives.get(rfeq.getDriveId());

                    if(rfeqDrive==null){
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "射频驱动找不到[12]");
                        return;
                    }

                    lockeqCtrl= LockeqCtrlInterface.getInstance(lockeqDrive.getComId(),lockeqDrive.getComBaud(),lockeqDrive.getComPrl());

                    rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(),rfeqDrive.getComBaud(),rfeqDrive.getComPrl());

                    //todo 判断设备连接

                    if (!rfeqCtrl.sendOpenRead(1)) {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_INIT_DATA_FAILURE, "设备命令发送失败[15]");
                        return;
                    }

                    rfeqCtrl.setReadHandler(new IRfeqCtrl.OnReadHandlerListener() {
                        @Override
                        public void onData(String rfId) {

                            LogUtil.d(TAG,"open_rfid:"+rfId);

                            if(!open_RfIds.contains(rfId)){
                                open_RfIds.add(rfId);
                            }
                        }
                    });


                    Thread.sleep(500);


                    HashMap<String,Object> actionData=new HashMap<>();


                    //open_RfIds.add("123456789012345678901410");
                    //open_RfIds.add("123456789012345678901409");
                    //open_RfIds.add("123456789012345678901408");
                    //open_RfIds.add("123456789012345678901407");
                    //open_RfIds.add("123456789012345678901406");

                    actionData.put("rfIds",open_RfIds);

                    LogUtil.d(TAG,"open_rfIds"+ JSON.toJSONString(open_RfIds));

                    rfeqCtrl.setReadHandler(null);

                    rfeqCtrl.sendCloseRead(1);


                    sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_OPEN_AUTH,actionData, "请求是否允许打开设备");


                } catch (InterruptedException e) {
                    sendBroadcastMsg(deviceId,flowId,ACTION_CODE_EXCEPTION,"发生异常");
                }

            }
        }).start();
    }

    public void bookerBorrowReturn(String deviceId,String flowId, String actionCode, HashMap<String,Object> actionData,String actionRemark, ReqHandler reqHandler) {

        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setDeviceId(deviceId);
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setFlowId(flowId);
        if (actionData != null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }
        ReqInterface.getInstance().bookerBorrowReturn(rop, reqHandler);
    }

    private void sendBroadcastMsg(String deviceId,String flowId,int actionCode, String actionRemark) {
        sendBroadcastMsg(deviceId, flowId, actionCode, null, actionRemark);
    }

    private void sendBroadcastMsg(String deviceId,String flowId,int actionCode, HashMap<String, Object> actionData, String actionRemark) {

        switch (actionCode) {
            case ACTION_CODE_FLOW_START:
                bookerBorrowReturn(deviceId,flowId,"flow_start", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA:
                bookerBorrowReturn(deviceId,flowId,"init_data", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"init_data_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"init_data_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH:
                bookerBorrowReturn(deviceId,flowId,"request_open_auth", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetBookerBorrowReturn>>() {});

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");
                        } else {
                            sendBroadcastMsg(deviceId,flowId, ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                    }
                });
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"request_open_auth_success", actionData, actionRemark, null);
                sendBroadcastMsg(deviceId,flowId,ACTION_CODE_SEND_OPEN_COMMAND, "发送打开命令");
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"request_open_auth_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND:
                bookerBorrowReturn(deviceId,flowId,"send_open_command", actionData, actionRemark, null);
                lockeqCtrl.open("1", new ILockeqCtrl.OnListener() {
                    @Override
                    public void onSendCommandSuccess() {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS, "打开命令发送成功");
                    }

                    @Override
                    public void onSendCommnadFailure() {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_SEND_OPEN_COMMAND_FAILURE, "打开命令发送失败");
                    }

                    @Override
                    public void onOpenSuccess() {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_OPEN_SUCCESS, "打开成功");

                        try {
                            Thread.sleep(15*1000);
                        } catch (Exception ex) {

                        }

                        rfeqCtrl.sendOpenRead(1);
                        rfeqCtrl.setReadHandler(null);
                        try {
                            Thread.sleep(500);
                        } catch (Exception ex) {

                        }

                        rfeqCtrl.setReadHandler(new IRfeqCtrl.OnReadHandlerListener() {
                            @Override
                            public void onData(String rfId) {

                                LogUtil.d(TAG,"close_rfid:"+rfId);
                                if(!close_RfIds.contains(rfId)){
                                    close_RfIds.add(rfId);
                                }
                            }
                        });


                        try {
                            Thread.sleep(500);
                        } catch (Exception ex) {

                        }

                        rfeqCtrl.sendCloseRead(1);

                        LogUtil.d(TAG,"close_rfIds"+JSON.toJSONString(close_RfIds));


                        //close_RfIds.add("123456789012345678901408");
                        //close_RfIds.add("123456789012345678901407");
                        //close_RfIds.add("123456789012345678901406");


                        HashMap<String, Object> actionData = new HashMap<>();
                        actionData.put("rfIds", close_RfIds);

                        rfeqCtrl.sendCloseRead(1);

                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_CLOSE_SUCCESS, actionData, "关闭成功");
                    }

                    @Override
                    public void onOpenFailure() {
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_OPEN_FAILURE, "打开失败");
                    }
                });
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"send_open_command_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"send_open_command_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_OPEN_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"open_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_OPEN_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"open_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_CLOSE_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"close_success", actionData, actionRemark, null);
                sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_CLOSE_AUTH, actionData, "请求是否允许关闭");
                break;
            case ACTION_CODE_CLOSE_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"close_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH:
                bookerBorrowReturn(deviceId,flowId,"request_close_auth", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JsonUtil.toResult(response,new TypeReference<ResultBean<RetBookerBorrowReturn>>() {});


                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetBookerBorrowReturn d = rt.getData();
                            HashMap<String, Object> m_ActionData = new HashMap<>();
                            m_ActionData.put("ret_booker_borrow_return", d);
                            sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS, m_ActionData, "请求关闭验证通过");
                        } else {
                            sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendBroadcastMsg(deviceId,flowId,ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                    }
                });
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS:
                bookerBorrowReturn(deviceId,flowId,"request_close_auth_success", actionData, actionRemark, null);
                sendBroadcastMsg(deviceId,flowId,ACTION_CODE_FLOW_END, actionData, "借还结束");
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"request_close_auth_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_FLOW_END:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"flow_end", actionData, actionRemark, null);
              //  reSet();
                break;
            case ACTION_CODE_EXCEPTION:
                openIsRunning = false;
                bookerBorrowReturn(deviceId,flowId,"exception", actionData, actionRemark, null);
                break;
        }

        Intent intent = new Intent();

        intent.setAction("action.booker.borrow.return.flow");

        BorrowReturnFlowResultVo flowResult=new BorrowReturnFlowResultVo();
        flowResult.setActionCode(actionCode);
        flowResult.setActionData(actionData);
        flowResult.setActionRemark(actionRemark);
        intent.putExtra("flow_result",actionRemark);
        sendBroadcast(intent);

    }

    public class CtrlBinder extends Binder {
        public void borrowReturnStart(DeviceVo device, BookerSlotVo slot, String flowId) {
            open(device,slot,flowId);
        }
    }
}
