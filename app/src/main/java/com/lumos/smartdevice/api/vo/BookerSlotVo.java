package com.lumos.smartdevice.api.vo;

import java.io.Serializable;


public class BookerSlotVo implements Serializable {
    private String slotId;
    private String name;
    private BookerSlotDrivesVo drives;

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

    public BookerSlotDrivesVo getDrives() {
        return drives;
    }

    public void setDrives(BookerSlotDrivesVo drives) {
        this.drives = drives;
    }
}
