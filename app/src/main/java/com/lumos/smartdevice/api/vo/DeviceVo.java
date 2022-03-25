package com.lumos.smartdevice.api.vo;

import java.io.Serializable;
import java.util.HashMap;

public class DeviceVo implements Serializable {
    private String deviceId;
    private String name;
    private int sceneMode;
    private int versionMode;
    private String merchId;
    private HashMap<String, DriveVo> drives;
    private MqttBean mqtt;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(int sceneMode) {
        this.sceneMode = sceneMode;
    }

    public int getVersionMode() {
        return versionMode;
    }

    public void setVersionMode(int versionMode) {
        this.versionMode = versionMode;
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public HashMap<String, DriveVo> getDrives() {
        return drives;
    }

    public void setDrives(HashMap<String, DriveVo> drives) {
        this.drives = drives;
    }

    public MqttBean getMqtt() {
        return mqtt;
    }

    public void setMqtt(MqttBean mqtt) {
        this.mqtt = mqtt;
    }
}
