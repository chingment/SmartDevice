package com.lumos.smartdevice.api.rop;

import java.util.List;

public class RopBookerRenewBooks {
    private String deviceId;
    private String clientUserId;
    private int identityType;
    private String identityId;
    private String actionCode;
    private List<String> borrowIds;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(String clientUserId) {
        this.clientUserId = clientUserId;
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

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public List<String> getBorrowIds() {
        return borrowIds;
    }

    public void setBorrowIds(List<String> borrowIds) {
        this.borrowIds = borrowIds;
    }
}
