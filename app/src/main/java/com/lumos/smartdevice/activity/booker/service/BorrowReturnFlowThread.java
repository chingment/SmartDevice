package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
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
import com.lumos.smartdevice.devicectrl.TagInfo;
import com.lumos.smartdevice.own.AppLogcatManager;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BorrowReturnFlowThread extends Thread {

    private static final String TAG = "BorrowReturnFlowThread";

    public static final String ACTION_TIPS = "tips";//开始
    public static final String ACTION_FLOW_START = "flow_start";//开始
    public static final String ACTION_INIT_DATA = "init_data";//初始数据
    public static final String ACTION_INIT_DATA_SUCCESS = "init_data_success";//初始数据成功
    public static final String ACTION_INIT_DATA_FAILURE = "init_data_failure";//初始数据失败 返回 1
    public static final String ACTION_REQUEST_OPEN_AUTH = "request_open_auth";//请求是否允许打开
    public static final String ACTION_REQUEST_OPEN_AUTH_SUCCESS = "request_open_auth_success";//请求允许打开
    public static final String ACTION_REQUEST_OPEN_AUTH_FAILURE = "request_open_auth_failure";//请求不允许打开 返回 1
    public static final String ACTION_SEND_OPEN_COMMAND = "send_open_command";//发送打开命令
    public static final String ACTION_SEND_OPEN_COMMAND_SUCCESS = "send_open_command_success";//发送打开命令成功
    public static final String ACTION_SEND_OPEN_COMMAND_FAILURE = "send_open_command_failure";//发送打开命令失败 返回 1
    public static final String ACTION_WAIT_OPEN="wait_open";//等待打开
    public static final String ACTION_OPEN_SUCCESS= "open_success";//打开成功
    public static final String ACTION_OPEN_FAILURE = "open_failure";//打开失败  返回 1
    public static final String ACTION_WAIT_CLOSE="wait_close";//等待关闭
    public static final String ACTION_CLOSE_SUCCESS = "close_success";//关闭成功
    public static final String ACTION_CLOSE_FAILURE = "close_failure";//关闭失败 ？如何处理？重试？
    public static final String ACTION_REQUEST_CLOSE_AUTH="request_close_auth";//请求关闭验证
    public static final String ACTION_REQUEST_CLOSE_AUTH_SUCCESS="request_close_auth_success";//关闭验证通过
    public static final String ACTION_REQUEST_CLOSE_AUTH_FAILURE="request_close_auth_failure";//关闭验证不通   返回  8
    public static final String ACTION_FLOW_END = "flow_end";
    public static final String ACTION_EXCEPTION = "exception";

    private final DeviceVo device;
    private final BookerSlotVo slot;
    private String flowId;
    private final String deviceId;
    private final String slotId;
    private final String clientUserId;
    private final int identityType;
    private final String identityId;

    private static final Map<String, Object> brFlowDo = new ConcurrentHashMap<>();

    private final OnHandlerListener onHandlerListener;

    public BorrowReturnFlowThread(String clientUserId, int identityType, String identityId, DeviceVo device, BookerSlotVo slot, OnHandlerListener onHandlerListener) {
        this.clientUserId = clientUserId;
        this.identityType = identityType;
        this.identityId = identityId;
        this.device = device;
        this.deviceId = device.getDeviceId();
        this.slot = slot;
        this.slotId = slot.getSlotId();
        this.onHandlerListener = onHandlerListener;
        this.setName("BorrowReturnFlow-" + slotId);
    }

    private void setRunning(boolean isRunning) {
        if(isRunning) {
            brFlowDo.put(slotId, true);
        }
        else{
            brFlowDo.remove(slotId);
        }
    }

    public static void clear(){
        brFlowDo.clear();
    }

    @Override
    public void run() {
        super.run();

        synchronized (BorrowReturnFlowThread.class) {
            if (brFlowDo.containsKey(slotId)) {
                LogUtil.d(TAG, slotId + ":借阅任务正在执行");
                sendHandlerMessage(ACTION_TIPS, "正在执行中");
                return;
            }
            else {
                LogUtil.d(TAG, slotId + ":借阅任务开始执行");
                setRunning(true);
            }
        }

        doTask();
    }

    private List<String> getRfIds(Map<String, TagInfo> map_TagInfos){

        List<String> rfIds=new ArrayList<>();

        if(map_TagInfos!=null) {
            Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
            for (Map.Entry<String, TagInfo> entry : entrys) {

                rfIds.add(entry.getKey());
            }
        }

        return rfIds;

    }

    private void  doTask() {

        RopBookerCreateFlow rop = new RopBookerCreateFlow();
        rop.setDeviceId(device.getDeviceId());
        rop.setSlotId(slot.getSlotId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);
        rop.setType(1);

        ResultBean<RetBookerCreateFlow> result_CreateFlow = ReqInterface.getInstance().bookerCreateFlow(rop);

        if (result_CreateFlow.getCode() != ResultCode.SUCCESS) {
            sendHandlerMessage(ACTION_TIPS, result_CreateFlow.getMsg());
            setRunning(false);
            return;
        }

        RetBookerCreateFlow ret_CreateFlow = result_CreateFlow.getData();

        flowId = ret_CreateFlow.getFlowId();

        try {

            sendHandlerMessage(ACTION_FLOW_START, "借还开始");

            sendHandlerMessage(ACTION_INIT_DATA, "设备初始数据检查");

            HashMap<String, DriveVo> drives = device.getDrives();

            if (drives == null || drives.size() == 0) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "设备未配置驱动[01]");
                setRunning(false);
                return;
            }

            if (drives.size() < 2) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "设备驱动数量不对[02]");
                setRunning(false);
                return;
            }

            BookerSlotDrivesVo slot_Drives = slot.getDrives();

            if (slot_Drives == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "设备未配置驱动[03]");
                setRunning(false);
                return;
            }

            BookerDriveLockeqVo lockeq = slot_Drives.getLockeq();
            if (lockeq == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "格子未配置锁驱动[04]");
                setRunning(false);
                return;
            }

            if (!drives.containsKey(lockeq.getDriveId())) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "格子驱动找不到[05]");
                setRunning(false);
                return;
            }

            BookerDriveRfeqVo rfeq = slot_Drives.getRfeq();
            if (rfeq == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频未配置驱动[06]");
                setRunning(false);
                return;
            }

            if (!drives.containsKey(rfeq.getDriveId())) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频驱动找不到[07]");
                setRunning(false);
                return;
            }


            DriveVo lockeqDrive = drives.get(lockeq.getDriveId());

            if (lockeqDrive == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "格子驱动找不到[08]");
                setRunning(false);
                return;
            }

            DriveVo rfeqDrive = drives.get(rfeq.getDriveId());

            if (rfeqDrive == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频驱动找不到[09]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_INIT_DATA_SUCCESS, "初始化数据成功");

            ILockeqCtrl  lockeqCtrl = LockeqCtrlInterface.getInstance(lockeqDrive.getComId(), lockeqDrive.getComBaud(), lockeqDrive.getComPrl());
            IRfeqCtrl rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(), rfeqDrive.getComBaud(), rfeqDrive.getComPrl());


            if (!lockeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "格子驱动未连接[10]");
                setRunning(false);
                return;
            }

            if (!rfeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频驱动未连接[11]");
                setRunning(false);
                return;
            }


            Thread.sleep(200);

            if (!rfeqCtrl.sendOpenRead(1)) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频设备命令发送失败[12]");
                setRunning(false);
                return;
            }

            //todo 读多久;

            Thread.sleep(200);

            rfeqCtrl.sendCloseRead(1);

            Map<String, TagInfo> tag_RfIds=rfeqCtrl.getRfIds(1);

            HashMap<String, Object> ad_Request_Open_Auth = new HashMap<>();

            List<String> open_RfIds=getRfIds(tag_RfIds);

            open_RfIds.add("123456789012345678901410");
            open_RfIds.add("123456789012345678901409");
            open_RfIds.add("123456789012345678901408");
            open_RfIds.add("123456789012345678901407");
            open_RfIds.add("123456789012345678901403");
            open_RfIds.add("123456789012345678901402");
            open_RfIds.add("123456789012345678901401");


            ad_Request_Open_Auth.put("rfIds", open_RfIds);


            ResultBean<RetBookerBorrowReturn> result_Request_Open_Auth = borrowReturn(ACTION_REQUEST_OPEN_AUTH, ad_Request_Open_Auth, "请求是否允许打开设备");

            if (result_Request_Open_Auth.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备[14]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");

            boolean isSendOpenSlot = lockeqCtrl.sendOpenSlot("1");

            if (!isSendOpenSlot) {
                sendHandlerMessage(ACTION_SEND_OPEN_COMMAND_FAILURE, "打开命令发送失败[15]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_SEND_OPEN_COMMAND_SUCCESS, "打开命令发送成功");

            sendHandlerMessage(ACTION_WAIT_OPEN, "等待打开");


            boolean isOpen = false;
            long nCheckMinute = 1;
            long nCheckStartTime = System.currentTimeMillis();
            long nCheckMaxStatusTime = System.currentTimeMillis() - nCheckStartTime;

            while (!isOpen && nCheckMaxStatusTime < nCheckMinute * 60 * 1000) {

                //LogUtil.i(TAG,"检查开门状态");

                int slotStatus = lockeqCtrl.getSlotStatus("1");

                if (slotStatus == 1) {
                    isOpen = true;
                } else {
                    Thread.sleep(300);
                }

                nCheckMaxStatusTime = System.currentTimeMillis() - nCheckStartTime;
            }

            if (!isOpen) {
                sendHandlerMessage(ACTION_OPEN_FAILURE, "打开失败[16]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_OPEN_SUCCESS, "打开成功");

            sendHandlerMessage(ACTION_WAIT_CLOSE, "等待关闭");

            boolean isClose = false;
            nCheckMinute = 30;
            nCheckStartTime = System.currentTimeMillis();
            nCheckMaxStatusTime = System.currentTimeMillis() - nCheckStartTime;
            while (!isClose && nCheckMaxStatusTime < nCheckMinute * 60 * 1000) {

                int slotStatus = lockeqCtrl.getSlotStatus("1");

                if (slotStatus == 0) {
                    isClose = true;
                } else {
                    Thread.sleep(300);
                }

                nCheckMaxStatusTime = System.currentTimeMillis() - nCheckStartTime;
            }


            //todo 关闭失败情况处理
            if (!isClose) {
                sendHandlerMessage(ACTION_CLOSE_FAILURE, "关闭失败");
            }

            //todo 关闭成功因网络问题上传的数量如何应对
            sendHandlerMessage(ACTION_CLOSE_SUCCESS, "关闭成功");

            rfeqCtrl.sendOpenRead(1);

            //todo 读多久
            Thread.sleep(2000);

            rfeqCtrl.sendCloseRead(1);

            tag_RfIds=rfeqCtrl.getRfIds(1);

            List<String> close_RfIds=getRfIds(tag_RfIds);

            close_RfIds.add("123456789012345678901403");
            close_RfIds.add("123456789012345678901402");
            close_RfIds.add("123456789012345678901401");

            HashMap<String, Object> ad_Request_Close_Auth = new HashMap<>();

            ad_Request_Close_Auth.put("rfIds", close_RfIds);

            ResultBean<RetBookerBorrowReturn> result_Request_Close_Auth = borrowReturn(ACTION_REQUEST_CLOSE_AUTH, ad_Request_Close_Auth, "请求是否允许关闭设备");

            if (result_Request_Close_Auth.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过[17]");
                setRunning(false);
                return;
            }

            RetBookerBorrowReturn ret_Request_Close_Auth = result_Request_Close_Auth.getData();

            HashMap<String, Object> ad_Request_Close_Auth_Result = new HashMap<>();

            ad_Request_Close_Auth.put("ret_booker_borrow_return", ret_Request_Close_Auth);

            sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_SUCCESS, ad_Request_Close_Auth_Result, "请求关闭验证通过");

            sendHandlerMessage(ACTION_FLOW_END, ad_Request_Close_Auth_Result, "借阅流程结束");

            setRunning(false);

        } catch (Exception ex) {
            sendHandlerMessage(ACTION_EXCEPTION, "发生异常");
            setRunning(false);
        }
    }

    private ResultBean<RetBookerBorrowReturn> borrowReturn(String actionCode,HashMap<String,Object> actionData,String actionRemark) {

        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setDeviceId(deviceId);
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setFlowId(flowId);
        if (actionData != null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }

        return ReqInterface.getInstance().bookerBorrowReturn(rop);

    }

    private void sendHandlerMessage(String actionCode,HashMap<String,Object> actionData,String actionRemark) {

        LogUtil.d(TAG,"actionCode:"+actionCode+",actionRemark:"+actionRemark);

        new Thread(() -> {
            if (!StringUtil.isEmpty(flowId)) {
                RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
                rop.setDeviceId(deviceId);
                rop.setActionCode(actionCode);
                rop.setActionTime(CommonUtil.getCurrentTime());
                rop.setActionRemark(actionRemark);
                rop.setFlowId(flowId);
                if (actionData != null) {
                    rop.setActionData(JSON.toJSONString(actionData));
                }

                ReqInterface.getInstance().bookerBorrowReturn(rop);
            }


            if(actionCode.contains("failure")||actionCode.contains("exception")){
                AppLogcatManager.uploadLogcat2Server("logcat -d -s BorrowReturnFlowThread RfeqCtrlByDs LockeqCtrlByDs ","test");
            }

        }).start();

        if (onHandlerListener != null) {
            BorrowReturnFlowResult result = new BorrowReturnFlowResult();
            result.setDeviceId(deviceId);
            result.setFlowId(flowId);
            result.setActionCode(actionCode);
            result.setActionData(actionData);
            result.setActionRemark(actionRemark);
            onHandlerListener.onResult(result);
        }
    }

    private void sendHandlerMessage(String actionCode,String actionRemark){
        sendHandlerMessage(actionCode, null, actionRemark);
    }

    public interface OnHandlerListener {
        void onResult(BorrowReturnFlowResult result);
    }

}
