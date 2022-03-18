package com.lumos.smartdevice.model;

import java.io.Serializable;

public class BookerSlotDriveBean implements Serializable {
    private BookerDriveLockeqBean lockeq;
    private BookerDriveRfeqBean rfeq;

    public BookerDriveLockeqBean getLockeq() {
        return lockeq;
    }

    public void setLockeq(BookerDriveLockeqBean lockeq) {
        this.lockeq = lockeq;
    }

    public BookerDriveRfeqBean getRfeq() {
        return rfeq;
    }

    public void setRfeq(BookerDriveRfeqBean rfeq) {
        this.rfeq = rfeq;
    }
}
