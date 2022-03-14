package com.lumos.smartdevice.devicectrl;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.model.CabinetBean;
import com.lumos.smartdevice.model.CabinetLayoutBean;
import com.lumos.smartdevice.model.CabinetSlotBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.RfIdBean;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BookerBorrowReturnFlowCtrl {

    private static final String TAG = "BookerBorrowReturnFlowCtrl";

    private static BookerBorrowReturnFlowCtrl mBookerBorrowReturnFlowCtrl= null;

    public static final int MESSAGE_WHAT_FLOW_START = 1;
    public static final int MESSAGE_WHAT_INIT_DATA = 2;
    public static final int MESSAGE_WHAT_INIT_DATA_SUCCESS = 3;
    public static final int MESSAGE_WHAT_INIT_DATA_FAILURE = 4;
    public static final int MESSAGE_WHAT_OPEN_REQUEST = 5;
    public static final int MESSAGE_WHAT_OPEN_REQUEST_SUCCESS = 6;
    public static final int MESSAGE_WHAT_OPEN_REQUEST_FAILURE = 7;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND = 8;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND_SUCCESS = 9;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND_FAILURE = 10;
    public static final int MESSAGE_WHAT_OPEN_SUCCESS= 11;
    public static final int MESSAGE_WHAT_OPEN_FAILURE = 12;
    public static final int MESSAGE_WHAT_CLOSE_SUCCESS= 13;
    public static final int MESSAGE_WHAT_CLOSE_FAILURE = 14;
    public static final int MESSAGE_WHAT_FLOW_END = 15;

    private DeviceBean device;
    private CabinetSlotBean cabinetSlot;
    private String clientUserId;
    private int identityType;
    private String identityId;
    private String trgId;
    private String flowId;


    private Handler openHandler = null;
    private boolean openIsRunning=false;
    private ICabinetCtrl cabinetCtrl;
    private IRfIdCtrl rfIdCtrl;

    public static BookerBorrowReturnFlowCtrl getInstance() {

        mBookerBorrowReturnFlowCtrl = new BookerBorrowReturnFlowCtrl();

        return mBookerBorrowReturnFlowCtrl;
    }

    private void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    private void reSet() {
        device = null;
        cabinetSlot = null;
        clientUserId = null;
        identityType = 0;
        identityId = null;
        trgId = null;
        flowId = null;
    }

    public void open(DeviceBean device,CabinetSlotBean cabinetSlot,String clientUserId,int identityType,String  identityId) {
        new Thread(() -> {
            if (openIsRunning) {
                LogUtil.d(TAG, "有任务正在执行");
            } else {
                openIsRunning = true;
                this.device = device;
                this.cabinetSlot = cabinetSlot;
                this.clientUserId = clientUserId;
                this.identityType = identityType;
                this.identityId = identityId;
                this.trgId = UUID.randomUUID().toString().replace("-", "");

                try {

                    sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_START);

                    sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA);

                    if (device == null) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "设备未配置");
                        return;
                    }

                    if (device.getCabinets() == null) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "门柜未配置");
                        return;
                    }

                    if (cabinetSlot == null) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "格子未配置");
                        return;
                    }

                    CabinetBean cabinet = device.getCabinets().get(cabinetSlot.getCabinetId());

                    if (cabinet == null) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "门柜未配置");
                        return;
                    }

                    if (StringUtil.isEmpty(cabinet.getLayout())) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "门柜参数未配置");
                        return;
                    }

                    CabinetLayoutBean cabinetLayout = JSON.parseObject(cabinet.getLayout(), new TypeReference<CabinetLayoutBean>() {
                    });

                    if (cabinetLayout == null) {
                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "门柜参数格式有误");
                        return;
                    }

                    cabinetCtrl=CabinetCtrlInterface.getInstance(cabinet.getComId(),cabinet.getComBaud(),cabinet.getComPrl());

//                    if (cabinetLayout.getRfId() == null) {
//                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "RFID未配置");
//                        return;
//                    }
//
//                    RfIdBean rfId = cabinetLayout.getRfId();
//
//                    IRfIdCtrl rfIdCtrl = RfIdCtrlInterface.getInstance(rfId.getComId(), rfId.getComBaud(), rfId.getComPrl());
//
//                    if (rfIdCtrl == null) {
//                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "初始化RFID失败");
//                        return;
//                    }
//
//
//                    if (!rfIdCtrl.sendRead()) {
//                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "RFID发送读取命令失败");
//                        return;
//                    }


                    Thread.sleep(3000);


                    //rfIdCtrl=RfIdCtrlInterface.getInstance()


                    //cabinetCtrl = CabinetCtrlInterface.getInstance(cabinet.getComId(), cabinet.getComBaud(), cabinet.getComPrl());

                    HashMap<String,Object> actionData=new HashMap<>();


                    List<String> rfIds=new ArrayList<>();
                    rfIds.add("31");
                    rfIds.add("32");
                    rfIds.add("33");
                    rfIds.add("34");
                    rfIds.add("35");

                    actionData.put("rfIds",rfIds);

                    sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_REQUEST,actionData, "请求打开");


                } catch (InterruptedException e) {
                    sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                }

            }
        }).start();


    }

    public void bookerBorrowReturn(String actionCode, HashMap<String,Object> actionData,String actionRemark, ReqHandler reqHandler) {

        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setDeviceId(device == null ? null : device.getDeviceId());
        rop.setCabinetId(cabinetSlot == null ? null : cabinetSlot.getCabinetId());
        rop.setSlotId(cabinetSlot == null ? null : cabinetSlot.getSlotId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setFlowId(flowId);
        rop.setTrgId(trgId);
        if (actionData != null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }
        ReqInterface.getInstance().bookerBorrowReturn(rop, reqHandler);

    }

    public void setOpenHandler(Handler openHandler) {
        this.openHandler = openHandler;
    }

    private void sendOpenHandlerMessage(int what) {
        sendOpenHandlerMessage(what, null, null);
    }

    private void sendOpenHandlerMessage(int what,String actionRemark) {
        sendOpenHandlerMessage(what, null, actionRemark);
    }

    private void sendOpenHandlerMessage(int what,HashMap<String,Object> actionData,String actionRemark ) {

        switch (what) {
            case MESSAGE_WHAT_FLOW_START:
                bookerBorrowReturn("flow_start", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_INIT_DATA:
                bookerBorrowReturn("init_data", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_INIT_DATA_SUCCESS:
                bookerBorrowReturn("init_data_success", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_INIT_DATA_FAILURE:
                bookerBorrowReturn("init_data_failure", actionData, actionRemark, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                break;
            case MESSAGE_WHAT_OPEN_REQUEST:
                bookerBorrowReturn("open_request", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetBookerBorrowReturn d = rt.getData();
                            setFlowId(d.getFlowId());
                            sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_REQUEST_SUCCESS);
                        } else {
                            sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_REQUEST_FAILURE);
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_REQUEST_FAILURE);
                    }
                });
                break;
            case MESSAGE_WHAT_OPEN_REQUEST_SUCCESS:
                bookerBorrowReturn("open_request_success", actionData, actionRemark, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_SEND_OPEN_COMMAND);
                break;
            case MESSAGE_WHAT_OPEN_REQUEST_FAILURE:
                bookerBorrowReturn("open_request_failure", actionData, actionRemark, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND:
                bookerBorrowReturn("send_open_command", actionData, actionRemark, null);
                cabinetCtrl.open("1", new ICabinetCtrl.OnListener() {
                    @Override
                    public void onSendCommandSuccess() {
                        sendOpenHandlerMessage(MESSAGE_WHAT_SEND_OPEN_COMMAND_SUCCESS);
                    }

                    @Override
                    public void onSendCommnadFailure() {
                        sendOpenHandlerMessage(MESSAGE_WHAT_SEND_OPEN_COMMAND_FAILURE);
                    }

                    @Override
                    public void onOpenSuccess() {
                        sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_SUCCESS);

                        List<String> rfIds = new ArrayList<>();
                        rfIds.add("31");
                        rfIds.add("32");
                        rfIds.add("33");

                        HashMap<String, Object> actionData = new HashMap<>();
                        actionData.put("rfIds", rfIds);

                        sendOpenHandlerMessage(MESSAGE_WHAT_CLOSE_SUCCESS, actionData, "关闭成功");
                    }

                    @Override
                    public void onOpenFailure() {
                        sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_FAILURE);
                    }
                });
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND_SUCCESS:
                bookerBorrowReturn("send_open_command_success", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND_FAILURE:
                bookerBorrowReturn("send_open_command_failure", actionData, actionRemark, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                break;
            case MESSAGE_WHAT_OPEN_SUCCESS:
                bookerBorrowReturn("open_success", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_OPEN_FAILURE:
                bookerBorrowReturn("open_failure", actionData, actionRemark, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                break;
            case MESSAGE_WHAT_CLOSE_SUCCESS:
                bookerBorrowReturn("close_success", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetBookerBorrowReturn d = rt.getData();
                            setFlowId(d.getFlowId());
                            sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                        } else {
                            sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendOpenHandlerMessage(MESSAGE_WHAT_FLOW_END);
                    }
                });

                break;
            case MESSAGE_WHAT_CLOSE_FAILURE:
                bookerBorrowReturn("close_failure", actionData, actionRemark, null);
                break;
            case MESSAGE_WHAT_FLOW_END:
                openIsRunning = false;
                bookerBorrowReturn("flow_end", actionData, actionRemark, null);
                reSet();
                break;
        }

        if (openHandler != null) {
            Message m = new Message();
            m.what = what;
            openHandler.sendMessage(m);
        }
    }

}
