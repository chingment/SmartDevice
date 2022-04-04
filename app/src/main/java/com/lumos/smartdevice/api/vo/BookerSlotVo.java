package com.lumos.smartdevice.api.vo;

import java.io.Serializable;


public class BookerSlotVo implements Serializable {
    private String slotId;
    private String name;
    private String lockeqId;
    private String lockeqAnt;
    private String rfeqId;
    private String rfeqAnt;

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockeqId() {
        return lockeqId;
    }

    public void setLockeqId(String lockeqId) {
        this.lockeqId = lockeqId;
    }

    public String getLockeqAnt() {
        return lockeqAnt;
    }

    public void setLockeqAnt(String lockeqAnt) {
        this.lockeqAnt = lockeqAnt;
    }

    public String getRfeqId() {
        return rfeqId;
    }

    public void setRfeqId(String rfeqId) {
        this.rfeqId = rfeqId;
    }

    public String getRfeqAnt() {
        return rfeqAnt;
    }

    public void setRfeqAnt(String rfeqAnt) {
        this.rfeqAnt = rfeqAnt;
    }
}
