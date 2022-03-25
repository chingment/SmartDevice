package com.lumos.smartdevice.api.rop;

import com.alibaba.fastjson.annotation.JSONField;
import com.lumos.smartdevice.api.vo.LockerBoxVo;

import java.io.Serializable;
import java.util.List;

public class RetLockerGetCabinet implements Serializable {

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
    @JSONField(name="boxs")
    private List<LockerBoxVo> boxs;

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

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<LockerBoxVo> getBoxs() {
        return boxs;
    }

    public void setBoxs(List<LockerBoxVo> boxs) {
        this.boxs = boxs;
    }


}
