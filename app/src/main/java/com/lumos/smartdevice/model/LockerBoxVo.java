package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class LockerBoxVo implements Serializable {

    private String deviceId;
    private String cabinetId;
    private String slotId;
    private int type;
    private boolean isUsed;
    private int height;
    private int width;
    private boolean isOpen;

    private List<LockerBoxUsageVo> usages;

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


    public List<LockerBoxUsageVo> getUsages() {
        return usages;
    }

    public void setUsages(List<LockerBoxUsageVo> usages) {
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

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
