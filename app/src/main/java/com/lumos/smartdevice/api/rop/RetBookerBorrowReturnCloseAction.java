package com.lumos.smartdevice.api.rop;

public class RetBookerBorrowReturnCloseAction {

    private String deviceId;
    private String flowId;
    private String actionCode;
    private String actionResult;
    private String actionTime;
    private String rfIds;

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

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getRfIds() {
        return rfIds;
    }

    public void setRfIds(String rfIds) {
        this.rfIds = rfIds;
    }
}
