package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class LockerBoxBean implements Serializable {

    private String deviceId;
    private String cabinetId;
    private String slotId;
    private int type;
    private String isUsed;
    private int height;
    private int width;
    private List<LockerBoxUsageBean> usages;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public List<LockerBoxUsageBean> getUsages() {
        return usages;
    }

    public void setUsages(List<LockerBoxUsageBean> usages) {
        this.usages = usages;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
