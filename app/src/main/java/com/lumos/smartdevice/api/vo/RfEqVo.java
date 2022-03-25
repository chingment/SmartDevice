package com.lumos.smartdevice.api.vo;

public class RfEqVo {
    private String rfEqId;
    private String comId;
    private int comBaud;
    private String comPrl;

    public String getRfEqId() {
        return rfEqId;
    }

    public void setRfEqId(String rfEqId) {
        this.rfEqId = rfEqId;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public int getComBaud() {
        return comBaud;
    }

    public void setComBaud(int comBaud) {
        this.comBaud = comBaud;
    }

    public String getComPrl() {
        return comPrl;
    }

    public void setComPrl(String comPrl) {
        this.comPrl = comPrl;
    }
}
