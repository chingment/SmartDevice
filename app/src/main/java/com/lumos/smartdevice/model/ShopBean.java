package com.lumos.smartdevice.model;

import java.io.Serializable;

public class ShopBean implements Serializable {

    private String nameK;
    private String nameV;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
