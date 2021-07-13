package com.lumos.smartdevice.api.rop;

import java.io.Serializable;

public class RopOwnLogout  implements Serializable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
