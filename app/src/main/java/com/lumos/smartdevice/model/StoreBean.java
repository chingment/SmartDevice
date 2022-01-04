package com.lumos.smartdevice.model;

import java.io.Serializable;

public class StoreBean implements Serializable {

    private String nameK;
    private String nameV;

    public String getNameK() {
        return nameK;
    }

    public void setNameK(String nameK) {
        this.nameK = nameK;
    }

    public String getNameV() {
        return nameV;
    }

    public void setNameV(String nameV) {
        this.nameV = nameV;
    }
}
