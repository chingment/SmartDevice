package com.lumos.smartdevice.model;

import java.io.Serializable;

public class BookerSlotBean implements Serializable {
    private String slotId;
    private String name;
    private BookerSlotDriveBean drive;

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

    public BookerSlotDriveBean getDrive() {
        return drive;
    }

    public void setDrive(BookerSlotDriveBean drive) {
        this.drive = drive;
    }
}
