package com.lumos.smartdevice.api.rop;

import java.util.List;

public class RopBookerBorrowReturnCloseAction {

    private String deviceId;
    private String flowId;
    private String actionCode;
    private int actionResult;
    private String actionTime;
    private List<String> rfIds;

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

    public int getActionResult() {
        return actionResult;
    }

    public void setActionResult(int actionResult) {
        this.actionResult = actionResult;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public List<String> getRfIds() {
        return rfIds;
    }

    public void setRfIds(List<String> rfIds) {
        this.rfIds = rfIds;
    }
}
