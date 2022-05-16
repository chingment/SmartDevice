package com.lumos.smartdevice.api.rop;

public class RopBookerBorrowReturn {
    private String msgId;
    private String msgMode;
    private String deviceId;
    private String actionCode;
    private int actionSn;
    private String actionData;
    private String actionTime;
    private String actionRemark;
    private String flowId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgMode() {
        return msgMode;
    }

    public void setMsgMode(String msgMode) {
        this.msgMode = msgMode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public int getActionSn() {
        return actionSn;
    }

    public void setActionSn(int actionSn) {
        this.actionSn = actionSn;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionRemark() {
        return actionRemark;
    }

    public void setActionRemark(String actionRemark) {
        this.actionRemark = actionRemark;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }
}
