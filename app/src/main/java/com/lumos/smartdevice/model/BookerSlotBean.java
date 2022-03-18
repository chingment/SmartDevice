package com.lumos.smartdevice.model;

import java.io.Serializable;


public class BookerSlotBean implements Serializable {
    private String slotId;
    private String name;
    private BookerSlotDrivesBean drives;

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

    public BookerSlotDrivesBean getDrives() {
        return drives;
    }

    public void setDrives(BookerSlotDrivesBean drives) {
        this.drives = drives;
    }
}
