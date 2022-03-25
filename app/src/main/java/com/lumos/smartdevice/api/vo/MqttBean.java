package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class MqttBean implements Serializable {

    private String type;
    private Object params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

}
