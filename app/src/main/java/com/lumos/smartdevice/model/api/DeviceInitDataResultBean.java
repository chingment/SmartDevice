package com.lumos.smartdevice.model.api;

import com.lumos.smartdevice.model.DeviceBean;

import java.io.Serializable;

public class DeviceInitDataResultBean implements Serializable {
    private DeviceBean device;

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }
}
