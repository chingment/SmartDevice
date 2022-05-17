package com.lumos.smartdevice.api.vo;

import java.io.Serializable;


public class BookerSlotVo implements Serializable {
    private String slotId;
    private String name;
    private String lockeqId;
    private String lockeqAnt;
    private String rfeqId;
    private String rfeqAnt;

    private int stockQuantity;
    private String lastTakeTime;
    private String lastTakeQuantity;
    private boolean isOpen;

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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getLastTakeTime() {
        return lastTakeTime;
    }

    public void setLastTakeTime(String lastTakeTime) {
        this.lastTakeTime = lastTakeTime;
    }

    public String getLastTakeQuantity() {
        return lastTakeQuantity;
    }

    public void setLastTakeQuantity(String lastTakeQuantity) {
        this.lastTakeQuantity = lastTakeQuantity;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
