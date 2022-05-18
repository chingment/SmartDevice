package com.lumos.smartdevice.api.rop;

public class RopBookerCreateFlow {
    private String deviceId;
    private String slotId;
    private String flowUserId;
    private int flowType;
    private int identityType;
    private String identityId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public int getIdentityType() {
        return identityType;
    }

    public void setIdentityType(int identityType) {
        this.identityType = identityType;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getFlowUserId() {
        return flowUserId;
    }

    public void setFlowUserId(String flowUserId) {
        this.flowUserId = flowUserId;
    }

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
    }
}
