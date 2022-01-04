package com.lumos.smartdevice.api.rop;

import java.io.Serializable;

public class RopOwnLoginByAccount implements Serializable {
    private String deviceId;
    private String userName;
    private String password;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
