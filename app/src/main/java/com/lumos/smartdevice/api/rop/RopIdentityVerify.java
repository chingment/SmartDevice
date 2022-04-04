package com.lumos.smartdevice.api.rop;

public class RopIdentityVerify {
    private String deviceId;
    private int verifyMode;//1 iccard
    private String payload;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
