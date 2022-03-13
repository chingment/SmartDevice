package com.lumos.smartdevice.devicectrl;

import java.io.Serializable;

public class TagInfo implements Serializable {
    private Long index;
    private String type;
    private String epc;
    private Long count;
    private String tid;
    private String userData;
    private String rssi;

    public TagInfo() {
    }

    public TagInfo(Long index, String type, String epc, String tid, String rssi) {
        this.index = index;
        this.type = type;
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    @Override
    public String toString() {
        return "TagInfo{" +
                "index=" + index +
                ", type='" + type + '\'' +
                ", epc='" + epc + '\'' +
                ", user='" + tid + '\'' +
                ", rssi='" + rssi + '\'' +
                '}';
    }
}
