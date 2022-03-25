package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.LockerBoxUsageVo;

import java.util.List;

public class RetLockerGetBox {

    private String deviceId;
    private String cabinetId;
    private String slotId;
    private boolean isUsed;
    private int type;
    private int height;
    private int width;

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


    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
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
}
