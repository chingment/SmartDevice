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
import com.lumos.smartdevice.model.SlotBean;
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

    private static BookerBorrowReturnFlowCtrl mThis= null;

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

    private DeviceBean device;
    private CabinetBean cabinet;
    private SlotBean slot;
    private String flowId;


    private boolean openIsRunning=false;
    private ICabinetCtrl cabinetCtrl;
    private IRfIdCtrl rfIdCtrl;

    public static BookerBorrowReturnFlowCtrl getInstance() {

        if (mThis == null) {
            synchronized (BookerBorrowReturnFlowCtrl.class) {
                if (mThis == null) {
                    mThis = new BookerBorrowReturnFlowCtrl();
                }
            }
        }

        return mThis;
    }

    private void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    private void reSet() {
        device = null;
        slot = null;
        flowId = null;
        open_RfIds.clear();
        close_RfIds.clear();
    }

    private List<String> open_RfIds=new ArrayList<>();
    private List<String> close_RfIds=new ArrayList<>();

    public void open(DeviceBean device,CabinetBean cabinet, SlotBean slot, String flowId) {
        new Thread(() -> {
            if (openIsRunning) {
                LogUtil.d(TAG, "有任务正在执行");
            } else {
                openIsRunning = true;
                open_RfIds.clear();
                close_RfIds.clear();
                this.device = device;
                this.cabinet=cabinet;
                this.slot = slot;
                this.flowId=flowId;

                try {

                    sendOpenHandlerMessage(ACTION_CODE_FLOW_START,"借还开始");

                    sendOpenHandlerMessage(ACTION_CODE_INIT_DATA,"设备初始数据检查");

                    if (device == null) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，设备未配置");
                        return;
                    }

                    if (device.getCabinets() == null) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，门柜未配置");
                        return;
                    }

                    if (slot == null) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，格子未配置");
                        return;
                    }

                    if (cabinet == null) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，门柜未配置");
                        return;
                    }

                    if (StringUtil.isEmpty(cabinet.getLayout())) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，门柜参数未配置");
                        return;
                    }

                    CabinetLayoutBean cabinetLayout = JSON.parseObject(cabinet.getLayout(), new TypeReference<CabinetLayoutBean>() {
                    });

                    if (cabinetLayout == null) {
                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，门柜参数格式有误");
                        return;
                    }

                    cabinetCtrl=CabinetCtrlInterface.getInstance(cabinet.getComId(),cabinet.getComBaud(),cabinet.getComPrl());

//                    if (cabinetLayout.getRfId() == null) {
//                        sendOpenHandlerMessage(MESSAGE_WHAT_INIT_DATA_FAILURE, "RFID未配置");
//                        return;
//                    }

//                    RfIdBean rfId = cabinetLayout.getRfId();
//
//                    rfIdCtrl = RfIdCtrlInterface.getInstance("ttyS4",115200,"Prl_A1");
//
//                    if (rfIdCtrl == null) {
//                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，设备连接失败");
//                        return;
//                    }
//
//
//
//                    rfIdCtrl.setReadHandler(new IRfIdCtrl.OnReadHandlerListener() {
//                        @Override
//                        public void onData(List<String> rfIds) {
//                            open_RfIds = rfIds;
//                        }
//                    });
//
//
//                    if (!rfIdCtrl.sendRead()) {
//                        sendOpenHandlerMessage(ACTION_CODE_INIT_DATA_FAILURE, "打开失败，设备命令发送失败");
//                        return;
//                    }
//
//
                    Thread.sleep(1000);


                    //rfIdCtrl=RfIdCtrlInterface.getInstance()


                    cabinetCtrl = CabinetCtrlInterface.getInstance(cabinet.getComId(), cabinet.getComBaud(), cabinet.getComPrl());

                    HashMap<String,Object> actionData=new HashMap<>();


                    open_RfIds.add("123456789012345678901410");
                    open_RfIds.add("123456789012345678901409");
                    open_RfIds.add("123456789012345678901408");
                    open_RfIds.add("123456789012345678901407");
                    //open_RfIds.add("123456789012345678901406");

                    actionData.put("rfIds",open_RfIds);

                    sendOpenHandlerMessage(ACTION_CODE_REQUEST_OPEN_AUTH,actionData, "请求是否允许打开设备");


                } catch (InterruptedException e) {
                    sendOpenHandlerMessage(ACTION_CODE_EXCEPTION,"发生异常");
                }

            }
        }).start();


    }

    public void bookerBorrowReturn(String actionCode, HashMap<String,Object> actionData,String actionRemark, ReqHandler reqHandler) {

        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setDeviceId(device == null ? null : device.getDeviceId());
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setActionRemark(actionRemark);
        rop.setFlowId(flowId);
        if (actionData != null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }
        ReqInterface.getInstance().bookerBorrowReturn(rop, reqHandler);

    }

    private void sendOpenHandlerMessage(int what,String actionRemark) {
        sendOpenHandlerMessage(what, null, actionRemark);
    }

    private void sendOpenHandlerMessage(int actionCode,HashMap<String,Object> actionData,String actionRemark ) {

        switch (actionCode) {
            case ACTION_CODE_FLOW_START:
                bookerBorrowReturn("flow_start", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA:
                bookerBorrowReturn("init_data", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA_SUCCESS:
                bookerBorrowReturn("init_data_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_INIT_DATA_FAILURE:
                bookerBorrowReturn("init_data_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH:
                bookerBorrowReturn("request_open_auth", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            sendOpenHandlerMessage(ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS, "请求允许打开设备");
                        } else {
                            sendOpenHandlerMessage(ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendOpenHandlerMessage(ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE, "请求不允许打开设备");
                    }
                });
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH_SUCCESS:
                bookerBorrowReturn("request_open_auth_success", actionData, actionRemark, null);
                sendOpenHandlerMessage(ACTION_CODE_SEND_OPEN_COMMAND, "发送打开命令");
                break;
            case ACTION_CODE_REQUEST_OPEN_AUTH_FAILURE:
                bookerBorrowReturn("request_open_auth_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND:
                bookerBorrowReturn("send_open_command", actionData, actionRemark, null);
                cabinetCtrl.open("1", new ICabinetCtrl.OnListener() {
                    @Override
                    public void onSendCommandSuccess() {
                        sendOpenHandlerMessage(ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS, "打开命令发送成功");
                    }

                    @Override
                    public void onSendCommnadFailure() {
                        sendOpenHandlerMessage(ACTION_CODE_SEND_OPEN_COMMAND_FAILURE, "打开命令发送失败");
                    }

                    @Override
                    public void onOpenSuccess() {
                        sendOpenHandlerMessage(ACTION_CODE_OPEN_SUCCESS, "打开成功");


//                        rfIdCtrl.setReadHandler(new IRfIdCtrl.OnReadHandlerListener() {
//                            @Override
//                            public void onData(List<String> rfIds) {
//                                close_RfIds = rfIds;
//                            }
//                        });

                        try {
                            Thread.sleep(1000);
                        } catch (Exception ex) {

                        }



                        close_RfIds.add("123456789012345678901408");
                        close_RfIds.add("123456789012345678901407");
                        close_RfIds.add("123456789012345678901406");


                        HashMap<String, Object> actionData = new HashMap<>();
                        actionData.put("rfIds", close_RfIds);

                        sendOpenHandlerMessage(ACTION_CODE_CLOSE_SUCCESS, actionData, "关闭成功");
                    }

                    @Override
                    public void onOpenFailure() {
                        sendOpenHandlerMessage(ACTION_CODE_OPEN_FAILURE, "打开失败");
                    }
                });
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND_SUCCESS:
                bookerBorrowReturn("send_open_command_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_SEND_OPEN_COMMAND_FAILURE:
                bookerBorrowReturn("send_open_command_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_OPEN_SUCCESS:
                bookerBorrowReturn("open_success", actionData, actionRemark, null);
                break;
            case ACTION_CODE_OPEN_FAILURE:
                bookerBorrowReturn("open_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_CLOSE_SUCCESS:
                bookerBorrowReturn("close_success", actionData, actionRemark, null);
                sendOpenHandlerMessage(ACTION_CODE_REQUEST_CLOSE_AUTH, actionData, "请求是否允许关闭");
                break;
            case ACTION_CODE_CLOSE_FAILURE:
                bookerBorrowReturn("close_failure", actionData, actionRemark, null);
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH:
                bookerBorrowReturn("request_close_auth", actionData, actionRemark, new ReqHandler() {
                    @Override
                    public void onSuccess(String response) {
                        super.onSuccess(response);
                        ResultBean<RetBookerBorrowReturn> rt = JSON.parseObject(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
                        });

                        if (rt.getCode() == ResultCode.SUCCESS) {
                            RetBookerBorrowReturn d = rt.getData();
                            HashMap<String, Object> m_ActionData = new HashMap<>();
                            m_ActionData.put("ret_booker_borrow_return", d);
                            sendOpenHandlerMessage(ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS, m_ActionData, "请求关闭验证通过");
                        } else {
                            sendOpenHandlerMessage(ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        super.onFailure(msg, e);
                        sendOpenHandlerMessage(ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE, "关闭验证不通过");
                    }
                });
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH_SUCCESS:
                bookerBorrowReturn("request_close_auth_success", actionData, actionRemark, null);
                sendOpenHandlerMessage(ACTION_CODE_FLOW_END, actionData, "借还结束");
                break;
            case ACTION_CODE_REQUEST_CLOSE_AUTH_FAILURE:
                bookerBorrowReturn("request_close_auth_failure", actionData, actionRemark, null);

                break;
            case ACTION_CODE_FLOW_END:
                openIsRunning = false;
                bookerBorrowReturn("flow_end", actionData, actionRemark, null);
                reSet();
                break;
            case ACTION_CODE_EXCEPTION:
                bookerBorrowReturn("exception", actionData, actionRemark, null);
                break;
        }

        if (onOpenHandlerListener != null) {
            onOpenHandlerListener.onHandle(actionCode, actionData, actionRemark);
        }
    }

    private OnOpenHandlerListener onOpenHandlerListener=null;

    public void setOpenHandlerListener(OnOpenHandlerListener onOpenHandlerListener) {
        this.onOpenHandlerListener = onOpenHandlerListener;
    }

    public  interface OnOpenHandlerListener {
        void onHandle(int actionCode, HashMap<String, Object> actionData, String actionRemark);
    }
}
