package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.DeviceVo;

import java.io.Serializable;

public class RetDeviceInitData implements Serializable {
    private DeviceVo device;

    public DeviceVo getDevice() {
        return device;
    }

    public void setDevice(DeviceVo device) {
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
