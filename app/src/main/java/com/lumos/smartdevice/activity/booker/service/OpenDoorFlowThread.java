package com.lumos.smartdevice.activity.booker.service;

import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.app.AppLogcatManager;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpenDoorFlowThread extends Thread {
    private static final String TAG = "OpenDoorFlowThread";

    public static final String ACTION_TIPS = "tips";//开始
    public static final String ACTION_FLOW_START = "flow_start";//开始
    public static final String ACTION_INIT_DATA = "init_data";//初始数据
    public static final String ACTION_INIT_DATA_SUCCESS = "init_data_success";//初始数据成功
    public static final String ACTION_INIT_DATA_FAILURE = "init_data_failure";//初始数据失败 返回 1
    public static final String ACTION_SEND_OPEN_COMMAND = "send_open_command";//发送打开命令
    public static final String ACTION_SEND_OPEN_COMMAND_SUCCESS = "send_open_command_success";//发送打开命令成功
    public static final String ACTION_SEND_OPEN_COMMAND_FAILURE = "send_open_command_failure";//发送打开命令失败 返回 1
    public static final String ACTION_WAIT_OPEN="wait_open";//等待打开
    public static final String ACTION_OPEN_SUCCESS= "open_success";//打开成功
    public static final String ACTION_OPEN_FAILURE = "open_failure";//打开失败
    public static final String ACTION_FLOW_END = "flow_end";
    public static final String ACTION_EXCEPTION = "exception";

    private String flowId;
    private final String flowUserId;
    private int flowType;
    private final int identityType;
    private final String identityId;
    private final DeviceVo device;
    private final BookerSlotVo slot;

    private final OnHandlerListener onHandlerListener;

    private static final Map<String, Object> brFlowDo = new ConcurrentHashMap<>();

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

    public OpenDoorFlowThread(int flowType, String flowUserId, int identityType, String identityId, DeviceVo device, BookerSlotVo slot, OnHandlerListener onHandlerListener) {
        this.flowType = flowType;
        this.flowUserId = flowUserId;
        this.identityType = identityType;
        this.identityId = identityId;
        this.device = device;
        this.slot=slot;
        this.onHandlerListener = onHandlerListener;
        this.setName("OpenDoorFlowThread-Slot-" + slot.getSlotId());
    }

    public interface OnHandlerListener {
        void handleMessage(MessageByAction message);
    }

    @Override
    public synchronized void run() {
        super.run();

        String slotId=slot.getSlotId();

        synchronized (BorrowReturnFlowThread.class) {
            if (brFlowDo.containsKey(slotId)) {
                LogUtil.d(TAG, slotId + ":打开柜门任务正在执行");
                sendHandlerMessage(ACTION_TIPS, "正在执行中");
                return;
            } else {
                LogUtil.d(TAG, slotId + ":打开柜门任务开始执行");
                setRunning(true);
            }
        }

        doTask();
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

            sendHandlerMessage(ACTION_FLOW_START, "打开柜门开始");

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


            ILockeqCtrl lockeqCtrl = LockeqCtrlInterface.getInstance(lockeqDrive.getComId(), lockeqDrive.getComBaud(), lockeqDrive.getComPrl());

            if (!lockeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "格子驱动未连接[04]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_INIT_DATA_SUCCESS, "初始化数据成功");

            Thread.sleep(200);

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

            long  nDoMaxTime = 10 * 1000;
            long  nDoStartTime = System.currentTimeMillis();
            long  nDoLastTime = System.currentTimeMillis() - nDoStartTime;

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

            sendHandlerMessage(ACTION_FLOW_END, actionData, "打开柜门结束");

            setRunning(false);

        } catch (Exception ex) {
            sendHandlerMessage(ACTION_EXCEPTION, actionData, "发生异常");
            setRunning(false);
        }
    }


    private void sendHandlerMessage(String actionCode, HashMap<String,Object> actionData, String actionRemark) {

        LogUtil.d(TAG,"actionCode:"+actionCode+",actionRemark:"+actionRemark);

        new Thread(() -> {

            //takeStock(actionCode,actionData,actionRemark);

            if(actionCode.contains("failure")||actionCode.contains("exception")){
                AppLogcatManager.uploadLogcat2Server("logcat -d -s OpenDoorFlowThread RfeqCtrlByDs LockeqCtrlByDs ","test");
            }

        }).start();

        if (onHandlerListener != null) {
            MessageByAction message = new MessageByAction();
            message.setDeviceId(device.getDeviceId());
            message.setFlowId(flowId);
            message.setFlowType(flowType);
            message.setActionCode(actionCode);
            message.setActionData(actionData);
            message.setActionRemark(actionRemark);
            onHandlerListener.handleMessage(message);
        }

    }

    private void sendHandlerMessage(String actionCode,String actionRemark){
        sendHandlerMessage(actionCode, null, actionRemark);
    }
}
