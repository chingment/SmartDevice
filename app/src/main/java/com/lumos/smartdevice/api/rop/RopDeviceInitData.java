package com.lumos.smartdevice.api.rop;

public class RopDeviceInitData {

    private String deviceId;
    private String macAddr;
    private String imeiId;
    private String sceneMode;
    private String vesionMode;
    private String appVerCode;
    private String appVerName;

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

    public String getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(String appVerCode) {
        this.appVerCode = appVerCode;
    }

    public String getAppVerName() {
        return appVerName;
    }

    public void setAppVerName(String appVerName) {
        this.appVerName = appVerName;
    }
}
