package com.lumos.smartdevice.api.rop;

public class RopIdentityInfo {
    private String deviceId;
    private int identityType;
    private String identityId;
    private int sceneMode;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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


    public int getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(int sceneMode) {
        this.sceneMode = sceneMode;
    }
}
