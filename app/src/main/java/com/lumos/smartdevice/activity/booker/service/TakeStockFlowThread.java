package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ReqUrl;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerTakeStock;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.app.AppLogcatManager;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.TagInfo;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.SnowFlake;
import com.lumos.smartdevice.utils.tinytaskonebyone.BaseSyncTask;
import com.lumos.smartdevice.utils.tinytaskonebyone.TinySyncExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TakeStockFlowThread extends Thread {

    private static final String TAG = "TakeStockFlowTread";

    public static final String ACTION_TIPS = "tips";//开始
    public static final String ACTION_FLOW_START = "flow_start";//开始
    public static final String ACTION_INIT_DATA = "init_data";//初始数据
    public static final String ACTION_INIT_DATA_SUCCESS = "init_data_success";//初始数据成功
    public static final String ACTION_INIT_DATA_FAILURE = "init_data_failure";//初始数据失败
    public static final String ACTION_CHECK_DOOR_STATUS="check_door_status";//检查柜门状态
    public static final String ACTION_CHECK_DOOR_STATUS_SUCCESS="check_door_status_success";//检查柜门状态成功
    public static final String ACTION_CHECK_DOOR_STATUS_FAILURE="check_door_status_failure";//检查柜门状态失败
    public static final String ACTION_DOOR_OPEN= "door_open";//柜门打开
    public static final String ACTION_DOOR_CLOSE = "door_close";//柜门关闭
    public static final String ACTION_WAIT_RFREADER="wait_rfreader";//等待RFID读取
    public static final String ACTION_RFREADER_SUCCESS = "rfreader_success";//RF读取成功
    public static final String ACTION_RFREADER_FAILURE = "rfreader_failure";//RF读取失败
    public static final String ACTION_TAKESTOCK_SUCCESS = "take_stock_success";//盘点成功
    public static final String ACTION_TAKESTOCK_FAILURE = "take_stock_failure";//盘点失败
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

    public TakeStockFlowThread(int flowType,String flowUserId, int identityType, String identityId, DeviceVo device, BookerSlotVo slot, OnHandlerListener onHandlerListener) {
        this.flowType = flowType;
        this.flowUserId = flowUserId;
        this.identityType = identityType;
        this.identityId = identityId;
        this.device = device;
        this.slot=slot;
        this.onHandlerListener = onHandlerListener;

        this.setName("TakeStockFlowTread-Slot-" + slot.getSlotId());
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
    public synchronized void run() {
        super.run();

        String slotId=slot.getSlotId();

        synchronized (BorrowReturnFlowThread.class) {
            if (brFlowDo.containsKey(slotId)) {
                LogUtil.d(TAG, slotId + ":盘点任务正在执行");
                sendHandlerMessage(ACTION_TIPS, "正在执行中");
                return;
            } else {
                LogUtil.d(TAG, slotId + ":盘点任务开始执行");
                setRunning(true);
            }
        }

        doTask();
    }

    private void  doTask() {

        RopBookerCreateFlow rop = new RopBookerCreateFlow();
        rop.setDeviceId(device.getDeviceId());
        rop.setSlotId(slot.getSlotId());
        rop.setFlowType(flowType);
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

            sendHandlerMessage(ACTION_FLOW_START, "盘点开始");

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

            sendHandlerMessage(ACTION_CHECK_DOOR_STATUS, "检查柜门打开状态");

            Thread.sleep(200);

            long nDoMaxTime = 10 * 1000;
            long nDoStartTime = System.currentTimeMillis();
            long nDoLastTime = System.currentTimeMillis() - nDoStartTime;
            int slotStatus=-1;
            while (nDoLastTime < nDoMaxTime) {

                slotStatus = lockeqCtrl.getSlotStatus(slot.getLockeqAnt());

                if (slotStatus == 0) {
                    break;
                } else {
                    Thread.sleep(300);
                }

                nDoLastTime = System.currentTimeMillis() - nDoStartTime;
            }

            if(slotStatus!=0&&slotStatus!=1) {
                LogUtil.d(TAG, "slotStatus:" + slotStatus);
                sendHandlerMessage(ACTION_CHECK_DOOR_STATUS_FAILURE, "检查柜门状态失败[06]");
            }

            sendHandlerMessage(ACTION_CHECK_DOOR_STATUS_SUCCESS, "检查柜门状态成功");

            if (slotStatus==1) {
                sendHandlerMessage(ACTION_DOOR_OPEN, "柜门已开[07]");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_DOOR_CLOSE, "柜门关闭");

            sendHandlerMessage(ACTION_WAIT_RFREADER, "等待检查数量");

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
                            LogUtil.d(TAG,"发送关闭RFID读取失败1");
                            setResult(false);
                            return;
                        }

                        LogUtil.d(TAG,"发送关闭RFID读取成功1");

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

                        Thread.sleep(1000);

                        LogUtil.d(TAG,"发送关闭RFID读取成功2");

                        setTagInfos(rfeqCtrl.getRfIds(slot.getRfeqAnt()));

                        setResult(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        setResult(false);
                    }
                }
            };

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
                sendHandlerMessage(ACTION_RFREADER_FAILURE, actionData, "射频读取未完成[08]");
                setRunning(false);
                return;
            }


            Map<String, TagInfo> tag_RfIds = taskRfRead.getTagInfos();

            List<String> closeRfIds = getRfIds(tag_RfIds);

            actionData.put("closeRfIds",  closeRfIds);

            ResultBean<RetBookerTakeStock> result_TakeStock = takeStock(ACTION_RFREADER_SUCCESS, actionData, "读取成功");

            if (result_TakeStock.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_TAKESTOCK_FAILURE, "盘点失败[09]");
                setRunning(false);
                return;
            }

            RetBookerTakeStock ret_TakeStock = result_TakeStock.getData();

            actionData.put("flowId", ret_TakeStock.getFlowId());
            actionData.put("sheetId", ret_TakeStock.getSheetId());
            actionData.put("sheetItems",ret_TakeStock.getSheetItems());
            actionData.put("warnItems",ret_TakeStock.getWarnItems());

            sendHandlerMessage(ACTION_TAKESTOCK_SUCCESS,actionData, "盘点成功");
            sendHandlerMessage(ACTION_FLOW_END, actionData, "盘点结束");

            setRunning(false);

        } catch (Exception ex) {
            sendHandlerMessage(ACTION_EXCEPTION, actionData, "发生异常");
            setRunning(false);
        }
    }

    private ResultBean<RetBookerTakeStock> takeStock(String actionCode, HashMap<String,Object> actionData, String actionRemark) {

        RopBookerTakeStock rop = new RopBookerTakeStock();
        rop.setFlowId(flowId);
        rop.setMsgId(String.valueOf(SnowFlake.nextId()));
        rop.setMsgMode("normal");
        rop.setDeviceId(device.getDeviceId());
        rop.setSlotId(slot.getSlotId());
        rop.setActionSn(getActionSn(actionCode));
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setActionData(JsonUtil.toJsonStr(actionData));

        String msg_content=JSON.toJSONString(rop);

        DbManager.getInstance().saveTripMsg(rop.getMsgId(), ReqUrl.booker_TakeStock, msg_content);
        ResultBean<RetBookerTakeStock>  result=ReqInterface.getInstance().bookerTakeStock(rop);
        if(result.getCode()==ResultCode.SUCCESS) {
            DbManager.getInstance().deleteTripMsg(rop.getMsgId());
        }

        return result;

    }

    private void sendHandlerMessage(String actionCode, HashMap<String,Object> actionData, String actionRemark) {

        LogUtil.d(TAG,"actionCode:"+actionCode+",actionRemark:"+actionRemark);

        new Thread(() -> {

            takeStock(actionCode,actionData,actionRemark);

            if(actionCode.contains("failure")||actionCode.contains("exception")){
                AppLogcatManager.uploadLogcat2Server("logcat -d -s TakeStockFlowTread RfeqCtrlByDs LockeqCtrlByDs ","test");
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

    public interface OnHandlerListener {
        void handleMessage(MessageByAction message);
    }

    public int getActionSn(String  actionCode) {

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
            case ACTION_CHECK_DOOR_STATUS:
                return 1005;
            case ACTION_CHECK_DOOR_STATUS_SUCCESS:
                return 1006;
            case ACTION_CHECK_DOOR_STATUS_FAILURE:
                return 1007;
            case ACTION_DOOR_OPEN:
                return 1008;
            case ACTION_DOOR_CLOSE:
                return 1009;
            case ACTION_WAIT_RFREADER:
                return 1010;
            case ACTION_RFREADER_SUCCESS:
                return 1011;
            case ACTION_RFREADER_FAILURE:
                return 1012;
            case ACTION_TAKESTOCK_SUCCESS:
                return 1013;
            case ACTION_TAKESTOCK_FAILURE:
                return 1014;
            case ACTION_FLOW_END:
                return 1080;
            case ACTION_EXCEPTION:
                return 1099;

        }

        return 0;
    }

    private List<String> getRfIds(Map<String, TagInfo> map_TagInfos) {

        List<String> rfIds = new ArrayList<>();

        if (map_TagInfos != null) {
            Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
            for (Map.Entry<String, TagInfo> entry : entrys) {
                rfIds.add(entry.getKey());
            }
        }

        return rfIds;

    }
}
