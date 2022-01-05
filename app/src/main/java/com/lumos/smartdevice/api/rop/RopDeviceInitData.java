package com.lumos.smartdevice.api.rop;

public class RopDeviceInitData {

    private String deviceId;
    private String imeiId;
    private String macAddr;
    private String appVerCode;
    private String appVerName;
    private String sysVerName;
    private String ctrlVerName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getImeiId() {
        return imeiId;
    }

    public void setImeiId(String imeiId) {
        this.imeiId = imeiId;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(String appVerCode) {
        this.appVerCode = appVerCode;
    }

    public String getAppVerName() {
        return appVerName;
    }

    public void setAppVerName(String appVerName) {
        this.appVerName = appVerName;
    }

    public String getSysVerName() {
        return sysVerName;
    }

    public void setSysVerName(String sysVerName) {
        this.sysVerName = sysVerName;
    }

    public String getCtrlVerName() {
        return ctrlVerName;
    }

    public void setCtrlVerName(String ctrlVerName) {
        this.ctrlVerName = ctrlVerName;
    }
}
