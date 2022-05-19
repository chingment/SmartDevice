package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ReqUrl;
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
import com.lumos.smartdevice.app.AppLogcatManager;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
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
    public static final String ACTION_OPEN_BEFORE_RFREADER_FAILURE="open_before_rfreader_failure";
    public static final String ACTION_WAIT_OPEN="wait_open";//等待打开
    public static final String ACTION_OPEN_SUCCESS= "open_success";//打开成功
    public static final String ACTION_OPEN_FAILURE = "open_failure";//打开失败
    public static final String ACTION_WAIT_CLOSE="wait_close";//等待关闭
    public static final String ACTION_CLOSE_SUCCESS = "close_success";//关闭成功
    public static final String ACTION_CLOSE_FAILURE = "close_failure";//关闭失败 ？如何处理？重试？
    public static final String ACTION_CLOSE_AFTER_RFREADER_FAILURE="close_after_rfreader_failure";
    public static final String ACTION_REQUEST_CLOSE_AUTH="request_close_auth";//请求关闭验证
    public static final String ACTION_REQUEST_CLOSE_AUTH_SUCCESS="request_close_auth_success";//关闭验证通过
    public static final String ACTION_REQUEST_CLOSE_AUTH_FAILURE="request_close_auth_failure";//关闭验证不通   返回  8
    public static final String ACTION_FLOW_END = "flow_end";
    public static final String ACTION_EXCEPTION = "exception";

    private String flowId;
    private final String flowUserId;
    private final int identityType;
    private final String identityId;
    private final DeviceVo device;
    private final BookerSlotVo slot;

    private static final Map<String, Object> brFlowDo = new ConcurrentHashMap<>();

    private final OnHandlerListener onHandlerListener;

    public BorrowReturnFlowThread(String flowUserId, int identityType, String identityId, DeviceVo device, BookerSlotVo slot, OnHandlerListener onHandlerListener) {
        this.flowUserId = flowUserId;
        this.identityType = identityType;
        this.identityId = identityId;
        this.device = device;
        this.slot = slot;
        this.onHandlerListener = onHandlerListener;
        this.setName("BorrowReturnFlow-Slot-" + slot.getSlotId());
    }

    private void setRunning(boolean isRunning) {

        String slotId=slot.getSlotId();

        if(isRunning) {
            brFlowDo.put(slotId, true);
        }
        else{
            brFlowDo.remove(slotId);
        }
    }

    public static boolean checkRunning(BookerSlotVo slot) {
        return brFlowDo.containsKey(slot.getSlotId());
    }

    @Override
    public void run() {
        super.run();

        String slotId=slot.getSlotId();

        synchronized (BorrowReturnFlowThread.class) {
            if (brFlowDo.containsKey(slotId)) {
                LogUtil.d(TAG, slotId + ":借阅任务正在执行");
                sendHandlerMessage(ACTION_TIPS, "正在执行中");
                return;
            } else {
                LogUtil.d(TAG, slotId + ":借阅任务开始执行");
                setRunning(true);
            }
        }

        doTask();
    }

    private List<String> getRfIds( Map<String, TagInfo> map_TagInfos) {

        List<String> rfIds = new ArrayList<>();

        if (map_TagInfos != null) {
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
        rop.setFlowType(1);
        rop.setFlowUserId(flowUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);

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

            actionData.put("drives", drives);
            actionData.put("slot", slot);

            if (drives == null || drives.size() == 0) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "设备未配置驱动[01]");
                setRunning(false);
                return;
            }

            DriveVo lockeqDrive = drives.get(slot.getLockeqId());

            if (lockeqDrive == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "格子驱动找不到[02]");
                setRunning(false);
                return;
            }

            DriveVo rfeqDrive = drives.get(slot.getRfeqId());

            if (rfeqDrive == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "射频驱动找不到[03]");
                setRunning(false);
                return;
            }


            IRfeqCtrl rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(), rfeqDrive.getComBaud(), rfeqDrive.getComPrl());

            ILockeqCtrl lockeqCtrl = LockeqCtrlInterface.getInstance(lockeqDrive.getComId(), lockeqDrive.getComBaud(), lockeqDrive.getComPrl());

            if (!lockeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "格子驱动未连接[04]");
                setRunning(false);
                return;
            }

            if (!rfeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "射频驱动未连接[05]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_INIT_DATA_SUCCESS, "初始化数据成功");

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
                            LogUtil.d(TAG,"发送关闭RFID读取失败1");
                            return;
                        }

                        LogUtil.d(TAG,"发送打开RFID读取成功1");

                        Thread.sleep(200);

                        //打开读取
                        boolean isSendOpenRead = false;
                        tryDo = 0;
                        while (tryDo < 3) {
                            if (rfeqCtrl.sendOpenRead(slot.getRfeqAnt())) {
                                isSendOpenRead = true;
                                break;
                            }
                            Thread.sleep(200);
                            tryDo++;
                        }

                        if (!isSendOpenRead) {
                            setResult(false);
                            LogUtil.d(TAG,"发送打开RFID读取失败");
                            return;
                        }

                        LogUtil.d(TAG,"发送打开RFID读取成功");

                        Thread.sleep(10*1000);

                        tryDo = 0;
                        isSendCloseRead = false;
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
                            LogUtil.d(TAG,"发送关闭RFID读取失败2");
                            return;
                        }

                        LogUtil.d(TAG,"发送关闭RFID读取成功");

                        setTagInfos(rfeqCtrl.getRfIds(slot.getRfeqAnt()));

                        setResult(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        setResult(false);
                    }
                }
            };


            TinySyncExecutor.getInstance().enqueue(taskRfRead);

            long nDoMaxTime = 15 * 1000;
            long nDoStartTime = System.currentTimeMillis();
            long nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            while (nDoLastTime < nDoMaxTime) {

                if (taskRfRead.isComplete()) {
                    break;
                }

                Thread.sleep(300);

                nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            }

            if (!taskRfRead.isComplete() || !taskRfRead.isSuccess()) {
                sendHandlerMessage(ACTION_OPEN_BEFORE_RFREADER_FAILURE, actionData, "打开设备前，射频读取未完成[06]");
                setRunning(false);
                return;
            }


            Map<String, TagInfo> tag_RfIds = taskRfRead.getTagInfos();

            List<String> open_RfIds = getRfIds(tag_RfIds);

//            open_RfIds.add("123456789012345678901410");
//            open_RfIds.add("123456789012345678901409");
//            open_RfIds.add("123456789012345678901408");
//            open_RfIds.add("123456789012345678901407");
//            open_RfIds.add("123456789012345678901403");
//            open_RfIds.add("123456789012345678901402");
//            open_RfIds.add("123456789012345678901401");


            actionData.put("openRfIds", open_RfIds);

            ResultBean<RetBookerBorrowReturn> result_BorrowReturn = borrowReturn(ACTION_REQUEST_OPEN_AUTH, actionData, "请求是否允许打开设备");

            if (result_BorrowReturn.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备[07]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");

            boolean isSendOpenSlot = false;
            int tryDo = 0;
            while (tryDo < 3) {
                isSendOpenSlot = lockeqCtrl.sendOpenSlot(slot.getLockeqAnt());
                if (isSendOpenSlot) {
                    break;
                }
                Thread.sleep(300);
                tryDo++;
            }

            if (!isSendOpenSlot) {
                sendHandlerMessage(ACTION_SEND_OPEN_COMMAND_FAILURE, "打开命令发送失败[08]");
                setRunning(false);
                return;
            }


            sendHandlerMessage(ACTION_SEND_OPEN_COMMAND_SUCCESS, "打开命令发送成功");

            sendHandlerMessage(ACTION_WAIT_OPEN, "等待打开");

            boolean isOpen = false;

            nDoMaxTime = 10 * 1000;
            nDoStartTime = System.currentTimeMillis();
            nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            while (nDoLastTime < nDoMaxTime) {

                int slotStatus = lockeqCtrl.getSlotStatus(slot.getLockeqAnt());

                if (slotStatus == 1) {
                    isOpen = true;
                    break;
                } else {
                    Thread.sleep(300);
                }

                nDoLastTime = System.currentTimeMillis() - nDoStartTime;
            }

            if (!isOpen) {
                sendHandlerMessage(ACTION_OPEN_FAILURE, "打开失败[16]");
                setRunning(false);
                return;
            }


            sendHandlerMessage(ACTION_OPEN_SUCCESS, "打开成功");


            //todo 发送命令成功后，后台要作超时判断和异常处理
            //情况1 开门后，检查客户最大的关门的时间，超过该时间段
            //情况2 开门途中，突然断电
            //情况3 处理关门后的数据异常


            sendHandlerMessage(ACTION_WAIT_CLOSE, "等待关闭");

            boolean isClose = false;

            nDoMaxTime = 60 * 60 * 1000;
            nDoStartTime = System.currentTimeMillis();
            nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            while (nDoLastTime < nDoMaxTime) {

                int slotStatus = lockeqCtrl.getSlotStatus(slot.getLockeqAnt());

                if (slotStatus == 0) {
                    isClose = true;
                    break;
                } else {
                    Thread.sleep(300);
                }

                nDoLastTime = System.currentTimeMillis() - nDoStartTime;
            }


            if (!isClose) {
                sendHandlerMessage(ACTION_CLOSE_FAILURE, "关闭失败");
            }

            sendHandlerMessage(ACTION_CLOSE_SUCCESS, "关闭成功");

            TinySyncExecutor.getInstance().enqueue(taskRfRead);

            nDoMaxTime = 15 * 1000;
            nDoStartTime = System.currentTimeMillis();
            nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            while (nDoLastTime < nDoMaxTime) {

                if (taskRfRead.isComplete()) {
                    break;
                }

                Thread.sleep(300);

                nDoLastTime = System.currentTimeMillis() - nDoStartTime;

            }

            if (!taskRfRead.isComplete() || !taskRfRead.isSuccess()) {
                sendHandlerMessage(ACTION_CLOSE_AFTER_RFREADER_FAILURE, "关闭设备后，射频读取不成功[12]");
            }

            tag_RfIds = taskRfRead.getTagInfos();

            List<String> close_RfIds = getRfIds(tag_RfIds);

//            close_RfIds.add("123456789012345678901403");
//            close_RfIds.add("123456789012345678901402");
//            close_RfIds.add("123456789012345678901401");


            actionData.put("closeRfIds", close_RfIds);

            result_BorrowReturn = borrowReturn(ACTION_REQUEST_CLOSE_AUTH, actionData, "请求是否允许关闭设备");

            if (result_BorrowReturn.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过[17]");
                setRunning(false);
                return;
            }

            RetBookerBorrowReturn ret_BorrowReturn = result_BorrowReturn.getData();

            actionData.put("flowId", ret_BorrowReturn.getFlowId());
            actionData.put("borrowBooks", ret_BorrowReturn.getBorrowBooks());
            actionData.put("returnBooks", ret_BorrowReturn.getReturnBooks());

            sendHandlerMessage(ACTION_REQUEST_CLOSE_AUTH_SUCCESS, actionData, "请求关闭验证通过");

            sendHandlerMessage(ACTION_FLOW_END, actionData, "借还结束");

            setRunning(false);

        } catch (Exception ex) {
            sendHandlerMessage(ACTION_EXCEPTION, actionData, "发生异常");
            setRunning(false);
        }
    }

    private ResultBean<RetBookerBorrowReturn> borrowReturn(String actionCode,HashMap<String,Object> actionData,String actionRemark) {
        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setFlowId(flowId);
        rop.setMsgId(String.valueOf(SnowFlake.nextId()));
        rop.setMsgMode("normal");
        rop.setDeviceId(device.getDeviceId());
        rop.setActionCode(actionCode);
        rop.setActionSn(getActionSn(actionCode));
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setActionData(JsonUtil.toJsonStr(actionData));

        String msg_content = JSON.toJSONString(rop);

        DbManager.getInstance().saveTripMsg(rop.getMsgId(), ReqUrl.booker_BorrowReturn, msg_content);
        ResultBean<RetBookerBorrowReturn> result = ReqInterface.getInstance().bookerBorrowReturn(rop);
        if (result.getCode() == ResultCode.SUCCESS) {
            DbManager.getInstance().deleteTripMsg(rop.getMsgId());
        }

        return result;
    }

    private void sendHandlerMessage(String actionCode,HashMap<String,Object> actionData, String actionRemark) {

        LogUtil.d(TAG,"actionCode:"+actionCode+",actionRemark:"+actionRemark);

        new Thread(() -> {

            borrowReturn(actionCode,actionData,actionRemark);

            if(actionCode.contains("failure")||actionCode.contains("exception")){
                AppLogcatManager.uploadLogcat2Server("logcat -d -s BorrowReturnFlowThread RfeqCtrlByDs LockeqCtrlByDs ","test");
            }

        }).start();

        if (onHandlerListener != null) {
            MessageByAction message = new MessageByAction();
            message.setDeviceId(device.getDeviceId());
            message.setFlowId(flowId);
            message.setFlowType(1);
            message.setActionCode(actionCode);
            message.setActionData(actionData);
            message.setActionRemark(actionRemark);
            onHandlerListener.handleMessage(message);
        }
    }

    private void sendHandlerMessage(String actionCode,String actionRemark){
        sendHandlerMessage(actionCode, null, actionRemark);
    }

    public interface OnHandlerListener {
        void handleMessage(MessageByAction message);
    }

    public int getActionSn(String  actionCode){

        switch (actionCode)
        {
            case ACTION_FLOW_START:
                return 1001;
            case ACTION_INIT_DATA:
                return 1002;
            case ACTION_INIT_DATA_SUCCESS:
                return 1003;
            case ACTION_INIT_DATA_FAILURE:
                return 1004;
            case ACTION_REQUEST_OPEN_AUTH:
                return 1005;
            case ACTION_REQUEST_OPEN_AUTH_SUCCESS:
                return 1006;
            case ACTION_REQUEST_OPEN_AUTH_FAILURE:
                return 1007;
            case ACTION_SEND_OPEN_COMMAND:
                return 1008;
            case ACTION_SEND_OPEN_COMMAND_FAILURE:
                return 1009;
            case ACTION_SEND_OPEN_COMMAND_SUCCESS:
                return 1010;
            case ACTION_OPEN_BEFORE_RFREADER_FAILURE:
                return 1011;
            case ACTION_WAIT_OPEN:
                return 1012;
            case ACTION_OPEN_SUCCESS:
                return 1013;
            case ACTION_OPEN_FAILURE:
                return 1014;
            case ACTION_WAIT_CLOSE:
                return 1015;
            case ACTION_CLOSE_SUCCESS:
                return 1016;
            case ACTION_CLOSE_FAILURE:
                return 1017;
            case ACTION_CLOSE_AFTER_RFREADER_FAILURE:
                return 1018;
            case ACTION_REQUEST_CLOSE_AUTH:
                return 1019;
            case ACTION_REQUEST_CLOSE_AUTH_SUCCESS:
                return 1020;
            case ACTION_REQUEST_CLOSE_AUTH_FAILURE:
                return 1021;
            case ACTION_FLOW_END:
                return 1080;
            case ACTION_EXCEPTION:
                return 1099;

        }

        return 0;
    }

}
