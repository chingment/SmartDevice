package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.TagInfo;
import com.lumos.smartdevice.own.AppLogcatManager;
import com.lumos.smartdevice.own.Config;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.SnowFlake;
import com.lumos.smartdevice.utils.StringUtil;
import com.lumos.smartdevice.utils.tinytaskonebyone.BaseSyncTask;
import com.lumos.smartdevice.utils.tinytaskonebyone.TinySyncExecutor;

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


    public static boolean checkRunning(BookerSlotVo slot) {
        return brFlowDo.containsKey(slot.getSlotId());
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

        HashMap<String, Object> actionData = new HashMap<>();

        try {


            sendHandlerMessage(ACTION_FLOW_START, "借还开始");

            sendHandlerMessage(ACTION_INIT_DATA, "设备初始数据检查");

            HashMap<String, DriveVo> drives = device.getDrives();

            if (drives == null || drives.size() == 0) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "设备未配置驱动[01]");
                setRunning(false);
                return;
            }

            if (StringUtil.isEmpty(slot.getLockeqId())||StringUtil.isEmpty(slot.getLockeqAnt())) {
                actionData.put("lockeqId",slot.getLockeqId());
                actionData.put("lockeqAnt",slot.getLockeqAnt());
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE,actionData, "格子未配置驱动[02]");
                setRunning(false);
                return;
            }

            if (!drives.containsKey(slot.getLockeqId())) {
                actionData.put("drives",drives);
                actionData.put("lockeqId",slot.getLockeqId());
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE,actionData, "格子驱动找不到[03]");
                setRunning(false);
                return;
            }

            if (StringUtil.isEmpty(slot.getRfeqId())||StringUtil.isEmpty(slot.getRfeqAnt())) {
                actionData.put("rfeqId",slot.getRfeqId());
                actionData.put("rfeqAnt",slot.getRfeqAnt());
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE,actionData, "射频未配置驱动[04]");
                setRunning(false);
                return;
            }

            if (!drives.containsKey(slot.getRfeqId())) {
                actionData.put("drives", drives);
                actionData.put("rfeqId", slot.getRfeqId());
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "射频驱动找不到[05]");
                setRunning(false);
                return;
            }

            DriveVo lockeqDrive = drives.get(slot.getLockeqId());

            DriveVo rfeqDrive = drives.get(slot.getRfeqAnt());

            IRfeqCtrl rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(), rfeqDrive.getComBaud(), rfeqDrive.getComPrl());

            ILockeqCtrl  lockeqCtrl = LockeqCtrlInterface.getInstance(lockeqDrive.getComId(), lockeqDrive.getComBaud(), lockeqDrive.getComPrl());

            sendHandlerMessage(ACTION_INIT_DATA_SUCCESS, "初始化数据成功");

            int tryDo=0;//尝试的次数

            if (!lockeqCtrl.isConnect()) {
                actionData.put("drives", drives);
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData,"格子驱动未连接[10]");
                setRunning(false);
                return;
            }

            if (!rfeqCtrl.isConnect()) {
                actionData.put("drives", drives);
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频驱动未连接[11]");
                setRunning(false);
                return;
            }

            Thread.sleep(200);

            //Rf读取任务
            BaseSyncTask taskRfRead = new BaseSyncTask() {
                @Override
                public void doTask() {

                    try {
                        int tryDo = 0;
                        boolean isSendCloseRead = false;
                        while (tryDo < 3) {
                            if (rfeqCtrl.sendCloseRead()) {
                                isSendCloseRead = true;
                                break;
                            }
                            Thread.sleep(200);
                            tryDo++;
                        }

                        if (!isSendCloseRead) {
                            setResult(false);
                            return;
                        }

                        Thread.sleep(200);

                        //打开读取
                        boolean isSendOpenRead=false;
                        tryDo=0;
                        while (tryDo<3){
                            if (rfeqCtrl.sendOpenRead(slot.getRfeqAnt())) {
                                isSendOpenRead=true;
                                break;
                            }
                            Thread.sleep(200);
                            tryDo++;
                        }

                        if (!isSendOpenRead) {
                            setResult(false);
                            return;
                        }


                        Thread.sleep(500);

                        tryDo=0;
                        isSendCloseRead=false;
                        while (tryDo<3) {

                            if(rfeqCtrl.sendCloseRead()) {
                                isSendCloseRead = true;
                                break;
                            }

                            Thread.sleep(200);

                            tryDo++;
                        }

                        if(!isSendCloseRead) {
                            setResult(false);
                            return;
                        }

                        setTagInfos(rfeqCtrl.getRfIds(slot.getRfeqAnt()));

                        setResult(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        setResult(false);
                    }
                }
            };


            TinySyncExecutor.getInstance().enqueue(taskRfRead);

            long nTaskRfReadMaxTime = 60 * 1000;
            long nTaskRfReadStartTime = System.currentTimeMillis();
            long nTaskRfReadLastTime = System.currentTimeMillis() - nTaskRfReadStartTime;

            while (nTaskRfReadLastTime < nTaskRfReadMaxTime) {

                if (taskRfRead.isComplete()) {
                    break;
                }

                Thread.sleep(300);

                nTaskRfReadLastTime = System.currentTimeMillis() - nTaskRfReadStartTime;

            }

            if(!taskRfRead.isComplete()){
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频读取未完成[11]");
                return;
            }

            if(!taskRfRead.isSuccess()){
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频读取不成功[12]");
                return;
            }

            Map<String, TagInfo> tag_RfIds=taskRfRead.getTagInfos();


            List<String> open_RfIds=getRfIds(tag_RfIds);

//            open_RfIds.add("123456789012345678901410");
//            open_RfIds.add("123456789012345678901409");
//            open_RfIds.add("123456789012345678901408");
//            open_RfIds.add("123456789012345678901407");
//            open_RfIds.add("123456789012345678901403");
//            open_RfIds.add("123456789012345678901402");
//            open_RfIds.add("123456789012345678901401");


            actionData.put("openRfIds", open_RfIds);

            ResultBean<RetBookerBorrowReturn> result_Request_Open_Auth = borrowReturn(ACTION_REQUEST_OPEN_AUTH, actionData, "请求是否允许打开设备");

            if (result_Request_Open_Auth.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备[14]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");

            boolean isSendOpenSlot = lockeqCtrl.sendOpenSlot(slot.getLockeqAnt());

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

            while (nCheckMaxStatusTime < nCheckMinute * 60 * 1000) {

                //LogUtil.i(TAG,"检查开门状态");

                int slotStatus = lockeqCtrl.getSlotStatus(slot.getLockeqAnt());

                if (slotStatus == 1) {
                    isOpen = true;
                    break;
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
            nCheckMinute = 1;
            nCheckStartTime = System.currentTimeMillis();
            nCheckMaxStatusTime = System.currentTimeMillis() - nCheckStartTime;
            while (nCheckMaxStatusTime < nCheckMinute * 10 * 1000) {

                int slotStatus = lockeqCtrl.getSlotStatus(slot.getLockeqAnt());

                if (slotStatus == 0) {
                    isClose = true;
                    break;
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

            TinySyncExecutor.getInstance().enqueue(taskRfRead);

            nTaskRfReadStartTime = System.currentTimeMillis();
            nTaskRfReadLastTime = System.currentTimeMillis() - nTaskRfReadStartTime;

            while (nTaskRfReadLastTime < nTaskRfReadMaxTime) {

                if (taskRfRead.isComplete()) {
                    break;
                }

                Thread.sleep(300);

                nTaskRfReadLastTime = System.currentTimeMillis() - nTaskRfReadStartTime;

            }

            if(!taskRfRead.isComplete()){
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频读取未完成[11]");
            }

            if(!taskRfRead.isSuccess()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, "射频读取不成功[12]");
            }


            tag_RfIds=taskRfRead.getTagInfos();

            List<String> close_RfIds=getRfIds(tag_RfIds);

//            close_RfIds.add("123456789012345678901403");
//            close_RfIds.add("123456789012345678901402");
//            close_RfIds.add("123456789012345678901401");


            actionData.put("closeRfIds", close_RfIds);

            ResultBean<RetBookerBorrowReturn> result_Request_Close_Auth = borrowReturn(ACTION_REQUEST_CLOSE_AUTH, actionData, "请求是否允许关闭设备");

            if (result_Request_Close_Auth.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过[17]");
                setRunning(false);
                return;
            }

            RetBookerBorrowReturn ret_Request_Close_Auth = result_Request_Close_Auth.getData();

            actionData.put("flowId", ret_Request_Close_Auth.getFlowId());
            actionData.put("borrowBooks",ret_Request_Close_Auth.getBorrowBooks());
            actionData.put("returnBooks",ret_Request_Close_Auth.getReturnBooks());

            sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_SUCCESS, actionData, "请求关闭验证通过");

            sendHandlerMessage(ACTION_FLOW_END, actionData, "借阅流程结束");

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
                rop.setMsgId(String.valueOf(SnowFlake.nextId()));
                rop.setMsgMode("normal");
                rop.setDeviceId(deviceId);
                rop.setActionCode(actionCode);
                rop.setActionTime(CommonUtil.getCurrentTime());
                rop.setActionRemark(actionRemark);
                rop.setFlowId(flowId);
                if (actionData != null) {
                    rop.setActionData(JSON.toJSONString(actionData));
                }

                String msg_content=JSON.toJSONString(rop);

                DbManager.getInstance().saveTripMsg(rop.getMsgId(), Config.URL.booker_BorrowReturn, msg_content);
                ResultBean<RetBookerBorrowReturn>  result= ReqInterface.getInstance().bookerBorrowReturn(rop);
                if(result.getCode()==ResultCode.SUCCESS) {
                    DbManager.getInstance().deleteTripMsg(rop.getMsgId());
                }
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
