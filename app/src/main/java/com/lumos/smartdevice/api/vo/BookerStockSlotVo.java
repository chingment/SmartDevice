package com.lumos.smartdevice.api.vo;

public class BookerStockSlotVo {
    private String slotId;
    private String name;
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
