package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.HashMap;

public class DeviceBean implements Serializable {
    private String deviceId;
    private String sceneMode;
    private String versionMode;

    private HashMap<String, CabinetBean> cabinets;
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

    public String getVersionMode() {
        return versionMode;
    }

    public void setVersionMode(String versionMode) {
        this.versionMode = versionMode;
    }

    public HashMap<String, CabinetBean> getCabinets() {
        return cabinets;
    }

    public void setCabinets(HashMap<String, CabinetBean> cabinets) {
        this.cabinets = cabinets;
    }
}
