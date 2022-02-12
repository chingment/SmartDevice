package com.lumos.smartdevice.model;

import java.io.Serializable;

public class CabinetBoxBean implements Serializable {
    private String cabinetId;
    private String boxId;
    private String boxName;
    private String boxPlate;

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getBoxPlate() {
        return boxPlate;
    }

    public void setBoxPlate(String boxPlate) {
        this.boxPlate = boxPlate;
    }
}
