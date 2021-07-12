package com.lumos.smartdevice.api.rop;

public class RopDeviceInitData {

    private String deviceId;
    private String sceneMode;
    private String vesionMode;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(String sceneMode) {
        this.sceneMode = sceneMode;
    }

    public String getVesionMode() {
        return vesionMode;
    }

    public void setVesionMode(String vesionMode) {
        this.vesionMode = vesionMode;
    }
}
