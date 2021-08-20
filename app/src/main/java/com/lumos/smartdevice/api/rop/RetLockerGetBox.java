package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.LockerBoxUsageBean;

import java.util.List;

public class RetLockerGetBox {

    private String deviceId;
    private String cabinetId;
    private String slotId;
    private String isUsed;
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
}
