package com.lumos.smartdevice.model;

import com.alibaba.fastjson.annotation.JSONField;

public class CabinetBean {
    @JSONField(name="cabinet_id")
    private String cabinetId;
    @JSONField(name="name")
    private String name;
    @JSONField(name="com_id")
    private String comId;
    @JSONField(name="com_baud")
    private int comBaud;
    @JSONField(name="com_prl")
    private String comPrl;
    @JSONField(name="layout")
    private String layout;

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getComPrl() {
        return comPrl;
    }

    public void setComPrl(String comPrl) {
        this.comPrl = comPrl;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getComBaud() {
        return comBaud;
    }

    public void setComBaud(int comBaud) {
        this.comBaud = comBaud;
    }
}
