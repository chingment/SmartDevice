package com.lumos.smartdevice.devicectrl;

import android.os.Bundle;
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
import com.lumos.smartdevice.model.CabinetSlotBean;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.HashMap;
import java.util.UUID;

public class BookerBorrowReturnInterface {

    private static final String TAG = "LockerBoxCtrlByDs";

    private static BookerBorrowReturnInterface mInterface= null;

    public static final int MESSAGE_WHAT_OPEN_REQUEST = 1;
    public static final int MESSAGE_WHAT_OPEN_REQUEST_SUCCESS = 2;
    public static final int MESSAGE_WHAT_OPEN_REQUEST_FAILURE = 3;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND = 4;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND_SUCCESS = 5;
    public static final int MESSAGE_WHAT_SEND_OPEN_COMMAND_FAILURE = 6;
    public static final int MESSAGE_WHAT_OPEN_SUCCESS= 7;
    public static final int MESSAGE_WHAT_OPEN_FAILURE = 8;

    private DeviceBean device;
    private CabinetSlotBean cabinetSlot;
    private String clientUserId;
    private int identityType;
    private String identityId;
    private String trgId;
    private String flowId;


    private Handler openHandler = null;


    private ILockerBoxCtrl lockerBoxCtrl;

    public static BookerBorrowReturnInterface getInstance() {

        mInterface = new BookerBorrowReturnInterface();

        return mInterface;
    }

    private void setFlowId(String flowId) {
        this.flowId = flowId;
    }



    public void open(DeviceBean device,CabinetSlotBean cabinetSlot,String clientUserId,int identityType,String  identityId) {

        this.device = device;
        this.cabinetSlot = cabinetSlot;
        this.clientUserId = clientUserId;
        this.identityType = identityType;
        this.identityId = identityId;
        this.trgId = UUID.randomUUID().toString().replace("-", "");

        CabinetBean cabinet = device.getCabinets().get(cabinetSlot.getCabinetId());

        lockerBoxCtrl = LockerBoxInterface.getInstance(cabinet.getComId(), cabinet.getComBaud(), cabinet.getComPrl());

        sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_REQUEST);

        bookerBorrowReturn("open_request", null, new ReqHandler() {
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

    }

    public void bookerBorrowReturn(String actionCode, HashMap<String,String> actionData, ReqHandler reqHandler) {

        RopBookerBorrowReturn rop = new RopBookerBorrowReturn();
        rop.setDeviceId(device.getDeviceId());
        rop.setCabinetId(cabinetSlot.getCabinetId());
        rop.setSlotId(cabinetSlot.getSlotId());
        rop.setClientUserId(clientUserId);
        rop.setIdentityType(identityType);
        rop.setIdentityId(identityId);
        rop.setActionCode(actionCode);
        rop.setActionTime(CommonUtil.getCurrentTime());
        rop.setFlowId(flowId);
        rop.setTrgId(trgId);
        if(actionData!=null) {
            rop.setActionData(JSON.toJSONString(actionData));
        }
        ReqInterface.getInstance().bookerBorrowReturn(rop, reqHandler);

    }

    public void setOpenHandler(Handler openHandler) {
        this.openHandler = openHandler;
    }

    private void sendOpenHandlerMessage(int what) {

        switch (what) {
            case MESSAGE_WHAT_OPEN_REQUEST_SUCCESS:
                bookerBorrowReturn("open_request_success", null, null);
                sendOpenHandlerMessage(MESSAGE_WHAT_SEND_OPEN_COMMAND);
                break;
            case MESSAGE_WHAT_OPEN_REQUEST_FAILURE:
                bookerBorrowReturn("open_request_failure", null, null);
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND:
                bookerBorrowReturn("send_open_command", null, null);
                lockerBoxCtrl.open("1", new ILockerBoxCtrl.OnListener() {
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
                    }

                    @Override
                    public void onOpenFailure() {
                        sendOpenHandlerMessage(MESSAGE_WHAT_OPEN_FAILURE);
                    }
                });
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND_SUCCESS:
                bookerBorrowReturn("send_open_command_success", null, null);
                break;
            case MESSAGE_WHAT_SEND_OPEN_COMMAND_FAILURE:
                bookerBorrowReturn("send_open_command_failure", null, null);
                break;
            case MESSAGE_WHAT_OPEN_SUCCESS:
                bookerBorrowReturn("open_success", null, null);
                break;
            case MESSAGE_WHAT_OPEN_FAILURE:
                bookerBorrowReturn("open_failure", null, null);
                break;
        }

        if (openHandler != null) {
            Message m = new Message();
            m.what = what;
            openHandler.sendMessage(m);
        }
    }

}
