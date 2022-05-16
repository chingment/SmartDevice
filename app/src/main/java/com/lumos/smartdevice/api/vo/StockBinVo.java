package com.lumos.smartdevice.api.vo;

public class StockBinVo {
    private String slotId;
    private String name;
    private int quantity;
    private String lastTakeStockTime;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLastTakeStockTime() {
        return lastTakeStockTime;
    }

    public void setLastTakeStockTime(String lastTakeStockTime) {
        this.lastTakeStockTime = lastTakeStockTime;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
