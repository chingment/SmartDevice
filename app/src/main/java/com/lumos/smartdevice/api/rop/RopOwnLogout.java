package com.lumos.smartdevice.api.rop;

import java.io.Serializable;

public class RopOwnLogout  implements Serializable {
    private String deviceId;
    private String userId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
