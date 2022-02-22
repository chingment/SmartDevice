package com.lumos.smartdevice.api.rop;

import java.io.Serializable;

public class RetIdentityInfo implements Serializable {

    private int sceneMode;
    private Object info;

    public int getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(int sceneMode) {
        this.sceneMode = sceneMode;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }



}
