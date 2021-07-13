package com.lumos.smartdevice.model.api;

import java.io.Serializable;

public class OwnLogoutResultBean implements Serializable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
