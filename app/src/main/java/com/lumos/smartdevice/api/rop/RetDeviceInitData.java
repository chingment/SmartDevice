package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.DeviceBean;

import java.io.Serializable;

public class RetDeviceInitData implements Serializable {
    private DeviceBean device;

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    private Object customData;

    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(Object customData) {
        this.customData = customData;
    }
}
