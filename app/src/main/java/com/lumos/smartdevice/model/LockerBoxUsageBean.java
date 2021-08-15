package com.lumos.smartdevice.model;

import java.io.Serializable;

public class LockerBoxUsageBean implements Serializable {

    private String deviceId;
    private String cabinetId;
    private String slotId;
    private String usageType;
    private String usageData;
    private String usageCumstom;

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

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public String getUsageData() {
        return usageData;
    }

    public void setUsageData(String usageData) {
        this.usageData = usageData;
    }

    public String getUsageCumstom() {
        return usageCumstom;
    }

    public void setUsageCumstom(String usageCumstom) {
        this.usageCumstom = usageCumstom;
    }
}
