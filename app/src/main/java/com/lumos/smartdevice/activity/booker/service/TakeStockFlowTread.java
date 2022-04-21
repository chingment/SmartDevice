package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerTakeStock;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.TagInfo;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.tinytaskonebyone.BaseSyncTask;
import com.lumos.smartdevice.utils.tinytaskonebyone.TinySyncExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeStockFlowTread extends Thread {

    private static final String TAG = "TakeStockFlowTread";

    public static final String ACTION_TIPS = "tips";//开始
    public static final String ACTION_FLOW_START = "flow_start";//开始
    public static final String ACTION_INIT_DATA = "init_data";//初始数据
    public static final String ACTION_INIT_DATA_SUCCESS = "init_data_success";//初始数据成功
    public static final String ACTION_INIT_DATA_FAILURE = "init_data_failure";//初始数据失败 返回 1
    public static final String ACTION_RFREADER_SUCCESS = "rfreader_success";//RF读取成功
    public static final String ACTION_RFREADER_FAILURE = "rfreader_failure";//RF读取失败
    public static final String ACTION_TAKESTOCK_SUCCESS = "takestock_success";//RF读取失败
    public static final String ACTION_TAKESTOCK_FAILURE = "takestock_failure";//RF读取失败
    public static final String ACTION_FLOW_END = "flow_end";
    public static final String ACTION_EXCEPTION = "exception";

    private final DeviceVo device;
    private boolean isRunning=false;
    private String flowId;
    private int flowType;

    public TakeStockFlowTread(DeviceVo device,int flowType) {
        this.device = device;
        this.flowType = flowType;
        this.setName("TakeStockFlowTread");
    }


    private void setRunning(boolean isRunning) {
        this.isRunning=isRunning;
    }

    @Override
    public synchronized void run() {
        super.run();

        setRunning(true);

        HashMap<String, Object> actionData = new HashMap<>();

        RopBookerCreateFlow rop = new RopBookerCreateFlow();
        rop.setDeviceId(device.getDeviceId());
        rop.setType(flowType);

        ResultBean<RetBookerCreateFlow> result_CreateFlow = ReqInterface.getInstance().bookerCreateFlow(rop);

        if (result_CreateFlow.getCode() != ResultCode.SUCCESS) {
            sendHandlerMessage(ACTION_TIPS, result_CreateFlow.getMsg());
            setRunning(false);
            return;
        }

        RetBookerCreateFlow ret_CreateFlow = result_CreateFlow.getData();

        flowId = ret_CreateFlow.getFlowId();

        try {

            sendHandlerMessage(ACTION_FLOW_START, "盘点开始");

            sendHandlerMessage(ACTION_INIT_DATA, "设备初始数据检查");

            HashMap<String, DriveVo> drives = device.getDrives();

            actionData.put("drives", drives);

            if (drives == null || drives.size() == 0) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "设备未配置驱动[01]");
                setRunning(false);
                return;
            }

            DriveVo rfeqDrive = drives.get("ss");

            if (rfeqDrive == null) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "射频驱动找不到[02]");
                setRunning(false);
                return;
            }

            IRfeqCtrl rfeqCtrl = RfeqCtrlInterface.getInstance(rfeqDrive.getComId(), rfeqDrive.getComBaud(), rfeqDrive.getComPrl());

            if (!rfeqCtrl.isConnect()) {
                sendHandlerMessage(ACTION_INIT_DATA_FAILURE, actionData, "射频驱动未连接[03]");
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
                            return;
                        }

                        Thread.sleep(200);

                        //打开读取
                        boolean isSendOpenRead = false;
                        tryDo = 0;
                        while (tryDo < 3) {
                            if (rfeqCtrl.sendOpenRead("")) {
                                isSendOpenRead = true;
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
                            return;
                        }

                        setTagInfos(rfeqCtrl.getRfIds(""));

                        setResult(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        setResult(false);
                    }
                }
            };


            TinySyncExecutor.getInstance().enqueue(taskRfRead);

            long nDoMaxTime = 5 * 1000;
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
                sendHandlerMessage(ACTION_RFREADER_FAILURE, actionData, "射频读取未完成[05]");
                setRunning(false);
                return;
            }


            Map<String, TagInfo> tag_RfIds = taskRfRead.getTagInfos();

            actionData.put("rfIds", tag_RfIds);

            ResultBean<RetBookerTakeStock> result_BorrowReturn = takeStock(ACTION_TAKESTOCK_SUCCESS, actionData, "盘点成功");

            if (result_BorrowReturn.getCode() != ResultCode.SUCCESS) {
                sendHandlerMessage(ACTION_TAKESTOCK_FAILURE, "盘点失败");
                setRunning(false);
                return;
            }

            sendHandlerMessage(ACTION_FLOW_END, actionData, "盘点结束");

            setRunning(false);

            Thread.sleep(200);

        } catch (Exception ex) {
            sendHandlerMessage(ACTION_EXCEPTION, actionData, "发生异常");
            setRunning(false);
        }
    }

    private ResultBean<RetBookerTakeStock> takeStock(String actionCode, HashMap<String,Object> actionData, String actionRemark) {

        RopBookerTakeStock rop = new RopBookerTakeStock();
        rop.setDeviceId(device.getDeviceId());
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setFlowId(flowId);
        if (actionData != null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }

        return ReqInterface.getInstance().bookerTakeStock(rop);

    }

    private void sendHandlerMessage(String actionCode, HashMap<String,Object> actionData, String actionRemark) {



    }

    private void sendHandlerMessage(String actionCode,String actionRemark){
        sendHandlerMessage(actionCode, null, actionRemark);
    }

    public interface OnHandlerListener {
        void handleMessage(MessageByAction message);
    }
}
