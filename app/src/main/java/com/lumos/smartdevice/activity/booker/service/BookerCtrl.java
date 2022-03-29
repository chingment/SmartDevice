package com.lumos.smartdevice.activity.booker.service;

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
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.BookerSlotDrivesVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookerCtrl {

    private static final String TAG = "BookerCtrl";

    private static BookerCtrl mThis= null;

    public static final String BR_ACTION_CODE_FLOW_START = "flow_start";//开始
    public static final String BR_ACTION_CODE_INIT_DATA = "init_data";//初始数据
    public static final String BR_ACTION_CODE_INIT_DATA_SUCCESS = "init_data_success";//初始数据成功
    public static final String BR_ACTION_CODE_INIT_DATA_FAILURE = "init_data_failure";//初始数据失败 返回 1
    public static final String BR_ACTION_CODE_REQUEST_OPEN_AUTH = "request_open_auth";//请求是否允许打开
    public static final String BR_ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS = "request_open_auth_success";//请求允许打开
    public static final String BR_ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE = "request_open_auth_failure";//请求不允许打开 返回 1
    public static final String BR_ACTION_CODE_SEND_OPEN_COMMAND = "send_open_command";//发送打开命令
    public static final String BR_ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS = "send_open_command_success";//发送打开命令成功
    public static final String BR_ACTION_CODE_SEND_OPEN_COMMAND_FAILURE = "send_open_command_failure";//发送打开命令失败 返回 1
    public static final String BR_ACTION_CODE_WAIT_OPEN="wait_open";//等待打开
    public static final String BR_ACTION_CODE_OPEN_SUCCESS= "open_success";//打开成功
    public static final String BR_ACTION_CODE_OPEN_FAILURE = "open_failure";//打开失败  返回 1
    public static final String BR_ACTION_CODE_WAIT_CLOSE="wait_close";//等待关闭
    public static final String BR_ACTION_CODE_CLOSE_SUCCESS = "close_success";//关闭成功
    public static final String BR_ACTION_CODE_CLOSE_FAILURE = "close_failure";//关闭失败 ？如何处理？重试？
    public static final String BR_ACTION_CODE_REQUEST_CLOSE_AUTH="request_close_auth";//请求关闭验证
    public static final String BR_ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS="request_close_auth_success";//关闭验证通过
    public static final String BR_ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE="request_close_auth_failure";//关闭验证不通   返回  8
    public static final String BR_ACTION_CODE_FLOW_END = "flow_end";
    public static final String BR_ACTION_CODE_EXCEPTION = "exception";


    private ILockeqCtrl lockeqCtrl;
    private IRfeqCtrl rfeqCtrl;


    private BookerCtrl(){

    }

    public static BookerCtrl getInstance() {

        if (mThis == null) {
            synchronized (BookerCtrl.class) {
                if (mThis == null) {
                    mThis = new BookerCtrl();
                }
            }
        }

        return mThis;
    }


    public void borrowReturnStart(DeviceVo device, BookerSlotVo slot, String flowId) {
        new BorrowReturnFlowThread(device,slot,flowId).start();

    }

    private void sendBorrowReturnHandlerMessage(String deviceId,String flowId,String actionCode,HashMap<String,Object> actionData,String actionRemark, ReqHandler reqHandler) {

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

        if (onHandlerListener != null) {
            BorrowReturnFlowResultVo result=new BorrowReturnFlowResultVo();
            result.setDeviceId(deviceId);
            result.setFlowId(flowId);
            result.setActionCode(actionCode);
            result.setActionData(actionData);
            result.setActionRemark(actionRemark);
            onHandlerListener.onBorrowReturn(result);
        }
    }

    private OnHandlerListener onHandlerListener=null;

    public void setHandlerListener(OnHandlerListener onHandlerListener) {
        this.onHandlerListener = onHandlerListener;
    }

    public  interface OnHandlerListener {
        void onBorrowReturn(BorrowReturnFlowResultVo result);
    }

    private Map<String, Object> brFlowDo = new ConcurrentHashMap<>();


   // private volatile HashMap  isRunning = false;

    private class BorrowReturnFlowThread extends Thread {

        private DeviceVo device;
        private BookerSlotVo slot;
        private String flowId;
        private String deviceId;
        private String slotId;

        private List<String> open_RfIds;
        private List<String> close_RfIds;


        private BorrowReturnFlowThread(DeviceVo device, BookerSlotVo slot, String flowId) {
            this.device = device;
            this.deviceId=device.getDeviceId();
            this.slot = slot;
            this.slotId=slot.getSlotId();
            this.flowId = flowId;
            this.setName(flowId);
        }


        private void setRunning(boolean isRunning) {
            if(isRunning) {
                brFlowDo.put(slotId, true);
            }
            else{
                brFlowDo.remove(slotId);
            }
        }

        @Override
        public void run() {
            super.run();


            boolean isDo = false;

            synchronized (BorrowReturnFlowThread.class) {
                if (brFlowDo.containsKey(slotId)) {
                    LogUtil.d(TAG, slotId + ":My有任务正在");
                } else {
                    setRunning(true);
                    isDo = true;
                }
            }

            if (isDo) {
                LogUtil.d(TAG, slotId + ":My有任务开始");

                open_RfIds = new ArrayList<>();
                close_RfIds = new ArrayList<>();

                try {

                    sendHandlerMessage(BR_ACTION_CODE_FLOW_START, "借还开始");

                    sendHandlerMessage(BR_ACTION_CODE_INIT_DATA, "设备初始数据检查");

                    if (device == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "设备未配置[01]");
                        setRunning(false);
                        return;
                    }

                    HashMap<String, DriveVo> drives = device.getDrives();

                    if (drives == null || drives.size() == 0) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "设备未配置驱动[02]");
                        setRunning(false);
                        return;
                    }

                    if (drives.size() < 2) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "设备驱动数量不对[03]");
                        setRunning(false);
                        return;
                    }

                    if (slot == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "格子未配置[04]");
                        setRunning(false);
                        return;
                    }

                    BookerSlotDrivesVo slot_Drives = slot.getDrives();

                    if (slot_Drives == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "设备未配置驱动[05]");
                        setRunning(false);
                        return;
                    }

                    BookerDriveLockeqVo lockeq = slot_Drives.getLockeq();
                    if (lockeq == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "格子未配置锁驱动[06]");
                        setRunning(false);
                        return;
                    }

                    if (!drives.containsKey(lockeq.getDriveId())) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "格子驱动找不到[07]");
                        setRunning(false);
                        return;
                    }

                    BookerDriveRfeqVo rfeq = slot_Drives.getRfeq();
                    if (rfeq == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "射频未配置驱动[08]");
                        setRunning(false);
                        return;
                    }

                    if (!drives.containsKey(rfeq.getDriveId())) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "射频驱动找不到[09]");
                        setRunning(false);
                        return;
                    }


                    DriveVo lockeqDrive = drives.get(lockeq.getDriveId());

                    if (lockeqDrive == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "格子驱动找不到[11]");
                        setRunning(false);
                        return;
                    }

                    DriveVo rfeqDrive = drives.get(rfeq.getDriveId());

                    if (rfeqDrive == null) {
                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "射频驱动找不到[12]");
                        setRunning(false);
                        return;
                    }

                    sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_SUCCESS, "初始化数据成功");

                    lockeqCtrl = LockeqCtrlInterface.getInstance(lockeqDrive.getComId(), lockeqDrive.getComBaud(), lockeqDrive.getComPrl());

                    rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(), rfeqDrive.getComBaud(), rfeqDrive.getComPrl());

//                    //todo 判断设备连接
//
//                    if (!rfeqCtrl.sendOpenRead(1)) {
//                        sendHandlerMessage(BR_ACTION_CODE_INIT_DATA_FAILURE, "设备命令发送失败[15]");
//                        isRunning = false;
//                        return;
//                    }
//
//                    rfeqCtrl.setReadHandler(new IRfeqCtrl.OnReadHandlerListener() {
//                        @Override
//                        public void onData(String rfId) {
//
//                            LogUtil.d(TAG, "open_rfid:" + rfId);
//
//                            if (!open_RfIds.contains(rfId)) {
//                                open_RfIds.add(rfId);
//                            }
//                        }
//                    });

                    Thread.sleep(1000 * 10);

                    HashMap<String, Object> open_ActionData = new HashMap<>();

                    open_RfIds.add("123456789012345678901410");
                    open_RfIds.add("123456789012345678901409");
                    open_RfIds.add("123456789012345678901408");
                    open_RfIds.add("123456789012345678901407");
                    open_RfIds.add("123456789012345678901403");
                    open_RfIds.add("123456789012345678901402");
                    open_RfIds.add("123456789012345678901401");

                    open_ActionData.put("rfIds", open_RfIds);

                    LogUtil.d(TAG, "open_rfIds" + JSON.toJSONString(open_RfIds));

                    rfeqCtrl.setReadHandler(null);

                    rfeqCtrl.sendCloseRead(1);


                    sendHandlerMessage(BR_ACTION_CODE_REQUEST_OPEN_AUTH, open_ActionData, "请求是否允许打开设备", new ReqHandler() {
                        @Override
                        public void onSuccess(String response) {
                            super.onSuccess(response);

                            ResultBean<RetBookerBorrowReturn> rt = JsonUtil.toResult(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                            });

                            if (rt.getCode() == ResultCode.SUCCESS) {

                                sendHandlerMessage(BR_ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");

                                lockeqCtrl.open("1", new ILockeqCtrl.OnListener() {
                                    @Override
                                    public void onSendCommandSuccess() {
                                        sendHandlerMessage(BR_ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS, "打开命令发送成功");
                                        sendHandlerMessage(BR_ACTION_CODE_WAIT_OPEN, "等待打开");
                                    }

                                    @Override
                                    public void onSendCommnadFailure() {
                                        sendHandlerMessage(BR_ACTION_CODE_SEND_OPEN_COMMAND_FAILURE, "打开命令发送失败");
                                        setRunning(false);
                                    }

                                    @Override
                                    public void onOpenFailure() {
                                        sendHandlerMessage(BR_ACTION_CODE_OPEN_FAILURE, "打开失败");
                                        setRunning(false);
                                    }

                                    @Override
                                    public void onOpenSuccess() {

                                        sendHandlerMessage(BR_ACTION_CODE_OPEN_SUCCESS, "打开成功");

                                        sendHandlerMessage(BR_ACTION_CODE_WAIT_CLOSE, "等待关闭");

//                                        try {
//                                            Thread.sleep(15 * 1000);
//                                        } catch (Exception ex) {
//
//                                        }
//
//                                        rfeqCtrl.sendOpenRead(1);
//                                        rfeqCtrl.setReadHandler(null);
//                                        try {
//                                            Thread.sleep(500);
//                                        } catch (Exception ex) {
//
//                                        }
//
//                                        rfeqCtrl.setReadHandler(new IRfeqCtrl.OnReadHandlerListener() {
//                                            @Override
//                                            public void onData(String rfId) {
//
////                                LogUtil.d(TAG,"close_rfid:"+rfId);
////                                if(!close_RfIds.contains(rfId)){
////                                    close_RfIds.add(rfId);
////                                }
//                                            }
//                                        });
//
//
//                                        try {
//                                            Thread.sleep(500);
//                                        } catch (Exception ex) {
//
//                                        }
//
//                                        rfeqCtrl.sendCloseRead(1);

                                        // LogUtil.d(TAG,"close_rfIds"+JSON.toJSONString(close_RfIds));


                                        close_RfIds.add("123456789012345678901403");
                                        close_RfIds.add("123456789012345678901402");
                                        close_RfIds.add("123456789012345678901401");


                                        // HashMap<String, Object> actionData = new HashMap<>();
                                        //  actionData.put("rfIds", close_RfIds);

                                        // rfeqCtrl.sendCloseRead(1);


                                        HashMap<String, Object> close_ActionData = new HashMap<>();

                                        close_ActionData.put("rfIds", close_RfIds);

                                        //todo 判断关闭是否成功
                                        sendHandlerMessage(BR_ACTION_CODE_CLOSE_SUCCESS, close_ActionData, "关闭成功", null);

                                        sendHandlerMessage(BR_ACTION_CODE_REQUEST_CLOSE_AUTH, close_ActionData, "请求关闭验证", new ReqHandler() {
                                            @Override
                                            public void onSuccess(String response) {
                                                super.onSuccess(response);
                                                ResultBean<RetBookerBorrowReturn> rt = JsonUtil.toResult(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                                                });

                                                if (rt.getCode() == ResultCode.SUCCESS) {
                                                    RetBookerBorrowReturn d = rt.getData();
                                                    HashMap<String, Object> m_ActionData = new HashMap<>();
                                                    m_ActionData.put("ret_booker_borrow_return", d);
                                                    sendHandlerMessage(BR_ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS, m_ActionData, "请求关闭验证通过", null);

                                                    sendHandlerMessage(BR_ACTION_CODE_FLOW_END, m_ActionData, "借阅流程结束", null);

                                                } else {
                                                    //todo 验证不通过的流程
                                                    sendHandlerMessage(BR_ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                                                }
                                            }

                                            @Override
                                            public void onFailure(String msg, Exception e) {
                                                super.onFailure(msg, e);
                                                sendHandlerMessage(BR_ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                                            }
                                        });
                                    }
                                });

                            } else {
                                sendHandlerMessage(BR_ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                                setRunning(false);
                            }
                        }

                        @Override
                        public void onFailure(String msg, Exception e) {
                            super.onFailure(msg, e);
                            sendHandlerMessage(BR_ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                            setRunning(false);
                        }
                    });


                } catch (InterruptedException e) {
                    sendHandlerMessage(BR_ACTION_CODE_EXCEPTION, "发生异常");
                    setRunning(false);
                }
            }
        }


        private void sendHandlerMessage(String actionCode,HashMap<String,Object> actionData,String actionRemark, ReqHandler reqHandler){
            sendBorrowReturnHandlerMessage(deviceId, flowId,actionCode, actionData, actionRemark,reqHandler);
        }

        private void sendHandlerMessage(String actionCode,HashMap<String,Object> actionData,String actionRemark){
            sendHandlerMessage(actionCode, actionData, actionRemark,null);
        }

        private void sendHandlerMessage(String actionCode,String actionRemark, ReqHandler reqHandler){
            sendHandlerMessage(actionCode, null, actionRemark,reqHandler);
        }

        private void sendHandlerMessage(String actionCode,String actionRemark){
            sendHandlerMessage(actionCode, null, actionRemark,null);
        }
    }
}
