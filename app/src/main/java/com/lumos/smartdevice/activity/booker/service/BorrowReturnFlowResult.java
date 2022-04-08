package com.lumos.smartdevice.activity.booker.service;

import java.io.Serializable;
import java.util.HashMap;


public class BorrowReturnFlowResult implements Serializable {
    private String deviceId;
    private String flowId;
    private String actionCode;
    private HashMap<String,Object> actionData;
    private String actionRemark;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }


    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public HashMap<String,Object> getActionData() {
        return actionData;
    }

    public void setActionData(HashMap<String,Object> actionData) {
        this.actionData = actionData;
    }

    public String getActionRemark() {
        return actionRemark;
    }

    public void setActionRemark(String actionRemark) {
        this.actionRemark = actionRemark;
    }

}
