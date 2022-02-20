package com.lumos.smartdevice.model;

import java.io.Serializable;

public class CabinetSlotBean implements Serializable {
    private String cabinetId;
    private String slotId;
    private String slotName;
    private String slotPlate;

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotPlate() {
        return slotPlate;
    }

    public void setSlotPlate(String slotPlate) {
        this.slotPlate = slotPlate;
    }
}
